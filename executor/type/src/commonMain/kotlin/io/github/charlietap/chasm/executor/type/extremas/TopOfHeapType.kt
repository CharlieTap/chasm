package io.github.charlietap.chasm.executor.type.extremas

import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance

fun TopOfHeapType(
    type: HeapType,
    instance: ModuleInstance,
): HeapType? = TopOfHeapType(
    type = type,
    instance = instance,
    topOfAbstractHeapType = ::TopOfAbstractHeapType,
    topOfConcreteHeapType = ::TopOfConcreteHeapType,
)

internal fun TopOfHeapType(
    type: HeapType,
    instance: ModuleInstance,
    topOfAbstractHeapType: TopOf<AbstractHeapType>,
    topOfConcreteHeapType: TopOf<ConcreteHeapType>,
): HeapType? = when (type) {
    is AbstractHeapType -> topOfAbstractHeapType(type, instance)
    is ConcreteHeapType -> topOfConcreteHeapType(type, instance)
}
