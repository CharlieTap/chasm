package io.github.charlietap.chasm.executor.invoker.dispatch.table

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.TableInstruction

fun TableInstructionDispatcher(
    instruction: TableInstruction,
): DispatchableInstruction = when (instruction) {
    is TableInstruction.TableCopyIii -> TableCopyDispatcher(instruction)
    is TableInstruction.TableCopyIis -> TableCopyDispatcher(instruction)
    is TableInstruction.TableCopyIsi -> TableCopyDispatcher(instruction)
    is TableInstruction.TableCopyIss -> TableCopyDispatcher(instruction)
    is TableInstruction.TableCopySii -> TableCopyDispatcher(instruction)
    is TableInstruction.TableCopySis -> TableCopyDispatcher(instruction)
    is TableInstruction.TableCopySsi -> TableCopyDispatcher(instruction)
    is TableInstruction.TableCopySss -> TableCopyDispatcher(instruction)
    is TableInstruction.TableFillIsi -> TableFillDispatcher(instruction)
    is TableInstruction.TableFillIss -> TableFillDispatcher(instruction)
    is TableInstruction.TableFillSsi -> TableFillDispatcher(instruction)
    is TableInstruction.TableFillSss -> TableFillDispatcher(instruction)
    is TableInstruction.TableGrowIs -> TableGrowDispatcher(instruction)
    is TableInstruction.TableGrowSs -> TableGrowDispatcher(instruction)
    is TableInstruction.TableInitIii -> TableInitDispatcher(instruction)
    is TableInstruction.TableInitIis -> TableInitDispatcher(instruction)
    is TableInstruction.TableInitIsi -> TableInitDispatcher(instruction)
    is TableInstruction.TableInitIss -> TableInitDispatcher(instruction)
    is TableInstruction.TableInitSii -> TableInitDispatcher(instruction)
    is TableInstruction.TableInitSis -> TableInitDispatcher(instruction)
    is TableInstruction.TableInitSsi -> TableInitDispatcher(instruction)
    is TableInstruction.TableInitSss -> TableInitDispatcher(instruction)
    is TableInstruction.TableGetI -> TableGetDispatcher(instruction)
    is TableInstruction.TableGetS -> TableGetDispatcher(instruction)
    is TableInstruction.TableSetSi -> TableSetDispatcher(instruction)
    is TableInstruction.TableSetSs -> TableSetDispatcher(instruction)
    is TableInstruction.TableSizeS -> TableSizeDispatcher(instruction)
    is TableInstruction.ElemDrop -> ElemDropDispatcher(instruction)
}
