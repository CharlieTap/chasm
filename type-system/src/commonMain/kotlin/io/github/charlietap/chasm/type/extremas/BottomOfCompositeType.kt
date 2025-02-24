package io.github.charlietap.chasm.type.extremas

import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.HeapType

internal fun BottomOfCompositeType(
    type: CompositeType,
    types: List<DefinedType>,
): HeapType? = BottomOfCompositeType(
    type = type,
    types = types,
    bottomOfAbstractHeapType = ::BottomOfAbstractHeapType,
)

internal fun BottomOfCompositeType(
    type: CompositeType,
    types: List<DefinedType>,
    bottomOfAbstractHeapType: BottomOf<AbstractHeapType>,
): HeapType? = when (type) {
    is CompositeType.Struct,
    is CompositeType.Array,
    -> AbstractHeapType.Struct
    is CompositeType.Function -> AbstractHeapType.Func
}.let { abstractHeapType ->
    bottomOfAbstractHeapType(abstractHeapType, types)
}
