package io.github.charlietap.chasm.decoder.section

import io.github.charlietap.chasm.ast.module.Export
import kotlin.jvm.JvmInline

@JvmInline
internal value class ExportSection(val exports: List<Export>) : Section
