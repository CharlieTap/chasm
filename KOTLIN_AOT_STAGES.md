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
| 3 | Keep locals across branches and loops in generated regions | Complete |
| 4 | Resume generated function bodies around existing guest and host calls | Complete |
| 5 | Reference/GC and exception synchronization; supported Wasm 3.0 corpus | Complete |
| 6 | Structured Kotlin loops and function bodies with explicit eligibility | Complete |
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
| 3: local regions | 1560.06 | 4184.68 | 2.682x | Same 209 fixtures in three modes; deterministic CoreMark match |
| 4: resumable functions | 1583.16 | 4215.85 | 2.663x | Same 209 fixtures in three modes; deterministic CoreMark match |
| 5: GC and exceptions | 1582.90 | 4255.92 | 2.689x | 386 supported fixtures in three modes; deterministic CoreMark match |
| 6: structured control | 1580.65 | 4926.11 | 3.117x | Same 386 fixtures in three modes; deterministic CoreMark match |

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

Stage 3 keeps raw slot locals across branches and loops in bounded regions.
Calls, returns and traps that transfer control remain at their original program
addresses. The same 23 scalar memory operations serve the frame wrappers and
generated code, preserving their existing bounds checks. Copy sequences retain
their sequential or parallel semantics. All 1024 CoreMark data operations are
promoted; 258 branches execute inside generated regions, leaving 85 retained
control operations. There are 99 region classes and 421 basic blocks.

Instrumentation uses a separate inlined body. A bytecode audit found no counter
or per-instruction dispatcher calls in measured `invoke` methods; the largest
ends at bytecode offset 3486. Classes total 2,486,868 bytes, including the counted
and uncounted bodies. Eight focused generated-execution tests, the compiler and
invoker tests, and ABI checks pass. Generated scores ranged from 4176.82 to
4298.92. This is a CoreMark result; instrumented corpus timings are not used as
performance claims for other workloads.

Stage 4 combines eligible regions into complete resumable function bodies.
Calls are holes in the generated control-flow graph: execution saves the frame
and returns the original call IP, then resumes in the same body at the saved
continuation after the callee returns. Call-site dispatchers and their result
metadata remain installed. A 20,000-level guest recursion test and shared
entry/continuation identity checks pass alongside the existing tests.

CoreMark has nine complete resumable bodies and six functions using bounded
regions. Whole-function eligibility requires at most 192 original instructions
and a conservative source-expansion cost of at most 6000. The result is 92
classes; the largest measured method ends at bytecode offset 5044, with no
counter or per-instruction dispatcher calls. Generated scores ranged from
4197.86 to 4239.08. This stage establishes resumable function structure; the
measurements show no additional speed gain over stage 3.

Stage 5 adds 109 executor bindings for reference, table and aggregate operations.
Every such helper observes the saved canonical frame; generated locals reload
afterward. Reference branches, all call variants and exception transfers retain
the original dispatchers. Both explicit branch targets and catch continuations
are generated entry points.

Forced-GC regressions exposed an existing interpreter bug after both calls and
exception unwinding: the restored stack depth excluded reference temporaries
written later. A catch case returned 189 instead of 579. Allocating instructions
now carry the compiled frame size, and their existing preflight restores the
complete frame root range before collection. No extra dispatch instruction is
introduced. Generated code saves its local values before calling these helpers.
The regressions pass in interpreter, preparation and cached modes, with an
independent runtime GC regression test.

The corpus runner also honors explicitly declared WASI function stubs when no
full host configuration is supplied. Previously it skipped 39 Kotlin fixtures
before considering their deterministic stubs. Unstubbed WASI imports still
require a host configuration. A runner regression test covers both cases.
The focused suite has nine generated-backend tests, 90 compiler tests, 64 invoker
tests, four runtime GC integration tests and one runner test.

Large supported modules also require bounded source compilation. Prisma expands
to about 114 MiB of source across more than 26,000 region classes. The JVM driver
now compiles independent classes in batches of at most 128 classes or 1 MiB of
source. All batches must complete before the artifact is marked ready. This
bounds the compiler working set; it does not remove the source/class-size cost.

All 386 selected fixtures pass in preparation, cached and interpreter modes:
209 Wasm 1.0, 60 Wasm 2.0 and 117 Wasm 3.0. There are no skipped fixtures.
The selection retains the repository's `esbuild` exclusion, recorded explicitly
in the result file. Every cached compilation reports a cache hit and zero
compilation time. CoreMark output, clock calls and full memory SHA-256 match.

CoreMark's generated coverage remains 1282 instructions, including all 1024
data operations, across 92 classes. The largest measured method ends at bytecode
offset 5044, with no counter or per-instruction dispatcher calls. Class files
total 2,443,557 bytes. Five generated scores range from 4220.30 to 4322.14.
The result preserves the previous stage's performance while adding the GC and
exception support; it does not establish a separate speed gain over stage 4.

Stage 6 emits direct Kotlin loops for contiguous block chains whose internal
edges go forward or back to the header. Exit branches keep their conditional
copies. Branches from outside the loop and catch entries prevent removal of an
interior entry. Other control flow retains the state machine. A complete body
with one external entry and a linear sequence of blocks and loops also omits
the outer program-counter switch. Original instructions remain installed at
interior addresses that no legal incoming edge can reach.

Reports count structured loops, their original basic blocks and bodies without
a program-counter switch. Focused tests cover early exits with copies, exact
internal execution counts, retained interior instructions and rejection of
outside branches and catch entries, alongside the Wasm and forced-GC tests.

All 386 fixtures pass in preparation, cached and interpreter modes. Every
fixture's preparation block and instruction counts also match stage 5 exactly.
CoreMark uses 27 structured loops covering 33 basic blocks; 43 of its 92
generated bodies omit the outer program-counter switch. Source totals 446,713
bytes and classes total 2,457,043 bytes. The largest measured method still ends
at bytecode offset 5044, with no counters or instruction-dispatch calls.

Eleven focused backend tests and ABI checks pass. Five generated scores range
from 4874.09 to 4950.50, compared with 4220.30 to 4322.14 in stage 5. The paired
interpreter median remains similar; the recorded within-stage ratio is 3.117x.
