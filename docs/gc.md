# WebAssembly GC in Chasm

Chasm supports the [WebAssembly GC proposal](https://github.com/WebAssembly/gc/blob/main/proposals/gc/Overview.md).
On the interpreter, structs, arrays, and exceptions are allocated in a
non-moving, mark-and-sweep heap owned by the runtime Store. Modules that only
use linear memory do not allocate in this heap. Compiled modules work
differently; see [In compiled modules](#in-compiled-modules).

The collector traces reference fields described by registered WebAssembly
types. During execution, the current value stack is passed directly to a
synchronous collection. Globals, tables, and element segments are also roots.

Kotlin host values and `externref` remain outside this collector. Explicit
collection works from embedding code between invocations, with
`collect(store)`, and from inside a host function, with `collect()` from
`io.github.charlietap.chasm.host`. A collection from a host function keeps the
references held by every active Wasm frame.

## Collection

Chasm collects when an allocation needs it; there is no strategy or threshold
to configure. The heap belongs to the store, so collection covers every
instance in it.

Each struct, array, and exception allocation performs a capacity and threshold
preflight. When collection is required, the currently executing value stack is
scanned directly before allocation continues.

The first collection happens once the heap holds 8 MiB, or reaches its limit
if that is lower. After collection, the next threshold adapts to the live heap
and pending allocation, and never passes the heap limit:

```text
min(heap limit, max(8 MiB, 2 * live words, 2 * pending allocation words))
```

Reusable swept capacity can satisfy an allocation without another collection.
If the configured heap is exhausted, Chasm makes one collection attempt before
reporting guest heap exhaustion as an invocation error.

## Heap limit

On the interpreter, `MemoryLimits.gcHeap` caps the heap so that a
guest cannot exhaust host memory by allocating:

```kotlin
val store = store(
    RuntimeConfig(
        limits = Limits(memory = MemoryLimits(gcHeap = 64.MB)),
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

Chasm collects before reporting exhaustion, so reclaimable garbage is reused
first.

The limit covers the guest heap only. Linear memories and tables have their own
limits, described in [Resource limits](./limits.md), and Kotlin host values
referenced through `externref` are not counted.

## In compiled modules

Compiled modules allocate structs, arrays, and exceptions as ordinary JVM
objects, so the JVM's or ART's own collector reclaims them. None of the above
applies: there is no Chasm heap, collection threshold or eight-byte slot, and
`collect(store)` asks the runtime to collect with `Runtime.gc()`.

Compiled code meters what guest code allocates and checks it once per MiB
allocated. At each check, an allocation larger than `gcHeap` on its own traps
with `GuestHeapOutOfMemory`; `gcHeap` does not cap the total the guest holds.
The check also traps when the process heap has too little headroom left, after
one collection.

