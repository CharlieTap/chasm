@file:Suppress("FunctionName")

package io.github.charlietap.chasm.memory.atomic

import io.github.charlietap.chasm.runtime.memory.LinearMemory

expect inline fun I64AtomicRmwAdd(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long

expect inline fun I64AtomicRmwSub(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long

expect inline fun I64AtomicRmwAnd(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long

expect inline fun I64AtomicRmwOr(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long

expect inline fun I64AtomicRmwXor(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long

expect inline fun I64AtomicRmwExchange(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long

expect inline fun I64AtomicRmw8Add(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long

expect inline fun I64AtomicRmw8Sub(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long

expect inline fun I64AtomicRmw8And(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long

expect inline fun I64AtomicRmw8Or(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long

expect inline fun I64AtomicRmw8Xor(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long

expect inline fun I64AtomicRmw8Exchange(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long

expect inline fun I64AtomicRmw16Add(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long

expect inline fun I64AtomicRmw16Sub(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long

expect inline fun I64AtomicRmw16And(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long

expect inline fun I64AtomicRmw16Or(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long

expect inline fun I64AtomicRmw16Xor(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long

expect inline fun I64AtomicRmw16Exchange(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long

expect inline fun I64AtomicRmw32Add(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long

expect inline fun I64AtomicRmw32Sub(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long

expect inline fun I64AtomicRmw32And(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long

expect inline fun I64AtomicRmw32Or(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long

expect inline fun I64AtomicRmw32Xor(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long

expect inline fun I64AtomicRmw32Exchange(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Long

