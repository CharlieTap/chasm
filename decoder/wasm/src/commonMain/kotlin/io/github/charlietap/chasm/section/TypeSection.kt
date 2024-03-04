package io.github.charlietap.chasm.section

import io.github.charlietap.chasm.ast.module.Type
import kotlin.jvm.JvmInline

@JvmInline
value class TypeSection(val types: List<Type>) : Section
