package io.github.charlietap.chasm.section

import io.github.charlietap.chasm.ast.module.Custom
import kotlin.jvm.JvmInline

@JvmInline
value class CustomSection(val custom: Custom) : Section
