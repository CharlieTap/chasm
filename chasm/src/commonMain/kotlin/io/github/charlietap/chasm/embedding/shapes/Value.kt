package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import kotlin.jvm.JvmInline

sealed interface Value {

    sealed interface Number<T> : Value {

        val value: T

        @JvmInline
        value class I32(override val value: Int) : Number<Int>

        @JvmInline
        value class I64(override val value: Long) : Number<Long>

        @JvmInline
        value class F32(override val value: Float) : Number<Float>

        @JvmInline
        value class F64(override val value: Double) : Number<Double>
    }

    sealed interface Reference : Value {

        class Array internal constructor(internal val value: ReferenceValue.Array) : Reference

        class Exception internal constructor(internal val value: ReferenceValue.Exception) : Reference

        @JvmInline
        value class Extern(val value: Reference) : Reference

        @JvmInline
        value class Func(val address: Int) : Reference

        @JvmInline
        value class Host(val value: Any?) : Reference

        class I31 internal constructor(internal val value: UInt) : Reference

        @JvmInline
        value class Null(val heapType: HeapType) : Reference

        class Struct internal constructor(internal val value: ReferenceValue.Struct) : Reference
    }
}
