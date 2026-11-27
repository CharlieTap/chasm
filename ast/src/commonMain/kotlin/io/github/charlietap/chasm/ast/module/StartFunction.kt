package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.module.Index.FunctionIndex
import kotlin.jvm.JvmInline

@InternalChasmApi
@JvmInline
value class StartFunction(val idx: FunctionIndex)
