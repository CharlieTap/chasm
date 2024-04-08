package io.github.charlietap.chasm.fixture.value

import io.github.charlietap.chasm.executor.runtime.value.NumberValue

fun i32NumberValue(
    value: Int = 0,
) = NumberValue.I32(value)

fun i64NumberValue(
    value: Long = 0L,
) = NumberValue.I64(value)

fun f32NumberValue(
    value: Float = 0f,
) = NumberValue.F32(value)

fun f64NumberValue(
    value: Double = 0.0,
) = NumberValue.F64(value)
