package io.github.charlietap.chasm.ast.type

import io.github.charlietap.chasm.ast.module.Index
import kotlin.jvm.JvmInline

sealed interface HeapType : Type

sealed interface AbstractHeapType : HeapType {

    data object Func : AbstractHeapType

    data object NoFunc : AbstractHeapType

    data object Extern : AbstractHeapType

    data object NoExtern : AbstractHeapType

    data object Exception : AbstractHeapType

    data object NoException : AbstractHeapType

    data object Any : AbstractHeapType

    data object Eq : AbstractHeapType

    data object Struct : AbstractHeapType

    data object Array : AbstractHeapType

    data object I31 : AbstractHeapType

    data object None : AbstractHeapType

    @JvmInline
    value class Bottom(val bottomType: BottomType) : AbstractHeapType
}

sealed interface ConcreteHeapType : HeapType {

    @JvmInline
    value class TypeIndex(val index: Index.TypeIndex) : ConcreteHeapType

    @JvmInline
    value class RecursiveTypeIndex(val index: Int) : ConcreteHeapType

    data class Defined(val definedType: DefinedType) : ConcreteHeapType
}
