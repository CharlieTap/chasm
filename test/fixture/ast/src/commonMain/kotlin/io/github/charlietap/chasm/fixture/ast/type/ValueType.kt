package io.github.charlietap.chasm.fixture.ast.type

import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType

fun valueType(): ValueType = i32ValueType()

fun i32ValueType() = ValueType.Number(NumberType.I32)

fun i64ValueType() = ValueType.Number(NumberType.I64)

fun f32ValueType() = ValueType.Number(NumberType.F32)

fun f64ValueType() = ValueType.Number(NumberType.F64)

fun referenceValueType(
    referenceType: ReferenceType = referenceType(),
) = ValueType.Reference(
    referenceType = referenceType,
)