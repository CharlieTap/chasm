package io.github.charlietap.chasm.ast.type

data class FunctionType(
    val params: ResultType,
    val results: ResultType,
) : Type
