package io.github.charlietap.chasm.fixture.executor.runtime.instruction

import io.github.charlietap.chasm.executor.runtime.instance.ElementInstance
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.instance.elementInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.tableInstance

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
) = TableInstruction.TableGrow(
    table = table,
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
