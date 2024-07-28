package io.github.charlietap.chasm.decoder.section

import io.github.charlietap.chasm.ast.module.DataSegment
import kotlin.jvm.JvmInline

@JvmInline
internal value class DataSection(val segments: List<DataSegment>) : Section
