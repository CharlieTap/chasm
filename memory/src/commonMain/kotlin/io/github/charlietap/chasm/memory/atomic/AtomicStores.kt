@file:Suppress("FunctionName")

package io.github.charlietap.chasm.memory.atomic

import io.github.charlietap.chasm.runtime.memory.LinearMemory

expect inline fun I32AtomicStore(
    memory: LinearMemory,
    address: Int,
    value: Int,
)

expect inline fun I32AtomicStore8(
    memory: LinearMemory,
    address: Int,
    value: Int,
)

expect inline fun I32AtomicStore16(
    memory: LinearMemory,
    address: Int,
    value: Int,
)

expect inline fun I64AtomicStore(
    memory: LinearMemory,
    address: Int,
    value: Long,
)

expect inline fun I64AtomicStore8(
    memory: LinearMemory,
    address: Int,
    value: Long,
)

expect inline fun I64AtomicStore16(
    memory: LinearMemory,
    address: Int,
    value: Long,
)

expect inline fun I64AtomicStore32(
    memory: LinearMemory,
    address: Int,
    value: Long,
)

