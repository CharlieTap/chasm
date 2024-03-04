package io.github.charlietap.chasm.section

import io.github.charlietap.chasm.ast.module.DataSegment
import kotlin.jvm.JvmInline

@JvmInline
value class DataSection(val segments: List<DataSegment>) : Section
