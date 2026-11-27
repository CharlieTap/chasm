package io.github.charlietap.chasm.cir

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
sealed interface ValueType {

    data object I32 : ValueType

    data object I64 : ValueType

    data object F32 : ValueType

    data object F64 : ValueType

    data object V128 : ValueType

    data class Reference(
        val type: ReferenceType,
    ) : ValueType
}

@InternalChasmApi
data class ReferenceType(
    val nullable: Boolean,
    val heapType: HeapType,
)

@InternalChasmApi
sealed interface HeapType {

    data class Abstract(
        val type: AbstractHeapType,
    ) : HeapType

    data class Concrete(
        val type: TypeIndex,
    ) : HeapType
}

@InternalChasmApi
enum class AbstractHeapType(
    val text: String,
) {
    Func("func"),
    NoFunc("nofunc"),
    Extern("extern"),
    NoExtern("noextern"),
    Exception("exn"),
    NoException("noexn"),
    Any("any"),
    Eq("eq"),
    Struct("struct"),
    Array("array"),
    I31("i31"),
    None("none"),
}
