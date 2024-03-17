package io.github.charlietap.chasm.decoder.wasm.section

import io.github.charlietap.chasm.ast.module.Import
import kotlin.jvm.JvmInline

@JvmInline
internal value class ImportSection(val imports: List<Import>) : Section
