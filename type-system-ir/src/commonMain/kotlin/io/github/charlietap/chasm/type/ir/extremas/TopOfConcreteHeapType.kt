package io.github.charlietap.chasm.type.ir.extremas

import io.github.charlietap.chasm.ir.type.CompositeType
import io.github.charlietap.chasm.ir.type.ConcreteHeapType
import io.github.charlietap.chasm.ir.type.DefinedType
import io.github.charlietap.chasm.ir.type.HeapType
import io.github.charlietap.chasm.type.ir.expansion.DefinedTypeExpander

internal fun TopOfConcreteHeapType(
    type: ConcreteHeapType,
    types: List<DefinedType>,
): HeapType? = TopOfConcreteHeapType(
    type = type,
    types = types,
    definedTypeExpander = ::DefinedTypeExpander,
    topOfCompositeType = ::TopOfCompositeType,
)

internal fun TopOfConcreteHeapType(
    type: ConcreteHeapType,
    types: List<DefinedType>,
    definedTypeExpander: DefinedTypeExpander,
    topOfCompositeType: TopOf<CompositeType>,
): HeapType? = when (type) {
    is ConcreteHeapType.Defined -> topOfCompositeType(definedTypeExpander(type.definedType), types)
    is ConcreteHeapType.TypeIndex -> topOfCompositeType(definedTypeExpander(types[type.index.idx]), types)
    is ConcreteHeapType.RecursiveTypeIndex -> null
}
