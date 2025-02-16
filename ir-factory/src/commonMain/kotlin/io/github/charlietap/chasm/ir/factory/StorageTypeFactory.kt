package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.type.PackedType
import io.github.charlietap.chasm.ast.type.StorageType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ir.type.PackedType as IRPackedType
import io.github.charlietap.chasm.ir.type.StorageType as IRStorageType
import io.github.charlietap.chasm.ir.type.ValueType as IRValueType

internal fun StorageTypeFactory(
    storageType: StorageType,
): IRStorageType = StorageTypeFactory(
    storageType = storageType,
    valueTypeFactory = ::ValueTypeFactory,
    packedTypeFactory = ::PackedTypeFactory,
)

internal inline fun StorageTypeFactory(
    storageType: StorageType,
    valueTypeFactory: IRFactory<ValueType, IRValueType>,
    packedTypeFactory: IRFactory<PackedType, IRPackedType>,
): IRStorageType {
    return when (storageType) {
        is StorageType.Value -> IRStorageType.Value(
            type = valueTypeFactory(storageType.type),
        )

        is StorageType.Packed -> IRStorageType.Packed(
            type = packedTypeFactory(storageType.type),
        )
    }
}
