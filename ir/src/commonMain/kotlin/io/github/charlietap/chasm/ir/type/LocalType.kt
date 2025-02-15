package io.github.charlietap.chasm.ir.type

data class LocalType(
    val status: InitializationStatus,
    val type: ValueType,
)
