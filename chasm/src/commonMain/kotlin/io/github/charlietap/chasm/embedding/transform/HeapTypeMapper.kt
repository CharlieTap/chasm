package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.BottomType
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.embedding.shapes.HeapType
import io.github.charlietap.chasm.ast.type.HeapType as InternalHeapType

internal object HeapTypeMapper : BidirectionalMapper<HeapType, InternalHeapType> {
    override fun map(input: HeapType): InternalHeapType {
        return when (input) {
            HeapType.Any -> AbstractHeapType.Any
            HeapType.Array -> AbstractHeapType.Array
            HeapType.Bottom -> AbstractHeapType.Bottom(BottomType)
            HeapType.Eq -> AbstractHeapType.Eq
            HeapType.Extern -> AbstractHeapType.Extern
            HeapType.Func -> AbstractHeapType.Func
            HeapType.I31 -> AbstractHeapType.I31
            HeapType.NoExtern -> AbstractHeapType.NoExtern
            HeapType.NoFunc -> AbstractHeapType.NoFunc
            HeapType.None -> AbstractHeapType.None
            HeapType.Struct -> AbstractHeapType.Struct
            is HeapType.Concrete -> input.heapType
        }
    }

    override fun bimap(input: InternalHeapType): HeapType {
        return when (input) {
            is AbstractHeapType -> {
                when (input) {
                    is AbstractHeapType.Any -> HeapType.Any
                    AbstractHeapType.Array -> HeapType.Array
                    is AbstractHeapType.Bottom -> HeapType.Bottom
                    AbstractHeapType.Eq -> HeapType.Eq
                    AbstractHeapType.Extern -> HeapType.Extern
                    AbstractHeapType.Func -> HeapType.Func
                    AbstractHeapType.I31 -> HeapType.I31
                    AbstractHeapType.NoExtern -> HeapType.NoExtern
                    AbstractHeapType.NoFunc -> HeapType.NoFunc
                    AbstractHeapType.None -> HeapType.None
                    AbstractHeapType.Struct -> HeapType.Struct
                }
            }
            is ConcreteHeapType -> {
                HeapType.Concrete(input)
            }
        }
    }
}
