package io.github.charlietap.chasm.type

data class TableType(
    val referenceType: ReferenceType,
    val limits: Limits,
) : Type
