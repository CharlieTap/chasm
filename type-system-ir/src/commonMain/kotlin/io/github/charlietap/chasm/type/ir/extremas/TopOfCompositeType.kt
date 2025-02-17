package io.github.charlietap.chasm.type.ir.extremas

import io.github.charlietap.chasm.ir.type.AbstractHeapType
import io.github.charlietap.chasm.ir.type.CompositeType
import io.github.charlietap.chasm.ir.type.DefinedType
import io.github.charlietap.chasm.ir.type.HeapType

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
