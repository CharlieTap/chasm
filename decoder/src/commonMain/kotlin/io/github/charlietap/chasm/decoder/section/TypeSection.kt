package io.github.charlietap.chasm.decoder.section

import io.github.charlietap.chasm.ast.module.Type
import kotlin.jvm.JvmInline

@JvmInline
internal value class TypeSection(val types: List<Type>) : Section
