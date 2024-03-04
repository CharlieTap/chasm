package io.github.charlietap.chasm.ast.type

data class TableType(
    val referenceType: ReferenceType,
    val limits: Limits,
) : Type
