@file:Suppress("FunctionName")

package io.github.charlietap.chasm.memory.atomic

import io.github.charlietap.chasm.runtime.memory.LinearMemory

expect inline fun I32AtomicRmwAdd(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int

expect inline fun I32AtomicRmwSub(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int

expect inline fun I32AtomicRmwAnd(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int

expect inline fun I32AtomicRmwOr(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int

expect inline fun I32AtomicRmwXor(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int

expect inline fun I32AtomicRmwExchange(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int

expect inline fun I32AtomicRmw8Add(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int

expect inline fun I32AtomicRmw8Sub(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int

expect inline fun I32AtomicRmw8And(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int

expect inline fun I32AtomicRmw8Or(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int

expect inline fun I32AtomicRmw8Xor(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int

expect inline fun I32AtomicRmw8Exchange(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int

expect inline fun I32AtomicRmw16Add(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int

expect inline fun I32AtomicRmw16Sub(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int

expect inline fun I32AtomicRmw16And(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int

expect inline fun I32AtomicRmw16Or(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int

expect inline fun I32AtomicRmw16Xor(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int

expect inline fun I32AtomicRmw16Exchange(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Int

