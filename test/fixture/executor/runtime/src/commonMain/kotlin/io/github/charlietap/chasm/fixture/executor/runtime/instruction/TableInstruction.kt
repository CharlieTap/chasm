package io.github.charlietap.chasm.fixture.executor.runtime.instruction

import io.github.charlietap.chasm.ast.module.Index.ElementIndex
import io.github.charlietap.chasm.ast.module.Index.TableIndex
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction
import io.github.charlietap.chasm.fixture.ast.module.elementIndex
import io.github.charlietap.chasm.fixture.ast.module.tableIndex

fun tableRuntimeInstruction(): TableInstruction = tableGetRuntimeInstruction()

fun tableGetRuntimeInstruction(
    tableIdx: TableIndex = tableIndex(),
) = TableInstruction.TableGet(
    tableIdx = tableIdx,
)

fun tableSetRuntimeInstruction(
    tableIdx: TableIndex = tableIndex(),
) = TableInstruction.TableSet(
    tableIdx = tableIdx,
)

fun tableInitRuntimeInstruction(
    elemIdx: ElementIndex = elementIndex(),
    tableIdx: TableIndex = tableIndex(),
) = TableInstruction.TableInit(
    elemIdx = elemIdx,
    tableIdx = tableIdx,
)

fun elemDropRuntimeInstruction(
    elemIdx: ElementIndex = elementIndex(),
) = TableInstruction.ElemDrop(
    elemIdx = elemIdx,
)

fun tableCopyRuntimeInstruction(
    srcTableIdx: TableIndex = tableIndex(),
    destTableIdx: TableIndex = tableIndex(),
) = TableInstruction.TableCopy(
    srcTableIdx = srcTableIdx,
    destTableIdx = destTableIdx,
)

fun tableGrowRuntimeInstruction(
    tableIdx: TableIndex = tableIndex(),
) = TableInstruction.TableGrow(
    tableIdx = tableIdx,
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
