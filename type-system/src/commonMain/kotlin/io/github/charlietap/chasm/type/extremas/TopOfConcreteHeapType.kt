package io.github.charlietap.chasm.type.extremas

import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.type.rolling.DefinedTypeUnroller

internal fun TopOfConcreteHeapType(
    type: ConcreteHeapType,
    types: List<DefinedType>,
): HeapType? = TopOfConcreteHeapType(
    type = type,
    types = types,
    definedTypeExpander = ::DefinedTypeExpander,
    definedTypeUnroller = ::DefinedTypeUnroller,
    topOfCompositeType = ::TopOfCompositeType,
)

internal fun TopOfConcreteHeapType(
    type: ConcreteHeapType,
    types: List<DefinedType>,
    definedTypeExpander: DefinedTypeExpander,
    definedTypeUnroller: DefinedTypeUnroller,
    topOfCompositeType: TopOf<CompositeType>,
): HeapType? = when (type) {
    is ConcreteHeapType.Defined -> topOfCompositeType(definedTypeExpander(type.definedType, definedTypeUnroller), types)
    is ConcreteHeapType.TypeIndex -> topOfCompositeType(definedTypeExpander(types[type.index], definedTypeUnroller), types)
    is ConcreteHeapType.RecursiveTypeIndex -> null
}
