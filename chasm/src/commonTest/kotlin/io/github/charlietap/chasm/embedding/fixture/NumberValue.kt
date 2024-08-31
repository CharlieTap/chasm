package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.Value

fun publicI32(
    value: Int = 0,
) = Value.Number.I32(value)

fun publicI64(
    value: Long = 0L,
) = Value.Number.I64(value)

fun publicF32(
    value: Float = 0f,
) = Value.Number.F32(value)

fun publicF64(
    value: Double = 0.0,
) = Value.Number.F64(value)
