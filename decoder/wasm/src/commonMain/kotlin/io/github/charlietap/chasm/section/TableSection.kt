package io.github.charlietap.chasm.section

import io.github.charlietap.chasm.ast.module.Table
import kotlin.jvm.JvmInline

@JvmInline
value class TableSection(val tables: List<Table>) : Section
