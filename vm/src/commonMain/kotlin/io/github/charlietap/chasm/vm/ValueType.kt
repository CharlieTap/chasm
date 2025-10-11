package io.github.charlietap.chasm.vm

import kotlin.jvm.JvmInline

enum class NumberType {
    I32,
    I64,
    F32,
    F64,
}

enum class VectorType {
    V128,
}

sealed interface ReferenceType {

    val heapType: HeapType

    @JvmInline
    value class Ref(override val heapType: HeapType) : ReferenceType

    @JvmInline
    value class RefNull(override val heapType: HeapType) : ReferenceType
}

sealed interface HeapType

sealed interface AbstractHeapType : HeapType {

    data object Func : AbstractHeapType

    data object NoFunc : AbstractHeapType

    data object Extern : AbstractHeapType

    data object NoExtern : AbstractHeapType

    data object Exception : AbstractHeapType

    data object NoException : AbstractHeapType

    data object Any : AbstractHeapType

    data object Eq : AbstractHeapType

    data object Struct : AbstractHeapType

    data object Array : AbstractHeapType

    data object I31 : AbstractHeapType

    data object None : AbstractHeapType

    data object Bottom : AbstractHeapType
}

sealed interface ValueType {
    @JvmInline
    value class Number(val numberType: NumberType) : ValueType

    @JvmInline
    value class Vector(val vectorType: VectorType) : ValueType

    @JvmInline
    value class Reference(val referenceType: ReferenceType) : ValueType
}
