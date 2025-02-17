package io.github.charlietap.chasm.executor.runtime.encoder

import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.ir.type.AbstractHeapType
import io.github.charlietap.chasm.ir.type.BottomType
import io.github.charlietap.chasm.ir.type.ConcreteHeapType
import io.github.charlietap.chasm.ir.type.HeapType

typealias HeapTypeEncoder = (HeapType) -> Int
typealias HeapTypeDecoder = (Int) -> HeapType

inline fun HeapTypeEncoder(
    type: HeapType,
): Int = when (type) {
    AbstractHeapType.Func -> HEAP_TYPE_FUNC
    AbstractHeapType.NoFunc -> HEAP_TYPE_NO_FUNC
    AbstractHeapType.Extern -> HEAP_TYPE_EXTERN
    AbstractHeapType.NoExtern -> HEAP_TYPE_NO_EXTERN
    AbstractHeapType.Exception -> HEAP_TYPE_EXCEPTION
    AbstractHeapType.NoException -> HEAP_TYPE_NO_EXCEPTION
    AbstractHeapType.Any -> HEAP_TYPE_ANY
    AbstractHeapType.Eq -> HEAP_TYPE_EQ
    AbstractHeapType.Struct -> HEAP_TYPE_STRUCT
    AbstractHeapType.Array -> HEAP_TYPE_ARRAY
    AbstractHeapType.I31 -> HEAP_TYPE_I31
    AbstractHeapType.None -> HEAP_TYPE_NONE
    is AbstractHeapType.Bottom -> HEAP_TYPE_BOTTOM
    is ConcreteHeapType.TypeIndex -> {
        (type.index.idx shl 8) or HEAP_TYPE_HEAP_TYPE_INDEX
    }
    is ConcreteHeapType.RecursiveTypeIndex -> throw IllegalArgumentException("Cannot encode RecursiveTypeIndex on the stack")
    is ConcreteHeapType.Defined -> throw IllegalArgumentException("Cannot encode Defined type on the stack")
}

inline fun HeapTypeDecoder(
    encoded: Int,
): HeapType {
    val typeId = encoded and 0xFF
    val value = encoded shr 8

    return when (typeId) {
        HEAP_TYPE_FUNC -> AbstractHeapType.Func
        HEAP_TYPE_NO_FUNC -> AbstractHeapType.NoFunc
        HEAP_TYPE_EXTERN -> AbstractHeapType.Extern
        HEAP_TYPE_NO_EXTERN -> AbstractHeapType.NoExtern
        HEAP_TYPE_EXCEPTION -> AbstractHeapType.Exception
        HEAP_TYPE_NO_EXCEPTION -> AbstractHeapType.NoException
        HEAP_TYPE_ANY -> AbstractHeapType.Any
        HEAP_TYPE_EQ -> AbstractHeapType.Eq
        HEAP_TYPE_STRUCT -> AbstractHeapType.Struct
        HEAP_TYPE_ARRAY -> AbstractHeapType.Array
        HEAP_TYPE_I31 -> AbstractHeapType.I31
        HEAP_TYPE_NONE -> AbstractHeapType.None
        HEAP_TYPE_BOTTOM -> AbstractHeapType.Bottom(BottomType)
        HEAP_TYPE_HEAP_TYPE_INDEX -> ConcreteHeapType.TypeIndex(Index.TypeIndex(value))
        else -> throw IllegalArgumentException("Unknown HeapType encoding: $encoded")
    }
}

const val HEAP_TYPE_FUNC = 1
const val HEAP_TYPE_NO_FUNC = 2
const val HEAP_TYPE_EXTERN = 3
const val HEAP_TYPE_NO_EXTERN = 4
const val HEAP_TYPE_EXCEPTION = 5
const val HEAP_TYPE_NO_EXCEPTION = 6
const val HEAP_TYPE_ANY = 7
const val HEAP_TYPE_EQ = 8
const val HEAP_TYPE_STRUCT = 9
const val HEAP_TYPE_ARRAY = 10
const val HEAP_TYPE_I31 = 11
const val HEAP_TYPE_NONE = 12
const val HEAP_TYPE_BOTTOM = 13
const val HEAP_TYPE_HEAP_TYPE_INDEX = 14
