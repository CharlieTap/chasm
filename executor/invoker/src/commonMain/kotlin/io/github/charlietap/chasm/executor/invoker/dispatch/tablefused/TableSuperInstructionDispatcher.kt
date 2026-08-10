package io.github.charlietap.chasm.executor.invoker.dispatch.tablefused

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.TableSuperInstruction

fun TableSuperInstructionDispatcher(
    instruction: TableSuperInstruction,
): DispatchableInstruction = when (instruction) {
    is TableSuperInstruction.TableCopyIii -> TableCopyDispatcher(instruction)
    is TableSuperInstruction.TableCopyIis -> TableCopyDispatcher(instruction)
    is TableSuperInstruction.TableCopyIsi -> TableCopyDispatcher(instruction)
    is TableSuperInstruction.TableCopyIss -> TableCopyDispatcher(instruction)
    is TableSuperInstruction.TableCopySii -> TableCopyDispatcher(instruction)
    is TableSuperInstruction.TableCopySis -> TableCopyDispatcher(instruction)
    is TableSuperInstruction.TableCopySsi -> TableCopyDispatcher(instruction)
    is TableSuperInstruction.TableCopySss -> TableCopyDispatcher(instruction)
    is TableSuperInstruction.TableFillIii -> TableFillDispatcher(instruction)
    is TableSuperInstruction.TableFillIis -> TableFillDispatcher(instruction)
    is TableSuperInstruction.TableFillIsi -> TableFillDispatcher(instruction)
    is TableSuperInstruction.TableFillIss -> TableFillDispatcher(instruction)
    is TableSuperInstruction.TableFillSii -> TableFillDispatcher(instruction)
    is TableSuperInstruction.TableFillSis -> TableFillDispatcher(instruction)
    is TableSuperInstruction.TableFillSsi -> TableFillDispatcher(instruction)
    is TableSuperInstruction.TableFillSss -> TableFillDispatcher(instruction)
    is TableSuperInstruction.TableGrowIi -> TableGrowDispatcher(instruction)
    is TableSuperInstruction.TableGrowIs -> TableGrowDispatcher(instruction)
    is TableSuperInstruction.TableGrowSi -> TableGrowDispatcher(instruction)
    is TableSuperInstruction.TableGrowSs -> TableGrowDispatcher(instruction)
    is TableSuperInstruction.TableInitIii -> TableInitDispatcher(instruction)
    is TableSuperInstruction.TableInitIis -> TableInitDispatcher(instruction)
    is TableSuperInstruction.TableInitIsi -> TableInitDispatcher(instruction)
    is TableSuperInstruction.TableInitIss -> TableInitDispatcher(instruction)
    is TableSuperInstruction.TableInitSii -> TableInitDispatcher(instruction)
    is TableSuperInstruction.TableInitSis -> TableInitDispatcher(instruction)
    is TableSuperInstruction.TableInitSsi -> TableInitDispatcher(instruction)
    is TableSuperInstruction.TableInitSss -> TableInitDispatcher(instruction)
    is TableSuperInstruction.TableGetI -> TableGetDispatcher(instruction)
    is TableSuperInstruction.TableGetS -> TableGetDispatcher(instruction)
    is TableSuperInstruction.TableSetIi -> TableSetDispatcher(instruction)
    is TableSuperInstruction.TableSetIs -> TableSetDispatcher(instruction)
    is TableSuperInstruction.TableSetSi -> TableSetDispatcher(instruction)
    is TableSuperInstruction.TableSetSs -> TableSetDispatcher(instruction)
    is TableSuperInstruction.TableSizeS -> TableSizeDispatcher(instruction)
}
