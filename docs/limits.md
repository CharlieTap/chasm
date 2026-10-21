# Resource limits in Chasm

A guest module can ask for memory in three places: linear memory, tables, and
the GC heap. On its own, each grows until it reaches the maximum the module
declares, or until the host runs out of memory. When the guest is untrusted,
such as a plugin, the host can set tighter limits on the store the guest runs
in.

Limits belong to the store. Every instance in a store shares them, so give each
untrusted guest its own store.

```kotlin
val store = store(
    StoreConfig(
        gcStrategy = GCStrategy.TRADITIONAL,
        limits = StoreLimits(
            gcHeapBytes = 64L * 1024 * 1024,
            memoryPages = 1024,
            tableElements = 100_000,
            trapOnGrowFailure = true,
        ),
    ),
)
```

Every limit defaults to `null`, which leaves that resource bounded only by the
module and the platform.

| Limit               | Bounds                            | When a guest exceeds it                                   |
|---------------------|-----------------------------------|-----------------------------------------------------------|
| `memoryPages`       | each linear memory, in 64 KiB pages | `memory.grow` returns -1                                |
| `tableElements`     | each table, in elements           | `table.grow` returns -1                                   |
| `gcHeapBytes`       | the store's GC heap, in bytes     | the allocation traps with `GuestHeapOutOfMemory`          |
| `trapOnGrowFailure` | —                                 | turns a failed `memory.grow` or `table.grow` into a trap  |

## Linear memory

`memoryPages` caps every linear memory in the store. A memory can grow to the
smaller of this cap and the maximum its module declares.

- Instantiating a module that defines a memory whose initial size exceeds the
  cap fails with `MemoryLimitExceeded`. The module leaves nothing behind in the
  store.
- `memory.grow` past the cap returns -1, as it does past a declared maximum.
- Memories created by the host with `memory` keep the initial size the host
  asked for, but cannot grow past the cap.
- Growth requested by the host, through `growMemory` or from a host function,
  is subject to the same cap and returns -1 when it fails.

The cap applies to each memory separately. A module with several memories can
therefore hold up to the cap multiplied by its number of memories.

## Tables

`tableElements` caps every table in the store, with the same rules as linear
memory. A module that defines a table whose initial size exceeds the cap fails
to instantiate with `TableLimitExceeded`, and `table.grow` past the cap
returns -1.

## GC heap

`gcHeapBytes` caps the heap that holds the structs, arrays, and exceptions of
every instance in the store. Every field and array element occupies eight
bytes, packed `i8` and `i16` values included. The collection strategy decides
how much garbage counts against the cap; see [the GC documentation](./gc.md#heap-limit).

## Trapping on grow failure

The WebAssembly specification says a failed `memory.grow` or `table.grow`
returns -1, and Chasm does so by default. Many guests cannot recover from that:
their allocator aborts or executes `unreachable`, and the host sees a trap that
says nothing about the limit.

With `trapOnGrowFailure = true`, a failed `memory.grow` or `table.grow` in guest
code traps with `MemoryGrowFailed` or `TableGrowFailed` instead. With
`RuntimeConfig.debugInfo` enabled, the trap reports `TrapReason.MEMORY_GROW_FAILED`
or `TrapReason.TABLE_GROW_FAILED`. Growth requested by the host still returns
-1, so the host can handle it.

## After a limit is hit

A trap ends the call, not the store. The host can drop the store, or release
references and collect before calling into it again.

## What limits do not cover

Store limits bound what a guest allocates in Chasm's own structures. They are
not a bound on total process memory. They do not count:

- Kotlin values the host passes to a guest as `externref`.
- Data and element segments, whose size is fixed by the module binary.
- Temporary copies made while a memory, table, or heap grows.

Call depth and running time are bounded separately: deep recursion traps with
`CallStackExhausted`, and fuel and interrupts, configured with
`StoreConfig.meterFuel` and `StoreConfig.interruptible`, stop long-running code.
