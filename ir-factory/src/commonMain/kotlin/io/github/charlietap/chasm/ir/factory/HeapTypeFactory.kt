package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ir.module.Index.TypeIndex as IRTypeIndex
import io.github.charlietap.chasm.ir.type.AbstractHeapType as IRAbstractHeapType
import io.github.charlietap.chasm.ir.type.BottomType as IRBottomType
import io.github.charlietap.chasm.ir.type.ConcreteHeapType as IRConcreteHeapType
import io.github.charlietap.chasm.ir.type.DefinedType as IRDefinedType
import io.github.charlietap.chasm.ir.type.HeapType as IRHeapType

internal fun HeapTypeFactory(
    heapType: HeapType,
): IRHeapType = HeapTypeFactory(
    heapType = heapType,
    typeIndexFactory = ::TypeIndexFactory,
    definedTypeFactory = ::DefinedTypeFactory,
)

internal inline fun HeapTypeFactory(
    heapType: HeapType,
    typeIndexFactory: IRFactory<Index.TypeIndex, IRTypeIndex>,
    definedTypeFactory: IRFactory<DefinedType, IRDefinedType>,
): IRHeapType {
    return when (heapType) {
        AbstractHeapType.Func -> IRAbstractHeapType.Func
        AbstractHeapType.NoFunc -> IRAbstractHeapType.NoFunc
        AbstractHeapType.Extern -> IRAbstractHeapType.Extern
        AbstractHeapType.NoExtern -> IRAbstractHeapType.NoExtern
        AbstractHeapType.Exception -> IRAbstractHeapType.Exception
        AbstractHeapType.NoException -> IRAbstractHeapType.NoException
        AbstractHeapType.Any -> IRAbstractHeapType.Any
        AbstractHeapType.Eq -> IRAbstractHeapType.Eq
        AbstractHeapType.Struct -> IRAbstractHeapType.Struct
        AbstractHeapType.Array -> IRAbstractHeapType.Array
        AbstractHeapType.I31 -> IRAbstractHeapType.I31
        AbstractHeapType.None -> IRAbstractHeapType.None

        is AbstractHeapType.Bottom -> IRAbstractHeapType.Bottom(
            bottomType = IRBottomType,
        )

        is ConcreteHeapType.TypeIndex -> IRConcreteHeapType.TypeIndex(
            index = typeIndexFactory(heapType.index),
        )

        is ConcreteHeapType.RecursiveTypeIndex -> IRConcreteHeapType.RecursiveTypeIndex(
            index = heapType.index,
        )

        is ConcreteHeapType.Defined -> IRConcreteHeapType.Defined(
            definedType = definedTypeFactory(heapType.definedType),
        )
    }
}
