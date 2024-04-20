package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ValueType

fun valueType(): ValueType = i32NumberValueType()

fun i32NumberValueType() = ValueType.Number(NumberType.I32)

fun i64NumberValueType() = ValueType.Number(NumberType.I64)

fun f32NumberValueType() = ValueType.Number(NumberType.F32)

fun f64NumberValueType() = ValueType.Number(NumberType.F64)
