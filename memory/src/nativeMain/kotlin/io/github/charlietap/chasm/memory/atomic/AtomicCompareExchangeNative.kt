package io.github.charlietap.chasm.memory.atomic

import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I32AtomicCompareExchange(
    memory: LinearMemory,
    address: Int,
    expected: Int,
    replacement: Int,
): Int = TODO()

actual inline fun I32AtomicCompareExchange8(
    memory: LinearMemory,
    address: Int,
    expected: Int,
    replacement: Int,
): Int = TODO()

actual inline fun I32AtomicCompareExchange16(
    memory: LinearMemory,
    address: Int,
    expected: Int,
    replacement: Int,
): Int = TODO()

actual inline fun I64AtomicCompareExchange(
    memory: LinearMemory,
    address: Int,
    expected: Long,
    replacement: Long,
): Long = TODO()

actual inline fun I64AtomicCompareExchange8(
    memory: LinearMemory,
    address: Int,
    expected: Long,
    replacement: Long,
): Long = TODO()

actual inline fun I64AtomicCompareExchange16(
    memory: LinearMemory,
    address: Int,
    expected: Long,
    replacement: Long,
): Long = TODO()

actual inline fun I64AtomicCompareExchange32(
    memory: LinearMemory,
    address: Int,
    expected: Long,
    replacement: Long,
): Long = TODO()


