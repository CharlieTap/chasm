package io.github.charlietap.chasm.vm

data class FunctionType(
    val params: List<ValueType>,
    val results: List<ValueType>,
)
