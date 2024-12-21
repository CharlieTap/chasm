package io.github.charlietap.chasm.fixture.executor.runtime.instruction

import io.github.charlietap.chasm.ast.module.Index.ElementIndex
import io.github.charlietap.chasm.ast.module.Index.TableIndex
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction
import io.github.charlietap.chasm.fixture.ast.module.elementIndex
import io.github.charlietap.chasm.fixture.ast.module.tableIndex
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
    elemIdx: ElementIndex = elementIndex(),
    table: TableInstance = tableInstance(),
) = TableInstruction.TableInit(
    elemIdx = elemIdx,
    table = table,
)

fun elemDropRuntimeInstruction(
    elemIdx: ElementIndex = elementIndex(),
) = TableInstruction.ElemDrop(
    elemIdx = elemIdx,
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
    tableIdx: TableIndex = tableIndex(),
) = TableInstruction.TableSize(
    tableIdx = tableIdx,
)

fun tableFillRuntimeInstruction(
    tableIdx: TableIndex = tableIndex(),
) = TableInstruction.TableFill(
    tableIdx = tableIdx,
)
