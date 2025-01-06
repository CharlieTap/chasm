package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.executor.runtime.value.NumberValue

fun publicI32(
    value: Int = 0,
) = NumberValue.I32(value)

fun publicI64(
    value: Long = 0L,
) = NumberValue.I64(value)

fun publicF32(
    value: Float = 0f,
) = NumberValue.F32(value)

fun publicF64(
    value: Double = 0.0,
) = NumberValue.F64(value)
