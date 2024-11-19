package io.github.charlietap.chasm.ast.type

data class MemoryType(
    val limits: Limits,
    val shared: SharedStatus,
) : Type
