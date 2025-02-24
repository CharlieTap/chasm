package io.github.charlietap.chasm.type.extremas

import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.HeapType

internal fun TopOfCompositeType(
    type: CompositeType,
    types: List<DefinedType>,
): HeapType? = TopOfCompositeType(
    type = type,
    types = types,
    topOfAbstractHeapType = ::TopOfAbstractHeapType,
)

internal fun TopOfCompositeType(
    type: CompositeType,
    types: List<DefinedType>,
    topOfAbstractHeapType: TopOf<AbstractHeapType>,
): HeapType? = when (type) {
    is CompositeType.Struct,
    is CompositeType.Array,
    -> AbstractHeapType.Struct
    is CompositeType.Function -> AbstractHeapType.Func
}.let { abstractHeapType ->
    topOfAbstractHeapType(abstractHeapType, types)
}
