package io.github.charlietap.chasm.type.extremas

import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.HeapType

@Suppress("UNUSED_PARAMETER")
internal fun BottomOfAbstractHeapType(
    type: AbstractHeapType,
    types: List<DefinedType>,
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
    is AbstractHeapType.Exception,
    is AbstractHeapType.NoException,
    -> AbstractHeapType.NoException
    is AbstractHeapType.Extern,
    is AbstractHeapType.NoExtern,
    -> AbstractHeapType.NoExtern
    is AbstractHeapType.Bottom -> null
}
