package io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop

// Shared scalar semantics for frame executors and generated Kotlin values.
internal inline fun valueI32Eq(left: Int, right: Int): Int = if (left == right) 1 else 0

internal inline fun valueI32Ne(left: Int, right: Int): Int = if (left != right) 1 else 0

internal inline fun valueI32LtS(left: Int, right: Int): Int = if (left < right) 1 else 0

internal inline fun valueI32LtU(left: Int, right: Int): Int = if (left.toUInt() < right.toUInt()) 1 else 0

internal inline fun valueI32GtS(left: Int, right: Int): Int = if (left > right) 1 else 0

internal inline fun valueI32GtU(left: Int, right: Int): Int = if (left.toUInt() > right.toUInt()) 1 else 0

internal inline fun valueI32LeS(left: Int, right: Int): Int = if (left <= right) 1 else 0

internal inline fun valueI32LeU(left: Int, right: Int): Int = if (left.toUInt() <= right.toUInt()) 1 else 0

internal inline fun valueI32GeS(left: Int, right: Int): Int = if (left >= right) 1 else 0

internal inline fun valueI32GeU(left: Int, right: Int): Int = if (left.toUInt() >= right.toUInt()) 1 else 0
