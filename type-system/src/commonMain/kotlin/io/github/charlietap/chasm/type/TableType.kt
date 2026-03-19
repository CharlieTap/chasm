package io.github.charlietap.chasm.type

data class TableType(
    val addressType: AddressType,
    var referenceType: ReferenceType,
    val limits: Limits,
) : Type
