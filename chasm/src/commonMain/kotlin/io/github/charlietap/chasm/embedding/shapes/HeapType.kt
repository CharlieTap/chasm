package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.ast.type.ConcreteHeapType

sealed interface HeapType {

    data object Func : HeapType

    data object NoFunc : HeapType

    data object Exception : HeapType

    data object NoException : HeapType

    data object Extern : HeapType

    data object NoExtern : HeapType

    data object Any : HeapType

    data object Eq : HeapType

    data object Struct : HeapType

    data object Array : HeapType

    data object I31 : HeapType

    data object None : HeapType

    data object Bottom : HeapType

    class Concrete internal constructor(internal val heapType: ConcreteHeapType) : HeapType
}
