package io.github.charlietap.chasm.section

import io.github.charlietap.chasm.ast.module.Import
import kotlin.jvm.JvmInline

@JvmInline
value class ImportSection(val imports: List<Import>) : Section
