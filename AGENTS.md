# Teralizer tool paper

LaTeX sources of the tool demonstration paper on Teralizer.

## Paper

- `main.tex` holds the whole text. Its preamble defines the colors and TikZ styles of the figures.
- `figures/idea.tex` is Fig. 1. It shows the use case and the idea on the test `fullApproval`: a refactoring that changes the result for a score of 80, which the unit test does not detect, the specification that Teralizer extracts, and the generalized test that detects the regression. `\IEEEaftertitletext` in `main.tex` places it below the author block, because a reader who sees only the first page has to learn there what Teralizer is for and how it works.
- Fig. 1 shows the generalized test in simplified form, with the ranges of the generated supplier class written as jqwik annotations, because the generated class spreads them over several hundred lines. Keep the ranges and the assertion in line with the generated class, and keep the sentence of Section II that says how the generated code differs.
- `figures/pipeline.tex` is Fig. 2, the overview of the five stages with their results for the three unit tests of the example. It carries what the text of Section II leaves out, so keep its cells in line with `demo/output` and keep that text short.
- Fig. 3 is an excerpt of `demo/output/console.txt` in `main.tex`, which shows what `scripts/try-example.sh` prints. Mark every omission with `[...]`.
- `figures/efficiency.tex` is Fig. 4, the comparison with EvoSuite. Its points are typed in, so compare them with their source when the evaluation changes.
- Blue marks what stems from the path condition and orange what stems from the symbolic output expression, in every figure. In Fig. 1, red and green mark the removed and the added lines of the refactoring, and their darker shades the condition that it changes.
- The paper is a tool paper. It explains the idea on the example, shows the pipeline and the usage in figures, and leaves out implementation detail that a reader needs neither to understand the example nor to run the tool, because such detail hides the idea and raises questions that the paper has no room to answer.
- Write the text in the register and with the terms of the Teralizer paper: method under test (MUT), MUT call, input/output specification, path condition, symbolic output expression, input partition, generator, oracle, generalization strategy, tries, test suite reduction and mutation score. Do not coin other words for them, do not phrase headings or opening sentences as slogans, and state results without judging them, because reviewers expect the established terms and an objective register, and everyday paraphrases read as vague. The plain-prose rules for documentation do not apply to the manuscript.
- Check a changed text with reader trials (`skill://persona-trials`) on the rendered pages, one of them on the first page alone. Add text only where a reader failed, because simulated reviewers ask for more caveats and detail in every round.

## Build

- Build with `latexmk main.tex`. The PDF is `build/main.pdf`.
- The call allows two to four pages including references, so check the page count and the log for overfull boxes after every change.
- Let no paragraph end with a line shorter than half the column, because such a line takes a full line of the four pages for a few words. `python scripts/last-lines.py` lists these lines after a build.
- `python arxiv/build.py` prepares an arXiv upload in `build/arxiv/`: `arxiv-source.tar.gz` with the sources without comments, because arXiv publishes them, and with the compiled bibliography, so that arXiv uses exactly the references of the local build, and `metadata.txt` with the form's fields as plain text. It compiles the bundle with pdfLaTeX alone and fails unless its text matches `build/main.pdf`.

## Example

- `demo/run.sh` runs Teralizer on `demo/loan` and refreshes `demo/output`. Run it with the Bash of Git for Windows after changing the example or its configuration, because the paper may only show what a run produced.
- Whatever the paper shows of the example comes from `demo/loan` or `demo/output`. Name the file in a comment next to each excerpt, and update the excerpt when a run changes that file.
- Never edit files under `demo/output` by hand.
- `demo/loan` and `demo/loan.conf` mirror `projects/example-loan` and `project-configs/example-loan.conf` of the Teralizer repository, which `scripts/try-example.sh` runs there. Change both copies together, because the paper sends readers to that script.
- `demo/refactoring/LoanPolicy.java` is the refactored method of Fig. 1, with which `demo/run.sh` replaces the method before it runs all tests again. `scripts/try-example.sh` of the Teralizer repository has to apply the same refactoring and print what `demo/output/console.txt` holds, because Fig. 3 shows that file as the output of the script.
- The commands and the requirements that the paper gives for running Teralizer are those of the README of the Teralizer repository. Change the paper when that section of the README changes.

## Numbers and claims

- Name the source of every number and of every statement about the tool in a `% Source:` comment above the sentence. A comment covers the sentences that follow it up to the next comment or the end of the paragraph.
- Numbers about the evaluation come from the analysis reports of the Teralizer repository (`analysis/reports`), from the generated macros of the Teralizer paper, and, where neither has a number, from the text of that paper. Take them from there unchanged.
- Present the evaluation as one body of results and cite the preprint for it. Do not tell readers which tool version or run a number comes from, because that does not help them.
- The SCCH funding acknowledgment exists in a full, a short and a very short wording. Use the longest one that still fits into four pages.
- Take bibliography entries from the Teralizer paper's `main.bib` where it has them, and verify new ones with `skill://research-evidence`. The header of this repository's `main.bib` holds the formatting rules.

## Commits

Follow `skill://commit-policy`.
