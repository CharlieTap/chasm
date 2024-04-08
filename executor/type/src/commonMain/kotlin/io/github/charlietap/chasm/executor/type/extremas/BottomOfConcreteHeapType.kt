package io.github.charlietap.chasm.executor.type.extremas

import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.executor.type.expansion.DefinedTypeExpanderImpl

fun BottomOfConcreteHeapType(
    type: ConcreteHeapType,
    instance: ModuleInstance,
): HeapType? = BottomOfConcreteHeapType(
    type = type,
    instance = instance,
    definedTypeExpander = ::DefinedTypeExpanderImpl,
    bottomOfCompositeType = ::BottomOfCompositeType,
)

internal fun BottomOfConcreteHeapType(
    type: ConcreteHeapType,
    instance: ModuleInstance,
    definedTypeExpander: DefinedTypeExpander,
    bottomOfCompositeType: BottomOf<CompositeType>,
): HeapType? = when (type) {
    is ConcreteHeapType.Defined -> bottomOfCompositeType(definedTypeExpander(type.definedType), instance)
    is ConcreteHeapType.TypeIndex -> bottomOfCompositeType(definedTypeExpander(instance.types[type.index.idx.toInt()]), instance)
    is ConcreteHeapType.RecursiveTypeIndex -> null
}
