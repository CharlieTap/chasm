package io.github.charlietap.chasm.runtime.value

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.type.HeapType
import kotlin.jvm.JvmInline

sealed interface ReferenceValue : ExecutionValue {

    data class Null(val heapType: HeapType) : ReferenceValue {
        override fun equals(other: Any?): Boolean = other is Null

        override fun hashCode(): Int = 0
    }

    @JvmInline
    value class I31(val value: UInt) : ReferenceValue

    @JvmInline
    value class Struct(val address: Address.Struct) : ReferenceValue

    @JvmInline
    value class Array(val address: Address.Array) : ReferenceValue

    @JvmInline
    value class Function(val address: Address.Function) : ReferenceValue

    @JvmInline
    value class Host(val address: Address.Host) : ReferenceValue

    @JvmInline
    value class Exception(val address: Address.Exception) : ReferenceValue

    @JvmInline
    value class Extern(val referenceValue: ReferenceValue) : ReferenceValue
}
