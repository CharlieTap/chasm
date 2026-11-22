package io.github.charlietap.chasm.embedding.value

import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.reference.FunctionReference
import io.github.charlietap.chasm.reference.I31Reference
import io.github.charlietap.chasm.reference.StructReference
import io.github.charlietap.chasm.type.HeapType
import kotlin.jvm.JvmInline

sealed interface ExecutionValue

sealed interface NumberValue<T> : ExecutionValue {

    val value: T

    @JvmInline
    value class I32(override val value: Int) : NumberValue<Int>

    @JvmInline
    value class I64(override val value: Long) : NumberValue<Long>

    @JvmInline
    value class F32(override val value: Float) : NumberValue<Float>

    @JvmInline
    value class F64(override val value: Double) : NumberValue<Double>
}

sealed interface VectorValue : ExecutionValue {

    @JvmInline
    value class V128(val v128: ByteArray) : VectorValue
}

sealed interface ReferenceValue : ExecutionValue {

    class Null(val heapType: HeapType) : ReferenceValue {

        override fun equals(other: Any?): Boolean {
            return other is Null
        }

        override fun hashCode(): Int {
            return 0
        }

        override fun toString(): String {
            return "Null($heapType)"
        }
    }

    @JvmInline
    value class Struct(val reference: StructReference) : ReferenceValue

    @JvmInline
    value class Array(val reference: ArrayReference) : ReferenceValue

    @JvmInline
    value class I31(val reference: I31Reference) : ReferenceValue

    @JvmInline
    value class Function(val reference: FunctionReference) : ReferenceValue

    @JvmInline
    value class Exception(val reference: ExceptionReference) : ReferenceValue

    @JvmInline
    value class Extern(val value: Any) : ReferenceValue
}
