package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.ast.type.PackedType

fun packedType() = i8PackedType()

fun i8PackedType(
    value: Byte = 0,
) = PackedType.I8(value)

fun i16PackedType(
    value: Short = 0,
) = PackedType.I16(value)
