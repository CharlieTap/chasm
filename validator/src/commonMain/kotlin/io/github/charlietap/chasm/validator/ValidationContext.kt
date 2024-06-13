package io.github.charlietap.chasm.validator

import io.github.charlietap.chasm.ast.module.Module
import kotlin.jvm.JvmInline

@JvmInline
internal value class ValidationContext(val module: Module)
