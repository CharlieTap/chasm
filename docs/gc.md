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

Choose a strategy with `StoreConfig.gcStrategy` and its threshold with
`StoreConfig.gcThreshold`. The heap belongs to the store, so these settings
apply to every instance in it. `RuntimeConfig.gcStrategy` and
`RuntimeConfig.gcThreshold` are deprecated; an instance still uses either one
when its store leaves the matching setting unset.

```kotlin
val store = store(
    StoreConfig(
        gcStrategy = GCStrategy.TRADITIONAL,
        gcThreshold = GCThreshold.MB(8),
    ),
)
```

### Arena (default)

At the end of a top-level WebAssembly invocation, Chasm checks the managed heap
against the configured threshold and collects when the threshold has been
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

## Heap limit

`StoreLimits.gcHeapBytes` caps the heap so that a guest cannot exhaust host
memory by allocating:

```kotlin
val store = store(
    StoreConfig(
        gcStrategy = GCStrategy.TRADITIONAL,
        limits = StoreLimits(gcHeapBytes = 64L * 1024 * 1024),
    ),
)
```

An allocation that does not fit traps with `GuestHeapOutOfMemory`; the store
remains usable, and the host can release references or collect before calling
into it again. Every struct field and array element occupies eight bytes,
packed `i8` and `i16` values included.

The limit counts the storage the heap has reserved, not just live objects.
Small objects are placed in 16 KiB pages, and collection makes freed space
reusable without giving it back, so a limit below 16 KiB admits only large
arrays.

How much garbage builds up before the limit is reached depends on the
strategy. Traditional collects before reporting exhaustion, so reclaimable
garbage is reused first. Arena and Manual never collect during a call, so
everything allocated since the last collection counts; with a tight limit,
prefer Traditional.

The limit covers the guest heap only. Linear memories and tables have their own
limits, described in [Resource limits](./limits.md), and Kotlin host values
referenced through `externref` are not counted.

