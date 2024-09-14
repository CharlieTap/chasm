package io.github.charlietap.chasm.decoder.section

import io.github.charlietap.chasm.ast.module.Tag
import kotlin.jvm.JvmInline

@JvmInline
internal value class TagSection(val tags: List<Tag>) : Section
