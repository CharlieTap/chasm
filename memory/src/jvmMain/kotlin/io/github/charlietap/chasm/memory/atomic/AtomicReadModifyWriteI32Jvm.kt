package io.github.charlietap.chasm.memory.atomic

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I32AtomicRmwAdd(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwAddI32(buffer, address, value)
}

actual inline fun I32AtomicRmwSub(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwSubI32(buffer, address, value)
}

actual inline fun I32AtomicRmwAnd(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwAndI32(buffer, address, value)
}

actual inline fun I32AtomicRmwOr(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwOrI32(buffer, address, value)
}

actual inline fun I32AtomicRmwXor(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwXorI32(buffer, address, value)
}

actual inline fun I32AtomicRmwExchange(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwExchangeI32(buffer, address, value)
}

actual inline fun I32AtomicRmw8Add(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwAddI32_8(buffer, address, value)
}

actual inline fun I32AtomicRmw8Sub(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwSubI32_8(buffer, address, value)
}

actual inline fun I32AtomicRmw8And(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwAndI32_8(buffer, address, value)
}

actual inline fun I32AtomicRmw8Or(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwOrI32_8(buffer, address, value)
}

actual inline fun I32AtomicRmw8Xor(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwXorI32_8(buffer, address, value)
}

actual inline fun I32AtomicRmw8Exchange(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwExchangeI32_8(buffer, address, value)
}

actual inline fun I32AtomicRmw16Add(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwAddI32_16(buffer, address, value)
}

actual inline fun I32AtomicRmw16Sub(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwSubI32_16(buffer, address, value)
}

actual inline fun I32AtomicRmw16And(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwAndI32_16(buffer, address, value)
}

actual inline fun I32AtomicRmw16Or(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwOrI32_16(buffer, address, value)
}

actual inline fun I32AtomicRmw16Xor(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwXorI32_16(buffer, address, value)
}

actual inline fun I32AtomicRmw16Exchange(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwExchangeI32_16(buffer, address, value)
}
