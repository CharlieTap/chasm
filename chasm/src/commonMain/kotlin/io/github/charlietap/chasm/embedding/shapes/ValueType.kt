package io.github.charlietap.chasm.embedding.shapes

import kotlin.jvm.JvmInline

sealed interface ValueType {

    sealed interface Number : ValueType {
        data object I32 : Number

        data object I64 : Number

        data object F32 : Number

        data object F64 : Number
    }

    sealed interface Reference : ValueType {

        val heapType: HeapType

        @JvmInline
        value class Ref(override val heapType: HeapType) : Reference

        @JvmInline
        value class RefNull(override val heapType: HeapType) : Reference
    }
}
