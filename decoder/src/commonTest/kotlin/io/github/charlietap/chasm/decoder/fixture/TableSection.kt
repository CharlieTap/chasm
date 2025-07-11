package io.github.charlietap.chasm.decoder.fixture

import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.decoder.section.TableSection

internal fun tableSection(
    tables: List<Table> = emptyList(),
) = TableSection(tables)
