package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

data class TableInstance(
    val type: TableType,
    val elements: MutableList<ReferenceValue>,
)
