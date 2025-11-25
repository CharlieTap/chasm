package io.github.charlietap.chasm.memory.atomic

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I32AtomicWait(
    memory: LinearMemory,
    address: Int,
    expected: Int,
    timeoutNanos: Long,
): Int {
    val mem = memory as ByteBufferLinearMemory
    return mem.waitRegistry.waitI32(mem.memory, address, expected, timeoutNanos)
}

actual inline fun I64AtomicWait(
    memory: LinearMemory,
    address: Int,
    expected: Long,
    timeoutNanos: Long,
): Int {
    val mem = memory as ByteBufferLinearMemory
    return mem.waitRegistry.waitI64(mem.memory, address, expected, timeoutNanos)
}

actual inline fun AtomicNotify(
    memory: LinearMemory,
    address: Int,
    count: Int,
): Int {
    val mem = memory as ByteBufferLinearMemory
    return mem.waitRegistry.notify(address, count)
}

actual inline fun AtomicFence(
    memory: LinearMemory,
) {
    WaitRegistry.fence()
}
