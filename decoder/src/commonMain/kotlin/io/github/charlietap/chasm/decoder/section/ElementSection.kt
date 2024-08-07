package io.github.charlietap.chasm.decoder.section

import io.github.charlietap.chasm.ast.module.ElementSegment
import kotlin.jvm.JvmInline

@JvmInline
internal value class ElementSection(val segments: List<ElementSegment>) : Section
