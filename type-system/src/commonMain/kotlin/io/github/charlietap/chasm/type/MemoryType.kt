package io.github.charlietap.chasm.type

data class MemoryType(
    val limits: Limits,
    val shared: SharedStatus,
) : Type
