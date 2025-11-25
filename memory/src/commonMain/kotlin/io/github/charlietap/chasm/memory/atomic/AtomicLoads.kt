@file:Suppress("FunctionName")

package io.github.charlietap.chasm.memory.atomic

import io.github.charlietap.chasm.runtime.memory.LinearMemory

expect inline fun I32AtomicLoad(
    memory: LinearMemory,
    address: Int,
): Int

expect inline fun I32AtomicLoad8U(
    memory: LinearMemory,
    address: Int,
): Int

expect inline fun I32AtomicLoad16U(
    memory: LinearMemory,
    address: Int,
): Int

expect inline fun I64AtomicLoad(
    memory: LinearMemory,
    address: Int,
): Long

expect inline fun I64AtomicLoad8U(
    memory: LinearMemory,
    address: Int,
): Long

expect inline fun I64AtomicLoad16U(
    memory: LinearMemory,
    address: Int,
): Long

expect inline fun I64AtomicLoad32U(
    memory: LinearMemory,
    address: Int,
): Long

