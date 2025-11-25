package io.github.charlietap.chasm.memory.atomic

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I32AtomicCompareExchange(
    memory: LinearMemory,
    address: Int,
    expected: Int,
    replacement: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.compareExchangeI32(buffer, address, expected, replacement)
}

actual inline fun I32AtomicCompareExchange8(
    memory: LinearMemory,
    address: Int,
    expected: Int,
    replacement: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.compareExchangeI32_8(buffer, address, expected, replacement)
}

actual inline fun I32AtomicCompareExchange16(
    memory: LinearMemory,
    address: Int,
    expected: Int,
    replacement: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.compareExchangeI32_16(buffer, address, expected, replacement)
}

actual inline fun I64AtomicCompareExchange(
    memory: LinearMemory,
    address: Int,
    expected: Long,
    replacement: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.compareExchangeI64(buffer, address, expected, replacement)
}

actual inline fun I64AtomicCompareExchange8(
    memory: LinearMemory,
    address: Int,
    expected: Long,
    replacement: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.compareExchangeI64_8(buffer, address, expected, replacement)
}

actual inline fun I64AtomicCompareExchange16(
    memory: LinearMemory,
    address: Int,
    expected: Long,
    replacement: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.compareExchangeI64_16(buffer, address, expected, replacement)
}

actual inline fun I64AtomicCompareExchange32(
    memory: LinearMemory,
    address: Int,
    expected: Long,
    replacement: Long,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return AtomicAccessors.compareExchangeI64_32(buffer, address, expected, replacement)
}
