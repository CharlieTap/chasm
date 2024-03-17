package io.github.charlietap.chasm.ast.type

import io.github.charlietap.chasm.ast.instruction.Index
import kotlin.jvm.JvmInline

sealed interface HeapType : Type {

    data object Func : HeapType

    data object Extern : HeapType

    @JvmInline
    value class TypeIndex(val index: Index.TypeIndex) : HeapType

    @JvmInline
    value class FuncType(val functionType: FunctionType) : HeapType

    data object Bottom : HeapType
}
