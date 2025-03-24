package io.github.charlietap.chasm.type.extremas

import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.type.rolling.DefinedTypeUnroller

internal fun BottomOfConcreteHeapType(
    type: ConcreteHeapType,
    types: List<DefinedType>,
): HeapType? = BottomOfConcreteHeapType(
    type = type,
    types = types,
    definedTypeExpander = ::DefinedTypeExpander,
    definedTypeUnroller = ::DefinedTypeUnroller,
    bottomOfCompositeType = ::BottomOfCompositeType,
)

internal fun BottomOfConcreteHeapType(
    type: ConcreteHeapType,
    types: List<DefinedType>,
    definedTypeExpander: DefinedTypeExpander,
    definedTypeUnroller: DefinedTypeUnroller,
    bottomOfCompositeType: BottomOf<CompositeType>,
): HeapType? = when (type) {
    is ConcreteHeapType.Defined -> bottomOfCompositeType(definedTypeExpander(type.definedType, definedTypeUnroller), types)
    is ConcreteHeapType.TypeIndex -> bottomOfCompositeType(definedTypeExpander(types[type.index], definedTypeUnroller), types)
    is ConcreteHeapType.RecursiveTypeIndex -> null
}
