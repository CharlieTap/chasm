package io.github.charlietap.chasm.ast.type

import kotlin.jvm.JvmInline

@JvmInline
value class MemoryType(val limits: Limits) : Type
