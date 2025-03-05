package io.github.charlietap.chasm.fixture.runtime.instruction

import io.github.charlietap.chasm.fixture.runtime.instance.elementInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tableInstance
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.instruction.TableInstruction

fun tableRuntimeInstruction(): TableInstruction = tableGetRuntimeInstruction()

fun tableGetRuntimeInstruction(
    table: TableInstance = tableInstance(),
) = TableInstruction.TableGet(
    table = table,
)

fun tableSetRuntimeInstruction(
    table: TableInstance = tableInstance(),
) = TableInstruction.TableSet(
    table = table,
)

fun tableInitRuntimeInstruction(
    element: ElementInstance = elementInstance(),
    table: TableInstance = tableInstance(),
) = TableInstruction.TableInit(
    element = element,
    table = table,
)

fun elemDropRuntimeInstruction(
    element: ElementInstance = elementInstance(),
) = TableInstruction.ElemDrop(
    element = element,
)

fun tableCopyRuntimeInstruction(
    srcTable: TableInstance = tableInstance(),
    destTable: TableInstance = tableInstance(),
) = TableInstruction.TableCopy(
    srcTable = srcTable,
    destTable = destTable,
)

fun tableGrowRuntimeInstruction(
    table: TableInstance = tableInstance(),
    max: Int = 0,
) = TableInstruction.TableGrow(
    table = table,
    max = max,
)

fun tableSizeRuntimeInstruction(
    table: TableInstance = tableInstance(),
) = TableInstruction.TableSize(
    table = table,
)

fun tableFillRuntimeInstruction(
    table: TableInstance = tableInstance(),
) = TableInstruction.TableFill(
    table = table,
)
