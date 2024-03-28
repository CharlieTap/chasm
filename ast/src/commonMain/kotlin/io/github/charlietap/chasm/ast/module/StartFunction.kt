package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.ast.module.Index.FunctionIndex
import kotlin.jvm.JvmInline

@JvmInline
value class StartFunction(val idx: FunctionIndex)
