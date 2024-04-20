package io.github.charlietap.chasm.executor.type.extremas

import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance

@Suppress("UNUSED_PARAMETER")
fun BottomOfAbstractHeapType(
    type: AbstractHeapType,
    instance: ModuleInstance,
): HeapType? = when (type) {
    is AbstractHeapType.Any,
    is AbstractHeapType.None,
    is AbstractHeapType.Eq,
    is AbstractHeapType.Struct,
    is AbstractHeapType.Array,
    is AbstractHeapType.I31,
    -> AbstractHeapType.None
    is AbstractHeapType.Func,
    is AbstractHeapType.NoFunc,
    -> AbstractHeapType.NoFunc
    is AbstractHeapType.Extern,
    is AbstractHeapType.NoExtern,
    -> AbstractHeapType.NoExtern
    is AbstractHeapType.Bottom -> null
}
