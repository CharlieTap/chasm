package io.github.charlietap.chasm.type

data class MemoryType(
    val addressType: AddressType,
    val limits: Limits,
    val shared: SharedStatus,
) : Type
