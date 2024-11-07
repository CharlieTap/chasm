package io.github.charlietap.chasm.ast.type

data class LocalType(
    val status: InitializationStatus,
    val type: ValueType,
)
