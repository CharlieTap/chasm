package io.github.charlietap.chasm.section

import io.github.charlietap.chasm.ast.module.Global
import kotlin.jvm.JvmInline

@JvmInline
value class GlobalSection(val globals: List<Global>) : Section
