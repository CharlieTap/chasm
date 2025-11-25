package io.github.charlietap.chasm.memory.atomic

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I32AtomicStore(
    memory: LinearMemory,
    address: Int,
    value: Int,
) {
    val buffer = (memory as ByteBufferLinearMemory).memory
    AtomicAccessors.storeI32(buffer, address, value)
}

actual inline fun I32AtomicStore8(
    memory: LinearMemory,
    address: Int,
    value: Int,
) {
    val buffer = (memory as ByteBufferLinearMemory).memory
    AtomicAccessors.storeI32_8(buffer, address, value)
}

actual inline fun I32AtomicStore16(
    memory: LinearMemory,
    address: Int,
    value: Int,
) {
    val buffer = (memory as ByteBufferLinearMemory).memory
    AtomicAccessors.storeI32_16(buffer, address, value)
}

actual inline fun I64AtomicStore(
    memory: LinearMemory,
    address: Int,
    value: Long,
) {
    val buffer = (memory as ByteBufferLinearMemory).memory
    AtomicAccessors.storeI64(buffer, address, value)
}

actual inline fun I64AtomicStore8(
    memory: LinearMemory,
    address: Int,
    value: Long,
) {
    val buffer = (memory as ByteBufferLinearMemory).memory
    AtomicAccessors.storeI64_8(buffer, address, value)
}

actual inline fun I64AtomicStore16(
    memory: LinearMemory,
    address: Int,
    value: Long,
) {
    val buffer = (memory as ByteBufferLinearMemory).memory
    AtomicAccessors.storeI64_16(buffer, address, value)
}

actual inline fun I64AtomicStore32(
    memory: LinearMemory,
    address: Int,
    value: Long,
) {
    val buffer = (memory as ByteBufferLinearMemory).memory
    AtomicAccessors.storeI64_32(buffer, address, value)
}