package io.github.charlietap.chasm.type.extremas

import io.github.charlietap.chasm.ir.type.AbstractHeapType
import io.github.charlietap.chasm.ir.type.ConcreteHeapType
import io.github.charlietap.chasm.ir.type.DefinedType
import io.github.charlietap.chasm.ir.type.HeapType

fun BottomOfHeapType(
    type: HeapType,
    types: List<DefinedType>,
): HeapType? = BottomOfHeapType(
    type = type,
    types = types,
    bottomOfAbstractHeapType = ::BottomOfAbstractHeapType,
    bottomOfConcreteHeapType = ::BottomOfConcreteHeapType,
)

internal fun BottomOfHeapType(
    type: HeapType,
    types: List<DefinedType>,
    bottomOfAbstractHeapType: BottomOf<AbstractHeapType>,
    bottomOfConcreteHeapType: BottomOf<ConcreteHeapType>,
): HeapType? = when (type) {
    is AbstractHeapType -> bottomOfAbstractHeapType(type, types)
    is ConcreteHeapType -> bottomOfConcreteHeapType(type, types)
}
