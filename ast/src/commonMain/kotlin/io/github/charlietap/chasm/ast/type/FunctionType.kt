package io.github.charlietap.chasm.ast.type

data class FunctionType(
    var params: ResultType,
    var results: ResultType,
) : Type
