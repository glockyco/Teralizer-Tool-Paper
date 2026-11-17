#!/usr/bin/env bash
#
# Runs Teralizer on a copy of demo/loan and refreshes demo/output, which holds
# everything the paper shows about the example. Afterwards it runs the generalized
# tests against a copy of the example with the refactored method of demo/refactoring,
# which returns a different amount for a score of 80, and all tests in a copy whose
# build file only adds jqwik.
#
# Needs Docker and the `teralizer` image, built in the Teralizer repository with
# `docker build -t teralizer .`. The run uses its own PostgreSQL container and
# network, so it never touches the databases of a Teralizer checkout.
#
# Usage: demo/run.sh
#   TERALIZER_IMAGE  image to run (default: teralizer)
set -euo pipefail
export MSYS_NO_PATHCONV=1

DEMO_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd -P)
IMAGE="${TERALIZER_IMAGE:-teralizer}"
NETWORK=teralizer-tool-demo
POSTGRES=teralizer-tool-demo-postgres
DATABASE=scratch_tool_demo
WORK="$DEMO_DIR/work"
OUT="$DEMO_DIR/output"

query() {
  docker exec "$POSTGRES" psql -U teralizer -d "$DATABASE" -P pager=off -c "$1"
}

rm -rf "$WORK" "$OUT"
mkdir -p "$WORK/data" "$OUT/generated" "$OUT/specs" "$OUT/inputs"
cp -r "$DEMO_DIR/loan" "$WORK/loan"

docker network create "$NETWORK" >/dev/null 2>&1 || true
docker rm -f "$POSTGRES" >/dev/null 2>&1 || true
docker run -d --name "$POSTGRES" --network "$NETWORK" \
  -e POSTGRES_USER=teralizer -e POSTGRES_PASSWORD=teralizer postgres:17 >/dev/null
until docker exec "$POSTGRES" pg_isready -U teralizer >/dev/null 2>&1; do sleep 1; done
docker exec "$POSTGRES" createdb -U teralizer "$DATABASE"

echo "==> Running Teralizer on demo/loan"
docker run --rm --network "$NETWORK" \
  -e DB_HOST="$POSTGRES" -e DB_PORT=5432 -e DB_USER=teralizer -e DB_PASSWORD=teralizer \
  -v "$WORK/loan:/app/projects/example-loan" \
  -v "$DEMO_DIR/loan.conf:/app/project-configs/example-loan.conf:ro" \
  -v "$WORK/data:/app/data" \
  -v teralizer-tool-demo-m2:/root/.m2 \
  "$IMAGE" ./gradlew run --no-daemon \
    -Dteralizer.config=project-configs/example-loan.conf \
    -Dteralizer.database.name="$DATABASE" \
    -Dteralizer.data-dir=data \
  > "$WORK/teralizer.log" 2>&1

echo "==> Collecting demo/output"
cp "$WORK"/loan/src/test/java/bank/_*_Generalized_*_Test.java "$OUT/generated/"
cp "$WORK"/data/*/project-id-1/jpf-data/specs/*.json "$OUT/specs/"
cp "$WORK/loan/pom.teralizer.generalized.xml" "$OUT/"
# The inputs that each generalized test tried, in the order in which it tried them. The file name starts
# with the number of the generalization, which is also part of the name of its test class.
cp "$WORK"/loan/data/*/project-id-1/jqwik-data/executions/*/*.values.tsv "$OUT/inputs/"
# The processor and memory that the containers of this run had, for the runtime that the paper states.
docker run --rm "$IMAGE" sh -c \
  'grep -m1 "model name" /proc/cpuinfo; echo "cpus       : $(nproc)"; grep MemTotal /proc/meminfo' \
  > "$OUT/machine.txt"

query "
  SELECT type AS build_system,
         test_framework,
         tool_git_version,
         round(runtime::numeric) AS runtime_seconds
  FROM project;" > "$OUT/run.txt"

query "
  SELECT min(step) AS step,
         stage,
         count(*) AS tasks,
         count(*) FILTER (WHERE status <> 'SUCCEEDED') AS not_succeeded,
         round(sum(runtime)::numeric, 1) AS seconds
  FROM task
  GROUP BY stage
  ORDER BY min(step);" > "$OUT/stages.txt"

query "
  SELECT t.test_method_name AS test,
         a.tested_method_name AS tested_method,
         a.output_spec_class,
         a.output_is_literal,
         g.variant,
         g.is_included AS generalized,
         g.widening_refusal_code,
         g.used_constraint_count AS encoded_constraints,
         g.total_constraint_count AS constraints
  FROM test t
  JOIN assertion a ON a.test_id = t.id
  LEFT JOIN generalization g ON g.assertion_id = a.id
  ORDER BY t.test_method_name;" > "$OUT/outcomes.txt"

query "
  SELECT i.line_number AS line,
         regexp_replace(i.mutator, '.*\.', '') AS mutator,
         i.status AS original_suite,
         g.status AS generalized_suite,
         g.killing_class_name AS killed_by
  FROM pit_mutation_report i
  JOIN pit_mutation_report g
    ON g.stage = 'COLLECT_PIT_DATA_GENERALIZED'
   AND (g.mutated_class, g.mutated_method, g.method_description, g.line_number, g.mutator, g.indexes, g.blocks)
     = (i.mutated_class, i.mutated_method, i.method_description, i.line_number, i.mutator, i.indexes, i.blocks)
  WHERE i.stage = 'COLLECT_PIT_DATA_INITIAL'
  ORDER BY i.line_number, mutator;" > "$OUT/mutants.txt"

# The assertions that were not generalized, with the reason that the paper names for partialApproval.
query "
  SELECT t.test_method_name, g.exclusion_info,
         g.widening_refusal_code
  FROM generalization g JOIN test t ON t.id = g.test_id
  WHERE NOT g.is_included;" > "$OUT/excluded.txt"

# What scripts/try-example.sh of the Teralizer repository prints for the example, with the queries of that
# script, so that the paper can show the output. The paths of the generalized tests are relative to demo/work.
{
  echo "==> Outcome of each assertion that reached generalization"
  query "
    SELECT t.test_method_name AS test,
           a.tested_method_name AS tested_method,
           g.is_included AS generalized,
           coalesce(g.widening_refusal_code, g.exclusion_info) AS reason
    FROM test t
    JOIN assertion a ON a.test_id = t.id
    JOIN generalization g ON g.assertion_id = a.id
    ORDER BY t.test_method_name;"
  echo "==> Mutants under the original and the generalized test suite"
  query "
    SELECT regexp_replace(i.mutated_class, '.*\.', '') AS class,
           i.line_number AS line,
           regexp_replace(i.mutator, '.*\.', '') AS mutator,
           i.status AS original_suite,
           g.status AS generalized_suite
    FROM pit_mutation_report i
    JOIN pit_mutation_report g
      ON g.stage = 'COLLECT_PIT_DATA_GENERALIZED'
     AND (g.mutated_class, g.mutated_method, g.method_description, g.line_number, g.mutator, g.indexes, g.blocks)
       = (i.mutated_class, i.mutated_method, i.method_description, i.line_number, i.mutator, i.indexes, i.blocks)
    WHERE i.stage = 'COLLECT_PIT_DATA_INITIAL'
    ORDER BY class, i.line_number, mutator;"
  echo "==> Generalized tests"
  (cd "$WORK" && find loan -name '_*_Generalized_*_Test.java' | sort)
} > "$OUT/console.txt"

echo "==> Running the generalized tests against the refactored example"
cp -r "$WORK/loan" "$WORK/loan-refactored"
cp "$DEMO_DIR/refactoring/LoanPolicy.java" "$WORK/loan-refactored/src/main/java/bank/LoanPolicy.java"
docker run --rm \
  -v "$WORK/loan-refactored:/app/projects/example-loan" \
  -v teralizer-tool-demo-m2:/root/.m2 \
  -w /app/projects/example-loan \
  "$IMAGE" mvn --batch-mode --file pom.teralizer.generalized.xml -Djacoco.skip=true test \
  > "$WORK/regression.log" 2>&1 || true
grep -E 'Tests run:|expected:|TestParameters\{|^tries = ' "$WORK/regression.log" \
  | sed -E 's/, Time elapsed: [0-9.]+ s//' > "$OUT/regression.txt"
{
  echo "==> Running all tests against the refactored example"
  cat "$OUT/regression.txt"
} >> "$OUT/console.txt"

echo "==> Running all tests of the example with jqwik as the only added dependency"
mkdir -p "$WORK/loan-adoption"
cp -r "$WORK/loan/src" "$WORK/loan-adoption/src"
# The version is the one that Teralizer added to its copy of the build file.
jqwik_version=$(grep -A 1 '<artifactId>jqwik</artifactId>' "$OUT/pom.teralizer.generalized.xml" \
  | sed -n 's|.*<version>\(.*\)</version>.*|\1|p')
awk -v version="$jqwik_version" '
  /<\/dependencies>/ {
    print "        <dependency>"
    print "            <groupId>net.jqwik</groupId>"
    print "            <artifactId>jqwik</artifactId>"
    print "            <version>" version "</version>"
    print "            <scope>test</scope>"
    print "        </dependency>"
  }
  { print }' "$DEMO_DIR/loan/pom.xml" > "$WORK/loan-adoption/pom.xml"
docker run --rm \
  -v "$WORK/loan-adoption:/app/projects/example-loan" \
  -v teralizer-tool-demo-m2:/root/.m2 \
  -w /app/projects/example-loan \
  "$IMAGE" mvn --batch-mode test \
  > "$WORK/adoption.log" 2>&1 || true
{
  diff "$DEMO_DIR/loan/pom.xml" "$WORK/loan-adoption/pom.xml" || true
  grep -E 'Tests run:' "$WORK/adoption.log" | sed -E 's/, Time elapsed: [0-9.]+ s//'
} > "$OUT/adoption.txt"

echo "Done. Query the results with:"
echo "  docker exec -it $POSTGRES psql -U teralizer -d $DATABASE"
