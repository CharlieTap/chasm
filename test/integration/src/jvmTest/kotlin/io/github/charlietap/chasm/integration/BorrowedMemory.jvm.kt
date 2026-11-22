@file:OptIn(UnsafeHostApi::class)

package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.embedding.memory.unsafeBorrowMemoryBuffer
import io.github.charlietap.chasm.embedding.memory.unsafeBorrowMemoryBytes
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.host.UnsafeHostApi
import kotlin.test.assertNotNull
import kotlin.test.assertNull

actual fun readBorrowedMemory(
    memory: Memory,
    pointer: Int,
    length: Int,
): ByteArray {
    assertNull(unsafeBorrowMemoryBytes(memory))
    val buffer = assertNotNull(unsafeBorrowMemoryBuffer(memory))
    return ByteArray(length) { index -> buffer.get(pointer + index) }
}

actual fun writeBorrowedMemory(
    memory: Memory,
    pointer: Int,
    bytes: ByteArray,
) {
    val buffer = assertNotNull(unsafeBorrowMemoryBuffer(memory))
    bytes.forEachIndexed { index, byte -> buffer.put(pointer + index, byte) }
}
