package io.github.charlietap.chasm.memory.atomic

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I32AtomicLoad(
    memory: LinearMemory,
    address: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.loadI32(buffer, address)
}

actual inline fun I32AtomicLoad8U(
    memory: LinearMemory,
    address: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.loadI32_8U(buffer, address)
}

actual inline fun I32AtomicLoad16U(
    memory: LinearMemory,
    address: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.loadI32_16U(buffer, address)
}

actual inline fun I64AtomicLoad(
    memory: LinearMemory,
    address: Int,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.loadI64(buffer, address)
}

actual inline fun I64AtomicLoad8U(
    memory: LinearMemory,
    address: Int,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.loadI64_8U(buffer, address)
}

actual inline fun I64AtomicLoad16U(
    memory: LinearMemory,
    address: Int,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.loadI64_16U(buffer, address)
}

actual inline fun I64AtomicLoad32U(
    memory: LinearMemory,
    address: Int,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.loadI64_32U(buffer, address)
}

