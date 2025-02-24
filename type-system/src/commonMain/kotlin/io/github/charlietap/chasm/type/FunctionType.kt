package io.github.charlietap.chasm.type

data class FunctionType(
    var params: ResultType,
    var results: ResultType,
) : Type
