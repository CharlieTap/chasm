package io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop

// Shared scalar semantics for frame executors and generated Kotlin values.
internal inline fun valueI64Eq(left: Long, right: Long): Long = if (left == right) 1L else 0L

internal inline fun valueI64Ne(left: Long, right: Long): Long = if (left != right) 1L else 0L

internal inline fun valueI64LtS(left: Long, right: Long): Long = if (left < right) 1L else 0L

internal inline fun valueI64LtU(left: Long, right: Long): Long = if (left.toULong() < right.toULong()) 1L else 0L

internal inline fun valueI64GtS(left: Long, right: Long): Long = if (left > right) 1L else 0L

internal inline fun valueI64GtU(left: Long, right: Long): Long = if (left.toULong() > right.toULong()) 1L else 0L

internal inline fun valueI64LeS(left: Long, right: Long): Long = if (left <= right) 1L else 0L

internal inline fun valueI64LeU(left: Long, right: Long): Long = if (left.toULong() <= right.toULong()) 1L else 0L

internal inline fun valueI64GeS(left: Long, right: Long): Long = if (left >= right) 1L else 0L

internal inline fun valueI64GeU(left: Long, right: Long): Long = if (left.toULong() >= right.toULong()) 1L else 0L
