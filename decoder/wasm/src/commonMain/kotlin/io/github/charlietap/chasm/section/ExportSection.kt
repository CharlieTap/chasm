package io.github.charlietap.chasm.section

import io.github.charlietap.chasm.ast.module.Export
import kotlin.jvm.JvmInline

@JvmInline
value class ExportSection(val exports: List<Export>) : Section
