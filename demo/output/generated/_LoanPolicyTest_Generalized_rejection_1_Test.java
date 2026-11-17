package bank;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
// Test: bank.LoanPolicyTest.rejection
// Input values: data/example-loan/project-id-1/jpf-data/specs/bank.LoanPolicyTest.rejection.1.jpf.concrete.input.json
// Input specification: data/example-loan/project-id-1/jpf-data/specs/bank.LoanPolicyTest.rejection.1.jpf.symbolic.input.json
// Output value: data/example-loan/project-id-1/jpf-data/specs/bank.LoanPolicyTest.rejection.1.jpf.concrete.output.json
// Output specification: data/example-loan/project-id-1/jpf-data/specs/bank.LoanPolicyTest.rejection.1.jpf.symbolic.output.json
class _LoanPolicyTest_Generalized_rejection_1_Test {
    /**
     * Wraps the generated tuple arbitrary so the captured concrete tuple is executed as the first
     * jqwik edge case and random exploration spends its bounded retry budget on unseen tuples.
     *
     * The random-phase state is sound for generated Teralizer properties because they run with a
     * fixed jqwik seed, shrinking is disabled, and jqwik executes properties single-threaded. If any
     * of those preconditions changes, this stateful deduplication must be revisited. The retry lives
     * inside the generator instead of using Arbitrary.filter(...), so duplicate draws do not affect
     * jqwik's discard-ratio accounting or interact with TOO_MANY_FILTER_MISSES handling.
     */
    private static class FirstValueArbitrary<T> implements net.jqwik.api.Arbitrary<T> {
        private T firstValue;

        private net.jqwik.api.Arbitrary<T> delegate;

        private static final java.util.Set<String> emittedRows = new java.util.LinkedHashSet<String>();

        /**
         * Consecutive-miss ceiling for the dedup retry. When this many draws in a row all repeat an
         * already-emitted tuple, the reachable input domain is effectively exhausted (its unseen mass
         * is negligible), so stop retrying and allow the repeat instead of spinning. This bounds the
         * cost on low-cardinality domains, where insisting on a fresh tuple every draw would burn a
         * huge retry budget on generation attempts that can never find an unseen value.
         */
        private static final int MAX_CONSECUTIVE_DEDUP_MISSES = 256;

        public FirstValueArbitrary(T firstValue, net.jqwik.api.Arbitrary<T> delegate) {
            FirstValueArbitrary.this.firstValue = firstValue;
            FirstValueArbitrary.this.delegate = delegate;
            FirstValueArbitrary.this.emittedRows.add(valueIdentity(firstValue));
        }

        public net.jqwik.api.RandomGenerator<T> generator(int genSize) {
            final net.jqwik.api.RandomGenerator<T> delegateGenerator = delegate.generator(genSize);
            return new net.jqwik.api.RandomGenerator<T>() {
                public net.jqwik.api.Shrinkable<T> next(java.util.Random random) {
                    net.jqwik.api.Shrinkable<T> candidate = delegateGenerator.next(random);
                    String candidateIdentity = valueIdentity(candidate.value());
                    int retries = 0;
                    while (emittedRows.contains(candidateIdentity) && (retries < MAX_CONSECUTIVE_DEDUP_MISSES)) {
                        retries++;
                        candidate = delegateGenerator.next(random);
                        candidateIdentity = valueIdentity(candidate.value());
                    } 
                    emittedRows.add(candidateIdentity);
                    return candidate;
                }
            };
        }

        /**
         * The engine builds the random phase through this overload; its default implementation
         * wraps the one-arg generator with probabilistic edge-case re-injection that bypasses the
         * dedup in next(...). Generated properties run EdgeCasesMode.FIRST, so every edge case has
         * already executed exactly once before the random phase - re-injection could only repeat
         * seen values. Ignore the flag and return the deduplicating generator.
         */
        public net.jqwik.api.RandomGenerator<T> generator(int genSize, boolean withEdgeCases) {
            return generator(genSize);
        }

        private String valueIdentity(T value) {
            return JqwikValueRecorder.serializeRow(((TestParameters) (value)));
        }

        public java.util.Optional<net.jqwik.api.ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
            return delegate.exhaustive(maxNumberOfSamples);
        }

        public net.jqwik.api.EdgeCases<T> edgeCases(int maxEdgeCases) {
            final java.util.List<java.util.function.Supplier<net.jqwik.api.Shrinkable<T>>> suppliers = new java.util.ArrayList<java.util.function.Supplier<net.jqwik.api.Shrinkable<T>>>();
            java.util.Set<String> edgeCaseRows = new java.util.LinkedHashSet<String>();
            addEdgeCaseIfUnseen(suppliers, edgeCaseRows, new java.util.function.Supplier<net.jqwik.api.Shrinkable<T>>() {
                public net.jqwik.api.Shrinkable<T> get() {
                    return firstValueShrinkable();
                }
            });
            for (java.util.function.Supplier<net.jqwik.api.Shrinkable<T>> supplier : delegate.edgeCases().suppliers()) {
                addEdgeCaseIfUnseen(suppliers, edgeCaseRows, supplier);
            }
            return new net.jqwik.api.EdgeCases<T>() {
                public java.util.List<java.util.function.Supplier<net.jqwik.api.Shrinkable<T>>> suppliers() {
                    return suppliers;
                }

                public java.util.Iterator<net.jqwik.api.Shrinkable<T>> iterator() {
                    java.util.List<net.jqwik.api.Shrinkable<T>> shrinkables = new java.util.ArrayList<net.jqwik.api.Shrinkable<T>>();
                    for (java.util.function.Supplier<net.jqwik.api.Shrinkable<T>> supplier : suppliers()) {
                        shrinkables.add(supplier.get());
                    }
                    return shrinkables.iterator();
                }
            };
        }

        private void addEdgeCaseIfUnseen(java.util.List<java.util.function.Supplier<net.jqwik.api.Shrinkable<T>>> suppliers, java.util.Set<String> edgeCaseRows, java.util.function.Supplier<net.jqwik.api.Shrinkable<T>> supplier) {
            final net.jqwik.api.Shrinkable<T> shrinkable = supplier.get();
            if (!edgeCaseRows.add(valueIdentity(shrinkable.value()))) {
                return;
            }
            emittedRows.add(valueIdentity(shrinkable.value()));
            suppliers.add(new java.util.function.Supplier<net.jqwik.api.Shrinkable<T>>() {
                public net.jqwik.api.Shrinkable<T> get() {
                    return shrinkable;
                }
            });
        }

        private net.jqwik.api.Shrinkable<T> firstValueShrinkable() {
            return new net.jqwik.engine.properties.shrinking.Unshrinkable<T>(new java.util.function.Supplier<T>() {
                public T get() {
                    return firstValue;
                }
            }, net.jqwik.api.ShrinkingDistance.MIN);
        }
    }

    public static class JqwikValueRecorder {
        private enum DiagnosticsMode {

            IN_MEMORY_ONLY,
            PERSISTED;}

        private static final long PROJECT_ID = 1L;

        private static final long GENERALIZATION_ID = 1L;

        private static final String VARIANT = "IMPROVED_200_TRIES";

        private static final String TEST_CASE_NAME = "rejection";

        private static final java.nio.file.Path BASE_DIRECTORY = java.nio.file.Paths.get("data/example-loan/project-id-1/jqwik-data");

        private static String seedRow = null;

        private static boolean sawDistinctNonSeedTuple = false;

        private static final java.util.List<String> rows = new java.util.ArrayList<String>();

        private static final java.util.Set<String> distinctRows = new java.util.LinkedHashSet<String>();

        public static synchronized void reset() {
            seedRow = null;
            sawDistinctNonSeedTuple = false;
            rows.clear();
            distinctRows.clear();
            if (diagnosticsMode() == DiagnosticsMode.PERSISTED) {
                try {
                    java.nio.file.Files.createDirectories(executionDirectory());
                    java.nio.file.Files.deleteIfExists(valueLogPath());
                    java.nio.file.Files.deleteIfExists(outcomePath());
                } catch (java.io.IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public static synchronized void record(final TestParameters parameters) {
            String serialized = serializeRow(parameters);
            if (seedRow == null) {
                seedRow = serialized;
            } else if (!seedRow.equals(serialized)) {
                sawDistinctNonSeedTuple = true;
            }
            if (diagnosticsMode() == DiagnosticsMode.PERSISTED) {
                rows.add(serialized);
                distinctRows.add(serialized);
            }
        }

        private static String serializeRow(final TestParameters parameters) {
            try {
                StringBuilder row = new StringBuilder();
                for (java.lang.reflect.Field field : TestParameters.class.getDeclaredFields()) {
                    if (field.isSynthetic() || field.getName().startsWith("$")) {
                        continue;
                    }
                    if (row.length() > 0) {
                        row.append('\t');
                    }
                    field.setAccessible(true);
                    row.append(field.getName()).append('=').append(escapeValue(field.get(parameters)));
                }
                return row.toString();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        // Value rows are tab-separated; escape control and surrogate characters so each jqwik trial stays one valid UTF-8 row.
        private static String escapeValue(Object value) {
            String raw = String.valueOf(value);
            StringBuilder escaped = new StringBuilder(raw.length());
            for (int i = 0; i < raw.length(); i++) {
                char ch = raw.charAt(i);
                switch (ch) {
                    case '\\' :
                        escaped.append("\\\\");
                        break;
                    case '\n' :
                        escaped.append("\\n");
                        break;
                    case '\r' :
                        escaped.append("\\r");
                        break;
                    case '\t' :
                        escaped.append("\\t");
                        break;
                    default :
                        if (Character.isISOControl(ch) || Character.isSurrogate(ch)) {
                            escaped.append(String.format("\\u%04x", ((int) (ch))));
                        } else {
                            escaped.append(ch);
                        }
                }
            }
            return escaped.toString();
        }

        private static DiagnosticsMode diagnosticsMode() {
            String raw = System.getProperty("teralizer.jqwik.diagnosticsMode");
            if ((raw == null) || raw.trim().isEmpty()) {
                raw = System.getenv("TERALIZER_JQWIK_DIAGNOSTICS_MODE");
            }
            if ((raw == null) || raw.trim().isEmpty()) {
                return DiagnosticsMode.IN_MEMORY_ONLY;
            }
            return DiagnosticsMode.valueOf(raw.trim());
        }

        private static String executionId() {
            String raw = System.getProperty("teralizer.jqwik.executionId");
            if ((raw == null) || raw.trim().isEmpty()) {
                raw = System.getenv("TERALIZER_JQWIK_EXECUTION_ID");
            }
            return (raw == null) || raw.trim().isEmpty() ? "manual" : raw.trim();
        }

        private static java.nio.file.Path executionDirectory() {
            return BASE_DIRECTORY.resolve("executions").resolve(executionId());
        }

        private static java.nio.file.Path valueLogPath() {
            return executionDirectory().resolve(((GENERALIZATION_ID + ".") + VARIANT) + ".values.tsv");
        }

        private static java.nio.file.Path outcomePath() {
            return executionDirectory().resolve(((GENERALIZATION_ID + ".") + VARIANT) + ".outcome.json");
        }

        private static String diagnosticKind(net.jqwik.api.lifecycle.PropertyExecutionResult raw, net.jqwik.api.lifecycle.PropertyExecutionResult mapped) {
            boolean filterExhausted = raw.throwable().isPresent() && (raw.throwable().get() instanceof net.jqwik.api.TooManyFilterMissesException);
            if (mapped.status() == net.jqwik.api.lifecycle.PropertyExecutionResult.Status.SUCCESSFUL) {
                if ((raw.status() == net.jqwik.api.lifecycle.PropertyExecutionResult.Status.FAILED) && filterExhausted) {
                    return "LIMITED_TOO_MANY_FILTER_MISSES";
                }
                return "FULL";
            }
            if (filterExhausted) {
                return "FILTER_EXHAUSTED_SEED_ONLY";
            }
            if (raw.status() == net.jqwik.api.lifecycle.PropertyExecutionResult.Status.FAILED) {
                return "ASSERTION_FAILED";
            }
            return "EXECUTION_ERROR";
        }

        private static synchronized void writeOutcome(net.jqwik.api.lifecycle.PropertyExecutionResult raw, net.jqwik.api.lifecycle.PropertyExecutionResult mapped) {
            if (diagnosticsMode() != DiagnosticsMode.PERSISTED) {
                return;
            }
            try {
                java.nio.file.Files.createDirectories(executionDirectory());
                java.nio.file.Files.write(valueLogPath(), rows, java.nio.charset.StandardCharsets.UTF_8);
                Throwable rawThrowable = (raw.throwable().isPresent()) ? raw.throwable().get() : null;
                String throwableType = (rawThrowable == null) ? "" : rawThrowable.getClass().getName();
                String throwableMessage = ((rawThrowable == null) || (rawThrowable.getMessage() == null)) ? "" : rawThrowable.getMessage();
                String seed = raw.seed().orElse("");
                int distinctNewTuples = (sawDistinctNonSeedTuple) ? Math.max(1, distinctRows.size() - 1) : 0;
                String json = ((((((((((((((((((((((((((((((((((((((((((((((((("{\n" + "  \"executionId\": \"") + jsonEscape(executionId())) + "\",\n") + "  \"projectId\": ") + PROJECT_ID) + ",\n") + "  \"generalizationId\": ") + GENERALIZATION_ID) + ",\n") + "  \"variant\": \"") + jsonEscape(VARIANT)) + "\",\n") + "  \"testCaseName\": \"") + jsonEscape(TEST_CASE_NAME)) + "\",\n") + "  \"diagnosticsMode\": \"PERSISTED\",\n") + "  \"rawStatus\": \"") + raw.status()) + "\",\n") + "  \"finalStatus\": \"") + mapped.status()) + "\",\n") + "  \"diagnosticKind\": \"") + diagnosticKind(raw, mapped)) + "\",\n") + "  \"throwableType\": \"") + jsonEscape(throwableType)) + "\",\n") + "  \"throwableMessage\": \"") + jsonEscape(throwableMessage)) + "\",\n") + "  \"tries\": ") + raw.countTries()) + ",\n") + "  \"checks\": ") + raw.countChecks()) + ",\n") + "  \"distinctTuples\": ") + distinctRows.size()) + ",\n") + "  \"distinctNewTuples\": ") + distinctNewTuples) + ",\n") + "  \"seed\": \"") + jsonEscape(seed)) + "\",\n") + "  \"valueLogPath\": \"") + jsonEscape(valueLogPath().toString())) + "\"\n") + "}\n";
                java.nio.file.Files.write(outcomePath(), json.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            } catch (java.io.IOException e) {
                throw new RuntimeException(e);
            }
        }

        private static String jsonEscape(String value) {
            StringBuilder escaped = new StringBuilder(value.length());
            for (int i = 0; i < value.length(); i++) {
                char ch = value.charAt(i);
                switch (ch) {
                    case '\\' :
                        escaped.append("\\\\");
                        break;
                    case '"' :
                        escaped.append("\\\"");
                        break;
                    case '\n' :
                        escaped.append("\\n");
                        break;
                    case '\r' :
                        escaped.append("\\r");
                        break;
                    case '\t' :
                        escaped.append("\\t");
                        break;
                    default :
                        if (Character.isISOControl(ch) || Character.isSurrogate(ch)) {
                            escaped.append(String.format("\\u%04x", ((int) (ch))));
                        } else {
                            escaped.append(ch);
                        }
                }
            }
            return escaped.toString();
        }

        // Remaps a property that exhausted Arbitrary.filter(...) after validating at least one
        // distinct new tuple to a passing result, so PIT and JUnit see a physically green test
        // while the run stays queryable as filter-exhausted via the outcome sidecar.
        public static final class LimitedFilterMissesHook implements net.jqwik.api.lifecycle.AroundPropertyHook {
            @Override
            public net.jqwik.api.lifecycle.PropertyExecutionResult aroundProperty(net.jqwik.api.lifecycle.PropertyLifecycleContext context, net.jqwik.api.lifecycle.PropertyExecutor property) {
                net.jqwik.api.lifecycle.PropertyExecutionResult raw = property.execute();
                net.jqwik.api.lifecycle.PropertyExecutionResult mapped = raw;
                boolean filterExhausted = raw.throwable().isPresent() && (raw.throwable().get() instanceof net.jqwik.api.TooManyFilterMissesException);
                if (((raw.status() == net.jqwik.api.lifecycle.PropertyExecutionResult.Status.FAILED) && filterExhausted) && sawDistinctNonSeedTuple) {
                    mapped = raw.mapToSuccessful();
                }
                writeOutcome(raw, mapped);
                return mapped;
            }
        }
    }

    @net.jqwik.api.Property(edgeCases = net.jqwik.api.EdgeCasesMode.FIRST, seed = "0", shrinking = net.jqwik.api.ShrinkingMode.OFF, tries = 200)
    @net.jqwik.api.lifecycle.AddLifecycleHook(JqwikValueRecorder.LimitedFilterMissesHook.class)
    void rejection(@net.jqwik.api.ForAll(supplier = TestParametersSupplier.class)
    TestParameters _p_) {
        JqwikValueRecorder.record(_p_);
        assertEquals(0, LoanPolicy.approvedAmount(_p_.score, _p_.amount));
    }

    public static class TestParameters {
        public int score;

        public int amount;

        public TestParameters(int score, int amount) {
            this.score=score;this.amount=amount;
        }

        public java.lang.String toString() {
            return "TestParameters{score=" + this.score + ",amount=" + this.amount + "}";
        }
    }

    public static class TestParametersSupplier implements net.jqwik.api.ArbitrarySupplier<TestParameters> {
        public net.jqwik.api.Arbitrary<TestParameters> get() {
            return new FirstValueArbitrary<TestParameters>(new TestParameters((int) (30), (int) (1)), get_score().flatMap(new java.util.function.Function<Integer, net.jqwik.api.Arbitrary<TestParameters>>() {
                public net.jqwik.api.Arbitrary<TestParameters> apply(final Integer score) {
                    return get_amount(score).map(new java.util.function.Function<Integer, TestParameters>() {
                public TestParameters apply(final Integer amount) {
                    return new TestParameters(score, amount);
                }
            });
                }
            })
            .filter(new java.util.function.Predicate<TestParameters>() {
                public boolean test(final TestParameters _p_) {
                    return (_p_.amount > 0) && (_p_.score < 80) && (_p_.score < 50);
                }
            }));
        }

        private net.jqwik.api.Arbitrary<Integer> get_score() {
            int scoreDefaultMin = Integer.MIN_VALUE;
            int scoreDefaultMax = Integer.MAX_VALUE;
            java.util.List<Integer> scoreLowerBounds = java.util.Arrays.asList(scoreDefaultMin);
            java.util.List<Integer> scoreUpperBounds = java.util.Arrays.asList(scoreDefaultMax, (int) ((int) (79.0)), (int) ((int) (49.0)));
            int scoreMin = java.util.Collections.max(scoreLowerBounds);
            int scoreMax = java.util.Collections.min(scoreUpperBounds);
            if (scoreMin > scoreMax) { return net.jqwik.api.Arbitraries.just((int) (30)); }
            return net.jqwik.api.Arbitraries.integers().between(scoreMin, scoreMax);
        }

        private net.jqwik.api.Arbitrary<Integer> get_amount(final int score) {
            int amountDefaultMin = Integer.MIN_VALUE;
            int amountDefaultMax = Integer.MAX_VALUE;
            java.util.List<Integer> amountLowerBounds = java.util.Arrays.asList(amountDefaultMin, (int) ((int) (1.0)));
            java.util.List<Integer> amountUpperBounds = java.util.Arrays.asList(amountDefaultMax);
            int amountMin = java.util.Collections.max(amountLowerBounds);
            int amountMax = java.util.Collections.min(amountUpperBounds);
            if (amountMin > amountMax) { return net.jqwik.api.Arbitraries.just((int) (1)); }
            return net.jqwik.api.Arbitraries.integers().between(amountMin, amountMax);
        }
    }

    @net.jqwik.api.lifecycle.BeforeProperty
    public void resetJqwikValueRecorder() {
        JqwikValueRecorder.reset();
    }
}