package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.type.TableType

fun tableInstance(
    type: TableType = tableType(),
    elements: LongArray = longArrayOf(),
) = TableInstance(
    type = type,
    elements = elements,
)
