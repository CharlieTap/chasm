package io.github.charlietap.chasm.fixture.instance

import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.type.tableType

fun tableInstance(
    type: TableType = tableType(),
    elements: Array<ReferenceValue> = arrayOf(),
) = TableInstance(
    type = type,
    elements = elements,
)
