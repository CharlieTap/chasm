package io.github.charlietap.chasm.executor.invoker.dispatch.tablefused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.tablefused.TableCopyExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.tablefused.TableFillExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.tablefused.TableGetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.tablefused.TableGrowExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.tablefused.TableInitExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.tablefused.TableSetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.tablefused.TableSizeExecutor
import io.github.charlietap.chasm.runtime.instruction.TableSuperInstruction

fun TableCopyDispatcher(instruction: TableSuperInstruction.TableCopyIii) = dispatchInstruction(instruction, ::TableCopyExecutor)

fun TableCopyDispatcher(instruction: TableSuperInstruction.TableCopyIis) = dispatchInstruction(instruction, ::TableCopyExecutor)

fun TableCopyDispatcher(instruction: TableSuperInstruction.TableCopyIsi) = dispatchInstruction(instruction, ::TableCopyExecutor)

fun TableCopyDispatcher(instruction: TableSuperInstruction.TableCopyIss) = dispatchInstruction(instruction, ::TableCopyExecutor)

fun TableCopyDispatcher(instruction: TableSuperInstruction.TableCopySii) = dispatchInstruction(instruction, ::TableCopyExecutor)

fun TableCopyDispatcher(instruction: TableSuperInstruction.TableCopySis) = dispatchInstruction(instruction, ::TableCopyExecutor)

fun TableCopyDispatcher(instruction: TableSuperInstruction.TableCopySsi) = dispatchInstruction(instruction, ::TableCopyExecutor)

fun TableCopyDispatcher(instruction: TableSuperInstruction.TableCopySss) = dispatchInstruction(instruction, ::TableCopyExecutor)

fun TableFillDispatcher(instruction: TableSuperInstruction.TableFillIii) = dispatchInstruction(instruction, ::TableFillExecutor)

fun TableFillDispatcher(instruction: TableSuperInstruction.TableFillIis) = dispatchInstruction(instruction, ::TableFillExecutor)

fun TableFillDispatcher(instruction: TableSuperInstruction.TableFillIsi) = dispatchInstruction(instruction, ::TableFillExecutor)

fun TableFillDispatcher(instruction: TableSuperInstruction.TableFillIss) = dispatchInstruction(instruction, ::TableFillExecutor)

fun TableFillDispatcher(instruction: TableSuperInstruction.TableFillSii) = dispatchInstruction(instruction, ::TableFillExecutor)

fun TableFillDispatcher(instruction: TableSuperInstruction.TableFillSis) = dispatchInstruction(instruction, ::TableFillExecutor)

fun TableFillDispatcher(instruction: TableSuperInstruction.TableFillSsi) = dispatchInstruction(instruction, ::TableFillExecutor)

fun TableFillDispatcher(instruction: TableSuperInstruction.TableFillSss) = dispatchInstruction(instruction, ::TableFillExecutor)

fun TableGrowDispatcher(instruction: TableSuperInstruction.TableGrowIi) = dispatchInstruction(instruction, ::TableGrowExecutor)

fun TableGrowDispatcher(instruction: TableSuperInstruction.TableGrowIs) = dispatchInstruction(instruction, ::TableGrowExecutor)

fun TableGrowDispatcher(instruction: TableSuperInstruction.TableGrowSi) = dispatchInstruction(instruction, ::TableGrowExecutor)

fun TableGrowDispatcher(instruction: TableSuperInstruction.TableGrowSs) = dispatchInstruction(instruction, ::TableGrowExecutor)

fun TableInitDispatcher(instruction: TableSuperInstruction.TableInitIii) = dispatchInstruction(instruction, ::TableInitExecutor)

fun TableInitDispatcher(instruction: TableSuperInstruction.TableInitIis) = dispatchInstruction(instruction, ::TableInitExecutor)

fun TableInitDispatcher(instruction: TableSuperInstruction.TableInitIsi) = dispatchInstruction(instruction, ::TableInitExecutor)

fun TableInitDispatcher(instruction: TableSuperInstruction.TableInitIss) = dispatchInstruction(instruction, ::TableInitExecutor)

fun TableInitDispatcher(instruction: TableSuperInstruction.TableInitSii) = dispatchInstruction(instruction, ::TableInitExecutor)

fun TableInitDispatcher(instruction: TableSuperInstruction.TableInitSis) = dispatchInstruction(instruction, ::TableInitExecutor)

fun TableInitDispatcher(instruction: TableSuperInstruction.TableInitSsi) = dispatchInstruction(instruction, ::TableInitExecutor)

fun TableInitDispatcher(instruction: TableSuperInstruction.TableInitSss) = dispatchInstruction(instruction, ::TableInitExecutor)

fun TableGetDispatcher(instruction: TableSuperInstruction.TableGetI) = dispatchInstruction(instruction, ::TableGetExecutor)

fun TableGetDispatcher(instruction: TableSuperInstruction.TableGetS) = dispatchInstruction(instruction, ::TableGetExecutor)

fun TableSetDispatcher(instruction: TableSuperInstruction.TableSetIi) = dispatchInstruction(instruction, ::TableSetExecutor)

fun TableSetDispatcher(instruction: TableSuperInstruction.TableSetIs) = dispatchInstruction(instruction, ::TableSetExecutor)

fun TableSetDispatcher(instruction: TableSuperInstruction.TableSetSi) = dispatchInstruction(instruction, ::TableSetExecutor)

fun TableSetDispatcher(instruction: TableSuperInstruction.TableSetSs) = dispatchInstruction(instruction, ::TableSetExecutor)

fun TableSizeDispatcher(instruction: TableSuperInstruction.TableSizeS) = dispatchInstruction(instruction, ::TableSizeExecutor)
