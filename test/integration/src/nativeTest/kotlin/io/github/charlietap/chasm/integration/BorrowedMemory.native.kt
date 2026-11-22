@file:OptIn(UnsafeHostApi::class)

package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.embedding.memory.readBytes
import io.github.charlietap.chasm.embedding.memory.unsafeBorrowMemoryBytes
import io.github.charlietap.chasm.embedding.memory.writeBytes
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.host.UnsafeHostApi
import kotlin.test.assertNull

actual fun readBorrowedMemory(
    memory: Memory,
    pointer: Int,
    length: Int,
): ByteArray {
    assertNull(unsafeBorrowMemoryBytes(memory))
    return readBytes(memory, ByteArray(length), pointer, length)
}

actual fun writeBorrowedMemory(
    memory: Memory,
    pointer: Int,
    bytes: ByteArray,
) {
    assertNull(unsafeBorrowMemoryBytes(memory))
    writeBytes(memory, pointer, bytes)
}
