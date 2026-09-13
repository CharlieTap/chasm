# Kotlin source execution prototype

Baseline: `a207e39b2bfb79b110e75387766ad7537081507f`, branch
`codex/kotlin-source-aot`. Work is isolated from the original checkout.

## Deliverable

An optional JVM execution mode that generates and compiles Kotlin source for
Wasm 1.0 modules. Generated bodies call the existing execution helpers directly,
so multiple lowered instructions execute per dispatch. The default interpreter
remains available. A passing Wasm 1.0 corpus run and a controlled CoreMark
comparison complete this prototype; a speedup is measured, not assumed.

The first tier retains ValueStack, linked instruction operands, memories,
tables, globals, function strategies, and the existing guest call convention.
Register promotion, a new Wasm ABI, native targets, and the official spec suite
are outside this first deliverable. Kotlin source keeps later multiplatform
support possible; JVM verification is not evidence of other platform support.

## Approach

1. Add a small optional program compiler hook to the runtime Program, selected
   when constructing a Store. After existing module lowering and call linking,
   collect the final LinkedInstruction descriptions and function entries and
   invoke this hook before the module start function runs. The ordinary path
   must not allocate this metadata. Route parallel instantiation through serial
   lowering when this prototype backend is selected.
2. Put the generator and JVM compilation/loading tools in a separate module.
   Identify basic block entries from function entries, branch targets and call
   continuations. Generate bounded straight-line Kotlin bodies that directly
   call inline executors. Reuse typed linked operands at instantiation; do not
   serialize or freeze live host resources. Preserve original control and call
   instruction slots/metadata and return to their IPs initially. Do not replace
   guest calls with recursive Kotlin calls.
3. Make only the execution helpers needed by generated source accessible, retaining one
   implementation of instruction semantics. Verify bytecode contains expanded
   execution bodies and not per-instruction virtual invoke calls. Bound source
   and method size and cache compiled artifacts by generated structure and
   compiler/runtime identity. Separate source preparation/compilation from
   cached execution, with an explicit mode that refuses cache misses.
4. Test arithmetic, branches, loops, calls/indirect calls, traps, globals,
   memory access/growth, start functions, and repeated/multiple instances.
   Verify the opt-in mode and interpreter execute the same observable results.
   Cover binding validation and code-generation failures explicitly. Run
   relevant module unit tests without pulling in the official spec suite.
5. Reuse the existing corpus runner and the pinned wasm-corpus repository,
   selecting version 1.0. Run its expected-value/memory/host assertions through
   generated execution. Report every fixture, any existing exclusions and
   untested cases, and generated versus control-boundary coverage. A missing
   executor or failed compilation must fail the run, not silently fall back.
6. After the corpus gate, compare CoreMark against the interpreter in fresh,
   sequential JVM processes using the same runtime, memory backend, heap and
   CPU placement. Use deterministic clock/final-memory checks for equivalence,
   then multiple paired real-clock trials. Report scores, spread, source/code
   size, preparation costs separately from execution, and remaining limitations.
7. Review the patch, format changed code, check/update affected public ABI,
   retain reproducible commands and result artifacts, and complete the goal
   only after the requested implementation and validation are finished.

## Risks to resolve during implementation

- Return metadata can refer to the original call-site dispatcher. Preserve it.
- Kotlin `inline` on a dispatcher factory does not inline a later virtual
  invocation. Generated source must call executable helper functions directly.
- Large functions and large modules can overwhelm Kotlin compilation or JVM
  method limits. Bound generated methods/classes and inspect actual artifacts.
- Start functions must use the selected mode, not run before installation.
- Cache reuse must bind current instance resources and validate code shape;
  it must not reuse another Store's addresses or freeze mutable state.
- Corpus classification and existing exclusions must be reported honestly.

## Progress

- [x] Create goal and isolated worktree; inspect current execution/compiler seams.
- [x] Write implementation and validation plan before code changes.
- [x] Implement optional compiler hook and source backend.
- [x] Compile and inspect generated JVM code; pass focused semantic tests.
- [x] Pass and report the Wasm 1.0 corpus slice.
- [x] Validate CoreMark and measure paired interpreter/generated execution.
- [x] Finish formatting, ABI checks and review; report results and limitations.

## Completed implementation

The optional `ProgramCompiler` hook runs after linking and before initialization.
`store(compiler)` selects it; default stores retain the ordinary interpreter path.
The common Kotlin generator emits small block methods and the JVM driver either
prepares their classes or requires a cache hit. Internal executor access uses
Kotlin friend compilation. No broad public executor ABI was needed.

Bytecode inspection exposed oversized JVM methods in the initial class layout;
the final implementation bounds blocks to 16 operations and places each in its
own method. All 209 selected Wasm 1.0 corpus fixtures pass in both execution
modes, including the default configuration's two excluded 1.0 targets. CoreMark
matches deterministic observable state and improves median real-clock score by
31.8% across five fresh-JVM pairs on this machine.

See [results and evidence](tools/kotlin-aot/RESULTS.md) and
[usage/reproduction commands](tools/kotlin-aot/README.md).
