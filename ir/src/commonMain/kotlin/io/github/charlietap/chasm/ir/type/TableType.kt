package io.github.charlietap.chasm.ir.type

data class TableType(
    val referenceType: ReferenceType,
    val limits: Limits,
) : Type
