package io.github.charlietap.chasm.decoder.wasm.section

import io.github.charlietap.chasm.ast.module.Table
import kotlin.jvm.JvmInline

@JvmInline
internal value class TableSection(val tables: List<Table>) : Section
