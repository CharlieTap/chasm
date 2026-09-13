# Kotlin source compilation stages

This continues the optional JVM prototype in `codex/kotlin-source-aot`.
The initial block backend is checkpoint `6017018b`; its measured CoreMark
medians are 1531.39 (interpreter) and 2018.44 (generated), from five pairs.
The original checkout remains untouched.

Every stage must pass focused tests and the applicable corpus before its
CoreMark trials. Record deterministic CoreMark output and memory equivalence,
then five paired trials in fresh sequential JVMs. Compilation and execution
counters remain outside the measurements. Record regressions as well as gains.
Commit each completed stage before beginning the next.

| Stage | Implementation | Status |
| --- | --- | --- |
| 1 | Shared scalar semantics used by frame wrappers and source generation | Complete |
| 2 | Promote frame slots to Kotlin locals inside bounded blocks | Complete |
| 3 | Keep locals across branches and loops in generated regions | Pending |
| 4 | Resume generated function bodies around existing guest and host calls | Pending |
| 5 | Reference/GC and exception synchronization; supported Wasm 3.0 corpus | Pending |
| 6 | Structured Kotlin loops and function bodies with explicit eligibility | Pending |
| 7 | Typed generated values, including safe handling of reused physical slots | Pending |
| 8 | Optional direct compiled calls with explicit eligibility and runtime fallback | Pending |

The source compiler must preserve the existing Wasm call stack, traps, host
interoperability, reference roots and exception unwinding. Unsupported existing
runtime proposals (such as SIMD and threads) are not enabled by this work.
Larger or ineligible functions retain a correct earlier tier. Reports must make
the generated coverage and any such fallback visible.

Stage measurements and validation records are saved under
`tools/kotlin-aot/results/stages/`, with raw local reports under
`tools/kotlin-aot/build/stages/`.

## Recorded measurements

Scores below are medians of five fresh paired JVM runs. Ratios compare the
generated backend with the interpreter measured in that same stage. Small
differences between stages should not be treated as isolated optimization gains.

| Stage | Interpreter | Generated | Ratio | Correctness |
| --- | ---: | ---: | ---: | --- |
| 0: bounded blocks | 1531.39 | 2018.44 | 1.318x | 209 Wasm 1.0 fixtures; deterministic CoreMark match |
| 1: shared values | 1507.95 | 1962.84 | 1.302x | Same 209 fixtures in three modes; deterministic CoreMark match |
| 2: block locals | 1395.77 | 2149.77 | 1.540x | Same 209 fixtures in three modes; deterministic CoreMark match |

Stage 1 extracts 86 scalar operations from 271 frame wrappers. Existing
value-based helpers remain in use. It changes semantic factoring, with no new
slot promotion yet. The measured result does not establish a performance gain
over stage 0. Focused compiler, invoker and generated-execution tests pass.

Stage 2 promotes 610 of CoreMark's 1024 generated operations into local
values. Blocks and retained control boundaries are unchanged (333 blocks and
343 controls). Each local is loaded on first use and modified slots are saved
before an unpromoted helper or block exit. Unpromoted helpers invalidate cached
locals. Frame slots retain their raw Long representation, including float bits
and slots reused for different Wasm types. Seven focused tests and ABI checks
pass. Generated scores ranged from 1908.64 to 2271.69; paired interpreter scores
also varied, so compare the within-stage ratio rather than absolute scores from
different stages.
