package io.github.charlietap.chasm.runtime.ext

import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType

fun ValueType.defaultBits(): Long? = when (this) {
    is ValueType.Number -> 0L
    is ValueType.Vector -> 0L
    is ValueType.Reference -> when (val refType = this.referenceType) {
        is ReferenceType.RefNull -> {
            val substituted = when (val heapType = refType.heapType) {
                is ConcreteHeapType.Defined -> ConcreteHeapType.TypeIndex(heapType.definedType.typeIndex)
                else -> heapType
            }
            ReferenceValue.Null(substituted).toLongFromBoxed()
        }
        is ReferenceType.Ref -> ExecutionValue.Uninitialised.toLongFromBoxed()
    }
    is ValueType.Bottom -> null
}

fun FieldType.defaultBits(): Long? = when (val storageType = this.storageType) {
    is StorageType.Packed -> storageType.type.default()
    is StorageType.Value -> storageType.type.defaultBits()
}
