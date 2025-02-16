package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ir.instruction.TableInstruction as IRTableInstruction
import io.github.charlietap.chasm.ir.module.Index.ElementIndex as IRElementIndex
import io.github.charlietap.chasm.ir.module.Index.TableIndex as IRTableIndex

internal fun TableInstructionFactory(
    instruction: TableInstruction,
): IRTableInstruction = TableInstructionFactory(
    instruction = instruction,
    tableIndexFactory = ::TableIndexFactory,
    elementIndexFactory = ::ElementIndexFactory,
)

internal inline fun TableInstructionFactory(
    instruction: TableInstruction,
    tableIndexFactory: IRFactory<Index.TableIndex, IRTableIndex>,
    elementIndexFactory: IRFactory<Index.ElementIndex, IRElementIndex>,
): IRTableInstruction {
    return when (instruction) {
        is TableInstruction.TableGet -> IRTableInstruction.TableGet(
            tableIdx = tableIndexFactory(instruction.tableIdx),
        )

        is TableInstruction.TableSet -> IRTableInstruction.TableSet(
            tableIdx = tableIndexFactory(instruction.tableIdx),
        )

        is TableInstruction.TableInit -> IRTableInstruction.TableInit(
            elemIdx = elementIndexFactory(instruction.elemIdx),
            tableIdx = tableIndexFactory(instruction.tableIdx),
        )

        is TableInstruction.ElemDrop -> IRTableInstruction.ElemDrop(
            elemIdx = elementIndexFactory(instruction.elemIdx),
        )

        is TableInstruction.TableCopy -> IRTableInstruction.TableCopy(
            srcTableIdx = tableIndexFactory(instruction.srcTableIdx),
            destTableIdx = tableIndexFactory(instruction.destTableIdx),
        )

        is TableInstruction.TableGrow -> IRTableInstruction.TableGrow(
            tableIdx = tableIndexFactory(instruction.tableIdx),
        )

        is TableInstruction.TableSize -> IRTableInstruction.TableSize(
            tableIdx = tableIndexFactory(instruction.tableIdx),
        )

        is TableInstruction.TableFill -> IRTableInstruction.TableFill(
            tableIdx = tableIndexFactory(instruction.tableIdx),
        )
    }
}
