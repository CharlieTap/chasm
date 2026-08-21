# WebAssembly GC in Chasm

Chasm supports the [WebAssembly GC proposal](https://github.com/WebAssembly/gc/blob/main/proposals/gc/Overview.md).
Structs, arrays, and exceptions are allocated in a non-moving, mark-and-sweep
heap owned by the runtime Store. Modules that only use linear memory do not
allocate in this heap.

The collector traces reference fields described by registered WebAssembly
types. During execution, the current value stack is passed directly to a
synchronous collection. Globals, tables, and element segments are also roots.

Kotlin host values and `externref` remain outside this collector. Explicit
collection is intended for embedding code between invocations; collection from
inside a host-function callback is not supported.

## Collection strategies

Choose a strategy with `RuntimeConfig.gcStrategy`.

### Arena (default)

At the end of a top-level WebAssembly invocation, Chasm checks the managed heap
against `RuntimeConfig.gcThreshold` and collects when the threshold has been
reached. Arena adds no per-allocation policy check.

### Manual

Chasm performs no automatic collection. The embedding triggers collection with
the public `gc` function.

### Traditional

Each struct, array, and exception allocation performs a capacity and threshold
preflight. When collection is required, the currently executing value stack is
scanned directly before allocation continues.

After collection, the next threshold adapts to the live heap and pending
allocation:

```text
max(configured threshold, 2 * live words, 2 * pending allocation words)
```

Reusable swept capacity can satisfy an allocation without another collection.
If the configured heap is exhausted, Chasm makes one collection attempt before
reporting guest heap exhaustion as an invocation error.
