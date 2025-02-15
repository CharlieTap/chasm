package io.github.charlietap.chasm.ir.type

data class FunctionType(
    var params: ResultType,
    var results: ResultType,
) : Type
