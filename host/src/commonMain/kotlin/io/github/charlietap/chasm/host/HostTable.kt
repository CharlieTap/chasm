package io.github.charlietap.chasm.host

/**
 * `HostTable` is an API for reading and changing the raw references in a
 * WebAssembly table inside host functions.
 */
interface HostTable {

    val size: Int

    fun readRaw(index: Int): Long

    fun writeRaw(index: Int, value: Long)

    /**
     * Borrows the table's real backing array. It must not be retained across
     * table growth, deallocation, or re-entry that can replace the backing.
     */
    @UnsafeHostApi
    fun unsafeBorrowElements(): LongArray
}
