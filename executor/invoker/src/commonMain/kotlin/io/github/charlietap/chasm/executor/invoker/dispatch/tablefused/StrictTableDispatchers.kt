package io.github.charlietap.chasm.executor.invoker.dispatch.tablefused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.tablefused.TableCopyExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.tablefused.TableFillExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.tablefused.TableGetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.tablefused.TableGrowExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.tablefused.TableInitExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.tablefused.TableSetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.tablefused.TableSizeExecutor
import io.github.charlietap.chasm.runtime.instruction.FusedTableInstruction

fun TableCopyDispatcher(instruction: FusedTableInstruction.TableCopyIii) = dispatchInstruction(instruction, ::TableCopyExecutor)

fun TableCopyDispatcher(instruction: FusedTableInstruction.TableCopyIis) = dispatchInstruction(instruction, ::TableCopyExecutor)

fun TableCopyDispatcher(instruction: FusedTableInstruction.TableCopyIsi) = dispatchInstruction(instruction, ::TableCopyExecutor)

fun TableCopyDispatcher(instruction: FusedTableInstruction.TableCopyIss) = dispatchInstruction(instruction, ::TableCopyExecutor)

fun TableCopyDispatcher(instruction: FusedTableInstruction.TableCopySii) = dispatchInstruction(instruction, ::TableCopyExecutor)

fun TableCopyDispatcher(instruction: FusedTableInstruction.TableCopySis) = dispatchInstruction(instruction, ::TableCopyExecutor)

fun TableCopyDispatcher(instruction: FusedTableInstruction.TableCopySsi) = dispatchInstruction(instruction, ::TableCopyExecutor)

fun TableCopyDispatcher(instruction: FusedTableInstruction.TableCopySss) = dispatchInstruction(instruction, ::TableCopyExecutor)

fun TableFillDispatcher(instruction: FusedTableInstruction.TableFillIii) = dispatchInstruction(instruction, ::TableFillExecutor)

fun TableFillDispatcher(instruction: FusedTableInstruction.TableFillIis) = dispatchInstruction(instruction, ::TableFillExecutor)

fun TableFillDispatcher(instruction: FusedTableInstruction.TableFillIsi) = dispatchInstruction(instruction, ::TableFillExecutor)

fun TableFillDispatcher(instruction: FusedTableInstruction.TableFillIss) = dispatchInstruction(instruction, ::TableFillExecutor)

fun TableFillDispatcher(instruction: FusedTableInstruction.TableFillSii) = dispatchInstruction(instruction, ::TableFillExecutor)

fun TableFillDispatcher(instruction: FusedTableInstruction.TableFillSis) = dispatchInstruction(instruction, ::TableFillExecutor)

fun TableFillDispatcher(instruction: FusedTableInstruction.TableFillSsi) = dispatchInstruction(instruction, ::TableFillExecutor)

fun TableFillDispatcher(instruction: FusedTableInstruction.TableFillSss) = dispatchInstruction(instruction, ::TableFillExecutor)

fun TableGrowDispatcher(instruction: FusedTableInstruction.TableGrowIi) = dispatchInstruction(instruction, ::TableGrowExecutor)

fun TableGrowDispatcher(instruction: FusedTableInstruction.TableGrowIs) = dispatchInstruction(instruction, ::TableGrowExecutor)

fun TableGrowDispatcher(instruction: FusedTableInstruction.TableGrowSi) = dispatchInstruction(instruction, ::TableGrowExecutor)

fun TableGrowDispatcher(instruction: FusedTableInstruction.TableGrowSs) = dispatchInstruction(instruction, ::TableGrowExecutor)

fun TableInitDispatcher(instruction: FusedTableInstruction.TableInitIii) = dispatchInstruction(instruction, ::TableInitExecutor)

fun TableInitDispatcher(instruction: FusedTableInstruction.TableInitIis) = dispatchInstruction(instruction, ::TableInitExecutor)

fun TableInitDispatcher(instruction: FusedTableInstruction.TableInitIsi) = dispatchInstruction(instruction, ::TableInitExecutor)

fun TableInitDispatcher(instruction: FusedTableInstruction.TableInitIss) = dispatchInstruction(instruction, ::TableInitExecutor)

fun TableInitDispatcher(instruction: FusedTableInstruction.TableInitSii) = dispatchInstruction(instruction, ::TableInitExecutor)

fun TableInitDispatcher(instruction: FusedTableInstruction.TableInitSis) = dispatchInstruction(instruction, ::TableInitExecutor)

fun TableInitDispatcher(instruction: FusedTableInstruction.TableInitSsi) = dispatchInstruction(instruction, ::TableInitExecutor)

fun TableInitDispatcher(instruction: FusedTableInstruction.TableInitSss) = dispatchInstruction(instruction, ::TableInitExecutor)

fun TableGetDispatcher(instruction: FusedTableInstruction.TableGetI) = dispatchInstruction(instruction, ::TableGetExecutor)

fun TableGetDispatcher(instruction: FusedTableInstruction.TableGetS) = dispatchInstruction(instruction, ::TableGetExecutor)

fun TableSetDispatcher(instruction: FusedTableInstruction.TableSetIi) = dispatchInstruction(instruction, ::TableSetExecutor)

fun TableSetDispatcher(instruction: FusedTableInstruction.TableSetIs) = dispatchInstruction(instruction, ::TableSetExecutor)

fun TableSetDispatcher(instruction: FusedTableInstruction.TableSetSi) = dispatchInstruction(instruction, ::TableSetExecutor)

fun TableSetDispatcher(instruction: FusedTableInstruction.TableSetSs) = dispatchInstruction(instruction, ::TableSetExecutor)

fun TableSizeDispatcher(instruction: FusedTableInstruction.TableSizeS) = dispatchInstruction(instruction, ::TableSizeExecutor)
