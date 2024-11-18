package io.github.charlietap.chasm.ast.type

data class MemoryType(
    val limits: Limits,
    val shared: Boolean,
) : Type
