package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.embedding.shapes.Memory

expect fun readBorrowedMemory(
    memory: Memory,
    pointer: Int,
    length: Int,
): ByteArray

expect fun writeBorrowedMemory(
    memory: Memory,
    pointer: Int,
    bytes: ByteArray,
)
