package io.github.charlietap.chasm.type.ir.extremas

import io.github.charlietap.chasm.ir.type.AbstractHeapType
import io.github.charlietap.chasm.ir.type.DefinedType
import io.github.charlietap.chasm.ir.type.HeapType

@Suppress("UNUSED_PARAMETER")
internal fun TopOfAbstractHeapType(
    type: AbstractHeapType,
    types: List<DefinedType>,
): HeapType? = when (type) {
    is AbstractHeapType.Any,
    is AbstractHeapType.None,
    is AbstractHeapType.Eq,
    is AbstractHeapType.Struct,
    is AbstractHeapType.Array,
    is AbstractHeapType.I31,
    -> AbstractHeapType.Any
    is AbstractHeapType.Func,
    is AbstractHeapType.NoFunc,
    -> AbstractHeapType.Func
    is AbstractHeapType.Exception,
    is AbstractHeapType.NoException,
    -> AbstractHeapType.Exception
    is AbstractHeapType.Extern,
    is AbstractHeapType.NoExtern,
    -> AbstractHeapType.Extern
    is AbstractHeapType.Bottom -> null
}
