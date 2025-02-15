package io.github.charlietap.chasm.ir.type

data class MemoryType(
    val limits: Limits,
    val shared: SharedStatus,
) : Type
