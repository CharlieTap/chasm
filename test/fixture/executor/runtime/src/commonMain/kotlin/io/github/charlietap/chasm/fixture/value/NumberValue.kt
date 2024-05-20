package io.github.charlietap.chasm.fixture.value

import io.github.charlietap.chasm.executor.runtime.value.NumberValue

fun i32(
    value: Int = 0,
) = NumberValue.I32(value)

fun i64(
    value: Long = 0L,
) = NumberValue.I64(value)

fun f32(
    value: Float = 0f,
) = NumberValue.F32(value)

fun f64(
    value: Double = 0.0,
) = NumberValue.F64(value)
