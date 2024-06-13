package io.github.charlietap.chasm.type.extremas

import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.HeapType

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
