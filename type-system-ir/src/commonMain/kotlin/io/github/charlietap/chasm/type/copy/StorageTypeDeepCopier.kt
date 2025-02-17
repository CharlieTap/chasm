package io.github.charlietap.chasm.type.copy

import io.github.charlietap.chasm.ir.type.PackedType
import io.github.charlietap.chasm.ir.type.StorageType
import io.github.charlietap.chasm.ir.type.ValueType

fun StorageTypeDeepCopier(
    input: StorageType,
): StorageType =
    StorageTypeDeepCopier(
        input = input,
        packedTypeCopier = ::PackedTypeDeepCopier,
        valueTypeCopier = ::ValueTypeDeepCopier,
    )

inline fun StorageTypeDeepCopier(
    input: StorageType,
    packedTypeCopier: DeepCopier<PackedType>,
    valueTypeCopier: DeepCopier<ValueType>,
): StorageType = when (input) {
    is StorageType.Packed -> StorageType.Packed(
        packedTypeCopier(input.type),
    )
    is StorageType.Value -> StorageType.Value(
        valueTypeCopier(input.type),
    )
}
