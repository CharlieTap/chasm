package io.github.charlietap.chasm.fixture.ir.instruction

import io.github.charlietap.chasm.fixture.ir.module.elementIndex
import io.github.charlietap.chasm.fixture.ir.module.tableIndex
import io.github.charlietap.chasm.ir.instruction.TableInstruction
import io.github.charlietap.chasm.ir.module.Index.ElementIndex
import io.github.charlietap.chasm.ir.module.Index.TableIndex

fun tableInstruction(): TableInstruction = tableGetInstruction()

fun tableGetInstruction(
    tableIdx: TableIndex = tableIndex(),
) = TableInstruction.TableGet(
    tableIdx = tableIdx,
)

fun tableSetInstruction(
    tableIdx: TableIndex = tableIndex(),
) = TableInstruction.TableSet(
    tableIdx = tableIdx,
)

fun tableInitInstruction(
    elemIdx: ElementIndex = elementIndex(),
    tableIdx: TableIndex = tableIndex(),
) = TableInstruction.TableInit(
    elemIdx = elemIdx,
    tableIdx = tableIdx,
)

fun elemDropInstruction(
    elemIdx: ElementIndex = elementIndex(),
) = TableInstruction.ElemDrop(
    elemIdx = elemIdx,
)

fun tableCopyInstruction(
    srcTableIdx: TableIndex = tableIndex(),
    destTableIdx: TableIndex = tableIndex(),
) = TableInstruction.TableCopy(
    srcTableIdx = srcTableIdx,
    destTableIdx = destTableIdx,
)

fun tableGrowInstruction(
    tableIdx: TableIndex = tableIndex(),
) = TableInstruction.TableGrow(
    tableIdx = tableIdx,
)

fun tableSizeInstruction(
    tableIdx: TableIndex = tableIndex(),
) = TableInstruction.TableSize(
    tableIdx = tableIdx,
)

fun tableFillInstruction(
    tableIdx: TableIndex = tableIndex(),
) = TableInstruction.TableFill(
    tableIdx = tableIdx,
)
