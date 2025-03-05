package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.runtime.value.NumberValue

fun NumberValue.I32.map(transform: (Int) -> Int): NumberValue.I32 = NumberValue.I32(transform(value))

fun NumberValue.I64.map(transform: (Long) -> Long): NumberValue.I64 = NumberValue.I64(transform(value))

fun NumberValue.F32.map(transform: (Float) -> Float): NumberValue.F32 = NumberValue.F32(transform(value))

fun NumberValue.F64.map(transform: (Double) -> Double): NumberValue.F64 = NumberValue.F64(transform(value))

fun NumberValue.I32.plus(b: NumberValue.I32): NumberValue.I32 = NumberValue.I32(value + b.value)

fun NumberValue.I32.minus(b: NumberValue.I32): NumberValue.I32 = NumberValue.I32(value - b.value)

fun NumberValue.I32.times(b: NumberValue.I32): NumberValue.I32 = NumberValue.I32(value * b.value)

fun NumberValue.I32.div(b: NumberValue.I32): NumberValue.I32 = NumberValue.I32(value / b.value)

fun NumberValue.I32.rem(b: NumberValue.I32): NumberValue.I32 = NumberValue.I32(value % b.value)

fun NumberValue.I64.plus(b: NumberValue.I64): NumberValue.I64 = NumberValue.I64(value + b.value)

fun NumberValue.I64.minus(b: NumberValue.I64): NumberValue.I64 = NumberValue.I64(value - b.value)

fun NumberValue.I64.times(b: NumberValue.I64): NumberValue.I64 = NumberValue.I64(value * b.value)

fun NumberValue.I64.div(b: NumberValue.I64): NumberValue.I64 = NumberValue.I64(value / b.value)

fun NumberValue.I64.rem(b: NumberValue.I64): NumberValue.I64 = NumberValue.I64(value % b.value)

fun NumberValue.F32.plus(b: NumberValue.F32): NumberValue.F32 = NumberValue.F32(value + b.value)

fun NumberValue.F32.minus(b: NumberValue.F32): NumberValue.F32 = NumberValue.F32(value - b.value)

fun NumberValue.F32.times(b: NumberValue.F32): NumberValue.F32 = NumberValue.F32(value * b.value)

fun NumberValue.F32.div(b: NumberValue.F32): NumberValue.F32 = NumberValue.F32(value / b.value)

fun NumberValue.F32.rem(b: NumberValue.F32): NumberValue.F32 = NumberValue.F32(value % b.value)

fun NumberValue.F64.plus(b: NumberValue.F64): NumberValue.F64 = NumberValue.F64(value + b.value)

fun NumberValue.F64.minus(b: NumberValue.F64): NumberValue.F64 = NumberValue.F64(value - b.value)

fun NumberValue.F64.times(b: NumberValue.F64): NumberValue.F64 = NumberValue.F64(value * b.value)

fun NumberValue.F64.div(b: NumberValue.F64): NumberValue.F64 = NumberValue.F64(value / b.value)

fun NumberValue.F64.rem(b: NumberValue.F64): NumberValue.F64 = NumberValue.F64(value % b.value)
