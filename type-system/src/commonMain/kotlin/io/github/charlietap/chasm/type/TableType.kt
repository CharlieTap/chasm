package io.github.charlietap.chasm.type

data class TableType(
    val addressType: AddressType,
    val referenceType: ReferenceType,
    val limits: Limits,
) : Type
