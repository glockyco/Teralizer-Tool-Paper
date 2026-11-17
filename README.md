# Teralizer tool paper

LaTeX sources of the tool demonstration paper "Teralizer: A Tool for Generalizing JUnit Tests into Property-Based jqwik Tests" by Johann Glock, Clemens Bauer and Martin Pinzger. The preprint is [arXiv:2611.19242](https://arxiv.org/abs/2611.19242).

Teralizer itself, its documentation and its replication package are in [glockyco/Teralizer](https://github.com/glockyco/Teralizer). Its `scripts/try-example.sh` runs the example of the paper, so start there to try the tool.

## Build the paper

Build with a TeX Live installation that includes `latexmk`:

```sh
latexmk main.tex
```

The PDF is `build/main.pdf`. `python arxiv/build.py` builds the arXiv upload in `build/arxiv/`.

## Reproduce the example

`demo/loan` is the example project, `demo/refactoring` the refactored method of Fig. 1 and `demo/output` what a Teralizer run on the example produced. Every excerpt of the example in the paper comes from these folders.

`demo/run.sh` reruns Teralizer on the example and refreshes `demo/output`. It needs Docker and the `teralizer` image, which `docker build -t teralizer .` builds in the Teralizer repository. On Windows, run it with the Bash of Git for Windows.

## Cite

```bibtex
@misc{glock_2026_teralizer_tool,
  title         = {Teralizer: A Tool for Generalizing {JUnit} Tests into Property-Based jqwik Tests},
  author        = {Glock, Johann and Bauer, Clemens and Pinzger, Martin},
  year          = {2026},
  eprint        = {2611.19242},
  archivePrefix = {arXiv},
  primaryClass  = {cs.SE},
  url           = {https://arxiv.org/abs/2611.19242}
}
```

## License

The text and the figures of the paper are licensed under [CC BY 4.0](LICENSE-CC-BY-4.0). The code in `arxiv/`, `scripts/` and `demo/` is licensed under the [MIT License](LICENSE-MIT).

`AGENTS.md` describes how the paper and its figures are kept in line with the example and the evaluation.
