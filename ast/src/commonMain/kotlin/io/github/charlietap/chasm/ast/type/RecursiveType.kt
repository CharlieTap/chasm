package io.github.charlietap.chasm.ast.type

import kotlin.jvm.JvmInline

@JvmInline
value class RecursiveType(val subTypes: List<SubType>) : Type
