package io.github.charlietap.chasm.fixture.instruction

import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.module.Index.ElementIndex
import io.github.charlietap.chasm.ast.module.Index.TableIndex
import io.github.charlietap.chasm.fixture.module.elementIndex
import io.github.charlietap.chasm.fixture.module.tableIndex

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
