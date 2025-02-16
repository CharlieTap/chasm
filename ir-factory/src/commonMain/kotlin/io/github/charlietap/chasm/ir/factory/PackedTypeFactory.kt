package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.type.PackedType
import io.github.charlietap.chasm.ir.type.PackedType as IRPackedType

internal inline fun PackedTypeFactory(
    packedType: PackedType,
): IRPackedType {
    return when (packedType) {
        PackedType.I8 -> IRPackedType.I8
        PackedType.I16 -> IRPackedType.I16
    }
}
