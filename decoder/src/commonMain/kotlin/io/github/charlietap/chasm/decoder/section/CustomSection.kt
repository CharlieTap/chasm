package io.github.charlietap.chasm.decoder.section

import io.github.charlietap.chasm.ast.module.Custom
import kotlin.jvm.JvmInline

@JvmInline
internal value class CustomSection(val custom: Custom) : Section
