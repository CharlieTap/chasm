package io.github.charlietap.chasm.ast.type

import kotlin.jvm.JvmInline

sealed interface CompositeType : Type {

    @JvmInline
    value class Function(val functionType: FunctionType) : CompositeType

    @JvmInline
    value class Struct(val structType: StructType) : CompositeType

    @JvmInline
    value class Array(val arrayType: ArrayType) : CompositeType
}
