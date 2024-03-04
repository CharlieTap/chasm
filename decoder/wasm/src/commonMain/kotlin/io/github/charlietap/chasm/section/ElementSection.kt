package io.github.charlietap.chasm.section

import io.github.charlietap.chasm.ast.module.ElementSegment
import kotlin.jvm.JvmInline

@JvmInline
value class ElementSection(val segments: List<ElementSegment>) : Section
