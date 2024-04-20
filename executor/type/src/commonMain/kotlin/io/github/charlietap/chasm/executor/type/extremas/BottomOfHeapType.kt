package io.github.charlietap.chasm.executor.type.extremas

import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance

fun BottomOfHeapType(
    type: HeapType,
    instance: ModuleInstance,
): HeapType? = BottomOfHeapType(
    type = type,
    instance = instance,
    bottomOfAbstractHeapType = ::BottomOfAbstractHeapType,
    bottomOfConcreteHeapType = ::BottomOfConcreteHeapType,
)

internal fun BottomOfHeapType(
    type: HeapType,
    instance: ModuleInstance,
    bottomOfAbstractHeapType: BottomOf<AbstractHeapType>,
    bottomOfConcreteHeapType: BottomOf<ConcreteHeapType>,
): HeapType? = when (type) {
    is AbstractHeapType -> bottomOfAbstractHeapType(type, instance)
    is ConcreteHeapType -> bottomOfConcreteHeapType(type, instance)
}
