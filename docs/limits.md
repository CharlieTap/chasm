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
    RuntimeConfig(
        limits = Limits(
            memory = MemoryLimits(
                linearMemory = 1024.pages,
                gcHeap = 64.MB,
                tables = 1.MB,
                onGrowthFailure = GrowthFailureStrategy.Trap,
            ),
        ),
    ),
)
```

Limits are part of the `RuntimeConfig` every engine takes. The interpreter
takes it when the store is created. A compiled module takes its limits at
compile time, and `store(module)` gives the store the limits the module was
compiled with; see [Ahead-of-time compilation](./aot.md#settings).

Sizes are `Bytes`, written with the extensions `bytes`, `KB`, `MB`, `GB` and
`pages`, such as `64.MB` or `1024.pages`. A kilobyte is 1,024 bytes and a page
is 64 KiB. A size cannot be negative.

Every limit defaults to `null`, which leaves that resource bounded only by the
module and the platform. An unset `stack` still has a cap; see
[Stack](#stack).

| Limit             | Bounds                                        | When a guest exceeds it                                          |
|-------------------|-----------------------------------------------|------------------------------------------------------------------|
| `linearMemory`    | each linear memory, in whole 64 KiB pages     | `memory.grow` returns -1                                         |
| `tables`          | each table, at eight bytes per element        | `table.grow` returns -1                                          |
| `gcHeap`          | the store's GC heap (see [GC heap](#gc-heap)) | the allocation traps with `GuestHeapOutOfMemory`                 |
| `stack`           | the stack of each call                        | the call traps with `CallStackExhausted`                         |
| `onGrowthFailure` | —                                             | `Trap` turns a failed `memory.grow` or `table.grow` into a trap  |

## Linear memory

`linearMemory` caps every linear memory in the store. A memory can grow to the
smaller of this cap and the maximum its module declares. The cap counts whole
pages: a size that is not a multiple of 64 KiB is rounded down.

- Instantiating a module that defines a memory whose initial size exceeds the
  cap fails with `MemoryLimitExceeded`. The module leaves nothing behind in the
  store. A compiled module that defines such a memory fails to compile instead.
- `memory.grow` past the cap returns -1, as it does past a declared maximum.
- Creating a memory with `memory` whose minimum exceeds the cap throws
  `IllegalArgumentException`, and the store holds no new memory afterwards.
- Growth requested by the host, through `growMemory` or from a host function,
  is subject to the same cap and returns -1 when it fails.

The cap applies to each memory separately. A module with several memories can
therefore hold up to the cap multiplied by its number of memories.

## Tables

`tables` caps every table in the store, with the same rules as linear memory.
Every element counts as eight bytes, and a size that is not a multiple of eight
is rounded down. A module that defines a table whose initial size exceeds the
cap fails to instantiate with `TableLimitExceeded`, `table` throws
`IllegalArgumentException` for such a table, and `table.grow` past the cap
returns -1.

## GC heap

On the interpreter, `gcHeap` caps the heap that holds the structs,
arrays, and exceptions of every instance in the store. Every field and array
element occupies eight bytes, packed `i8` and `i16` values included.
Collection happens before an allocation is refused; see
[the GC documentation](./gc.md#heap-limit).

In compiled modules, guest objects are ordinary JVM objects and `gcHeap` does
not cap their total: it only refuses an allocation larger than the limit on
its own, checked once per MiB the guest allocates; see
[the GC documentation](./gc.md#in-compiled-modules).

## Stack

`stack` caps the stack of each call the host makes into the store. A call that
would push it past the cap traps with `CallStackExhausted`, which is how
runaway recursion ends.

On the interpreter, the stack holds the parameters, locals and operands of
every active Wasm function, eight bytes each, plus a small record per call.
A call made from inside a host function gets a stack of its own with the same
cap. Chasm never lets a stack exceed 256 MiB, so `null`, or a larger value, has
the effect of 256 MiB.

Compiled code, from class files and from DEX, runs on the calling thread's
stack. Each call, other than to a small leaf function, is charged an estimate
of its frame's size in 32-byte units, and a call
that takes the total past `stack` traps. The total is also capped at three
quarters of a 1 MiB thread stack, whatever `stack` is set to.

`stack` defaults to `null` on every platform. On Android an unset `stack` caps
the stack at 16 MiB, which keeps a runaway guest well inside a default app
heap. For linking compiled modules, an unset `stack` and an explicit `16.MB` are
different settings; see [Ahead-of-time compilation](./aot.md#settings).

## Trapping on grow failure

The WebAssembly specification says a failed `memory.grow` or `table.grow`
returns -1, and Chasm does so by default. Many guests cannot recover from that:
their allocator aborts or executes `unreachable`, and the host sees a trap that
says nothing about the limit.

With `onGrowthFailure = GrowthFailureStrategy.Trap`, a failed `memory.grow` or
`table.grow` in guest code traps with `MemoryGrowFailed` or `TableGrowFailed`
instead, and the trap reports `TrapReason.MEMORY_GROW_FAILED` or
`TrapReason.TABLE_GROW_FAILED`. Growth requested by the host still returns
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

Running time is bounded separately: fuel and interrupts, configured with
`Limits.cpu` and `RuntimeConfig.interrupts`, stop long-running code. With
`cpu = CpuLimits.Metered(10_000_000.fuel)` the store starts with that much
fuel, and `addFuel(store, amount)` tops it up.
