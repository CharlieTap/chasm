package io.github.charlietap.chasm.memory.atomic

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I64AtomicRmwAdd(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwAddI64(buffer, address, value)
}

actual inline fun I64AtomicRmwSub(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwSubI64(buffer, address, value)
}

actual inline fun I64AtomicRmwAnd(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwAndI64(buffer, address, value)
}

actual inline fun I64AtomicRmwOr(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwOrI64(buffer, address, value)
}

actual inline fun I64AtomicRmwXor(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwXorI64(buffer, address, value)
}

actual inline fun I64AtomicRmwExchange(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwExchangeI64(buffer, address, value)
}

actual inline fun I64AtomicRmw8Add(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwAddI64_8(buffer, address, value)
}

actual inline fun I64AtomicRmw8Sub(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwSubI64_8(buffer, address, value)
}

actual inline fun I64AtomicRmw8And(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwAndI64_8(buffer, address, value)
}

actual inline fun I64AtomicRmw8Or(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwOrI64_8(buffer, address, value)
}

actual inline fun I64AtomicRmw8Xor(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwXorI64_8(buffer, address, value)
}

actual inline fun I64AtomicRmw8Exchange(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwExchangeI64_8(buffer, address, value)
}

actual inline fun I64AtomicRmw16Add(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwAddI64_16(buffer, address, value)
}

actual inline fun I64AtomicRmw16Sub(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwSubI64_16(buffer, address, value)
}

actual inline fun I64AtomicRmw16And(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwAndI64_16(buffer, address, value)
}

actual inline fun I64AtomicRmw16Or(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwOrI64_16(buffer, address, value)
}

actual inline fun I64AtomicRmw16Xor(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwXorI64_16(buffer, address, value)
}

actual inline fun I64AtomicRmw16Exchange(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwExchangeI64_16(buffer, address, value)
}

actual inline fun I64AtomicRmw32Add(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwAddI64_32(buffer, address, value)
}

actual inline fun I64AtomicRmw32Sub(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwSubI64_32(buffer, address, value)
}

actual inline fun I64AtomicRmw32And(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwAndI64_32(buffer, address, value)
}

actual inline fun I64AtomicRmw32Or(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwOrI64_32(buffer, address, value)
}

actual inline fun I64AtomicRmw32Xor(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwXorI64_32(buffer, address, value)
}

actual inline fun I64AtomicRmw32Exchange(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.rmwExchangeI64_32(buffer, address, value)
}
