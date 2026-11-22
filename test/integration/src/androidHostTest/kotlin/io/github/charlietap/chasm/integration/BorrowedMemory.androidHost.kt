@file:OptIn(UnsafeHostApi::class)

package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.embedding.memory.unsafeBorrowMemoryBytes
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.host.UnsafeHostApi
import kotlin.test.assertNotNull

actual fun readBorrowedMemory(
    memory: Memory,
    pointer: Int,
    length: Int,
): ByteArray {
    return assertNotNull(unsafeBorrowMemoryBytes(memory)).copyOfRange(pointer, pointer + length)
}

actual fun writeBorrowedMemory(
    memory: Memory,
    pointer: Int,
    bytes: ByteArray,
) {
    bytes.copyInto(assertNotNull(unsafeBorrowMemoryBytes(memory)), pointer)
}
