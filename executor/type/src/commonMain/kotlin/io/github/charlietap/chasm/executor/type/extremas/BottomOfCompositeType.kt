package io.github.charlietap.chasm.executor.type.extremas

import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance

fun BottomOfCompositeType(
    type: CompositeType,
    instance: ModuleInstance,
): HeapType? = BottomOfCompositeType(
    type = type,
    instance = instance,
    bottomOfAbstractHeapType = ::BottomOfAbstractHeapType,
)

internal fun BottomOfCompositeType(
    type: CompositeType,
    instance: ModuleInstance,
    bottomOfAbstractHeapType: BottomOf<AbstractHeapType>,
): HeapType? = when (type) {
    is CompositeType.Struct,
    is CompositeType.Array,
    -> AbstractHeapType.Struct
    is CompositeType.Function -> AbstractHeapType.Func
}.let { abstractHeapType ->
    bottomOfAbstractHeapType(abstractHeapType, instance)
}
