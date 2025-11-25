@file:Suppress("FunctionName")

package io.github.charlietap.chasm.memory.atomic

import io.github.charlietap.chasm.runtime.memory.LinearMemory

expect inline fun I32AtomicWait(
    memory: LinearMemory,
    address: Int,
    expected: Int,
    timeoutNanos: Long,
): Int

expect inline fun I64AtomicWait(
    memory: LinearMemory,
    address: Int,
    expected: Long,
    timeoutNanos: Long,
): Int

expect inline fun AtomicNotify(
    memory: LinearMemory,
    address: Int,
    count: Int,
): Int

expect inline fun AtomicFence(
    memory: LinearMemory,
)

