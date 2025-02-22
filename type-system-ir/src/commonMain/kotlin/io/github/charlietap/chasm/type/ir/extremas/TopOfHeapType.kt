package io.github.charlietap.chasm.type.ir.extremas

import io.github.charlietap.chasm.ir.type.AbstractHeapType
import io.github.charlietap.chasm.ir.type.ConcreteHeapType
import io.github.charlietap.chasm.ir.type.DefinedType
import io.github.charlietap.chasm.ir.type.HeapType

fun TopOfHeapType(
    type: HeapType,
    types: List<DefinedType>,
): HeapType? = TopOfHeapType(
    type = type,
    types = types,
    topOfAbstractHeapType = ::TopOfAbstractHeapType,
    topOfConcreteHeapType = ::TopOfConcreteHeapType,
)

internal fun TopOfHeapType(
    type: HeapType,
    types: List<DefinedType>,
    topOfAbstractHeapType: TopOf<AbstractHeapType>,
    topOfConcreteHeapType: TopOf<ConcreteHeapType>,
): HeapType? = when (type) {
    is AbstractHeapType -> topOfAbstractHeapType(type, types)
    is ConcreteHeapType -> topOfConcreteHeapType(type, types)
}
