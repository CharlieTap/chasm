package io.github.charlietap.chasm.memory.atomic

import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I32AtomicWait(
    memory: LinearMemory,
    address: Int,
    expected: Int,
    timeoutNanos: Long,
): Int = TODO()

actual inline fun I64AtomicWait(
    memory: LinearMemory,
    address: Int,
    expected: Long,
    timeoutNanos: Long,
): Int = TODO()

actual inline fun AtomicNotify(
    memory: LinearMemory,
    address: Int,
    count: Int,
): Int = TODO()

actual inline fun AtomicFence(
    memory: LinearMemory,
) {
    TODO()
}


