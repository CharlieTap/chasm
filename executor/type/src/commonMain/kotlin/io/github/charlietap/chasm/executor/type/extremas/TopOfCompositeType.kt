package io.github.charlietap.chasm.executor.type.extremas

import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance

fun TopOfCompositeType(
    type: CompositeType,
    instance: ModuleInstance,
): HeapType? = TopOfCompositeType(
    type = type,
    instance = instance,
    topOfAbstractHeapType = ::TopOfAbstractHeapType,
)

internal fun TopOfCompositeType(
    type: CompositeType,
    instance: ModuleInstance,
    topOfAbstractHeapType: TopOf<AbstractHeapType>,
): HeapType? = when (type) {
    is CompositeType.Struct,
    is CompositeType.Array,
    -> AbstractHeapType.Struct
    is CompositeType.Function -> AbstractHeapType.Func
}.let { abstractHeapType ->
    topOfAbstractHeapType(abstractHeapType, instance)
}
