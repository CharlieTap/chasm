package io.github.charlietap.chasm.type.extremas

import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.HeapType

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
