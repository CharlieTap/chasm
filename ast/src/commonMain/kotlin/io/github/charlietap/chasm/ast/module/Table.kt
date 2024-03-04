package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.ast.instruction.Index.TableIndex
import io.github.charlietap.chasm.ast.type.TableType

data class Table(
    val idx: TableIndex,
    val type: TableType,
)
