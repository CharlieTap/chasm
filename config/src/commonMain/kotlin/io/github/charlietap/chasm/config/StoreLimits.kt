package io.github.charlietap.chasm.config

/**
 * Bounds the resources that code running in a store can consume. A `null`
 * limit leaves that resource bounded only by the module and the platform.
 *
 * @property gcHeapBytes caps the storage the store's GC heap reserves for the
 * structs, arrays and exceptions of every instance in the store. Every field
 * and array element occupies eight bytes, including packed `i8` and `i16`
 * values. Small objects are placed in 16 KiB pages that collection makes
 * reusable but does not give back, so a cap below 16 KiB admits only large
 * arrays. An allocation that would exceed the cap traps with
 * `InvocationError.GuestHeapOutOfMemory`.
 * @property memoryPages caps each linear memory in the store at this many
 * 64 KiB pages, on top of any maximum the module declares. Instantiating a
 * module that defines a memory whose initial size exceeds the cap fails, and
 * `memory.grow` past the cap returns -1. Memories created by the host keep
 * their initial size but cannot grow past the cap.
 * @property tableElements caps each table in the store at this many elements,
 * on top of any maximum the module declares, with the same rules as
 * [memoryPages].
 * @property trapOnGrowFailure makes a failed `memory.grow` or `table.grow`
 * trap instead of returning -1, so the host sees why a guest stopped rather
 * than whatever the guest does on allocation failure. Growth requested by the
 * host still returns -1.
 */
data class StoreLimits(
    val gcHeapBytes: Long? = null,
    val memoryPages: Int? = null,
    val tableElements: Int? = null,
    val trapOnGrowFailure: Boolean = false,
) {
    init {
        require(gcHeapBytes == null || gcHeapBytes >= 0) { "gcHeapBytes must not be negative" }
        require(memoryPages == null || memoryPages >= 0) { "memoryPages must not be negative" }
        require(tableElements == null || tableElements >= 0) { "tableElements must not be negative" }
    }
}
