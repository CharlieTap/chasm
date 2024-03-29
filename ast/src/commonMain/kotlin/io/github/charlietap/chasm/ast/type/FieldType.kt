package io.github.charlietap.chasm.ast.type

import kotlin.jvm.JvmInline

sealed interface FieldType : Type {

    @JvmInline
    value class MutableStorageType(val storageType: StorageType) : FieldType

    @JvmInline
    value class ImmutableStorageType(val storageType: StorageType) : FieldType
}
