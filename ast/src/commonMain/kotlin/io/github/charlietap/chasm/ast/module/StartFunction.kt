package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.ast.instruction.Index.FunctionIndex
import kotlin.jvm.JvmInline

@JvmInline
value class StartFunction(val idx: FunctionIndex)
