package io.github.charlietap.chasm.fixture.runtime.instance

import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.type.TableType

fun tableInstance(
    type: TableType = tableType(),
    elements: LongArray = longArrayOf(),
) = TableInstance(
    type = type,
    elements = elements,
)
