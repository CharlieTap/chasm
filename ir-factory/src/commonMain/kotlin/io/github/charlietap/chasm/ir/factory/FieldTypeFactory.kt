package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.Mutability
import io.github.charlietap.chasm.ast.type.StorageType
import io.github.charlietap.chasm.ir.type.FieldType as IRFieldType
import io.github.charlietap.chasm.ir.type.Mutability as IRMutability
import io.github.charlietap.chasm.ir.type.StorageType as IRStorageType

internal fun FieldTypeFactory(
    fieldType: FieldType,
): IRFieldType = FieldTypeFactory(
    fieldType = fieldType,
    storageTypeFactory = ::StorageTypeFactory,
    mutabilityFactory = ::MutabilityFactory,
)

internal inline fun FieldTypeFactory(
    fieldType: FieldType,
    storageTypeFactory: IRFactory<StorageType, IRStorageType>,
    mutabilityFactory: IRFactory<Mutability, IRMutability>,
): IRFieldType {
    return IRFieldType(
        storageType = storageTypeFactory(fieldType.storageType),
        mutability = mutabilityFactory(fieldType.mutability),
    )
}
