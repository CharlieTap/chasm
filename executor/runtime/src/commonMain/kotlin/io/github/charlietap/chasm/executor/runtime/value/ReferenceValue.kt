package io.github.charlietap.chasm.executor.runtime.value

import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.executor.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.executor.runtime.instance.StructInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import kotlin.jvm.JvmInline

sealed interface ReferenceValue : ExecutionValue {

    data class Null(val heapType: HeapType) : ReferenceValue {
        override fun equals(other: Any?): Boolean = other is Null

        override fun hashCode(): Int = 0
    }

    @JvmInline
    value class I31(val value: UInt) : ReferenceValue

    @JvmInline
    value class Struct(val instance: StructInstance) : ReferenceValue

    @JvmInline
    value class Array(val instance: ArrayInstance) : ReferenceValue

    @JvmInline
    value class Function(val address: Address.Function) : ReferenceValue

    @JvmInline
    value class Host(val address: Any?) : ReferenceValue

    @JvmInline
    value class Exception(val address: Address.Exception) : ReferenceValue

    @JvmInline
    value class Extern(val referenceValue: ReferenceValue) : ReferenceValue
}
