package io.github.charlietap.chasm.executor.type.extremas

import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.executor.type.expansion.DefinedTypeExpanderImpl

fun TopOfConcreteHeapType(
    type: ConcreteHeapType,
    instance: ModuleInstance,
): HeapType? = TopOfConcreteHeapType(
    type = type,
    instance = instance,
    definedTypeExpander = ::DefinedTypeExpanderImpl,
    topOfCompositeType = ::TopOfCompositeType,
)

internal fun TopOfConcreteHeapType(
    type: ConcreteHeapType,
    instance: ModuleInstance,
    definedTypeExpander: DefinedTypeExpander,
    topOfCompositeType: TopOf<CompositeType>,
): HeapType? = when (type) {
    is ConcreteHeapType.Defined -> topOfCompositeType(definedTypeExpander(type.definedType), instance)
    is ConcreteHeapType.TypeIndex -> topOfCompositeType(definedTypeExpander(instance.types[type.index.idx.toInt()]), instance)
    is ConcreteHeapType.RecursiveTypeIndex -> null
}
