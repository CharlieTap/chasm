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

fun TableCopyDispatcher(instruction: TableSuperInstruction.TableCopyIii) = dispatchInstruction { vstack, context -> TableCopyExecutor(vstack, context, instruction) }

fun TableCopyDispatcher(instruction: TableSuperInstruction.TableCopyIis) = dispatchInstruction { vstack, context -> TableCopyExecutor(vstack, context, instruction) }

fun TableCopyDispatcher(instruction: TableSuperInstruction.TableCopyIsi) = dispatchInstruction { vstack, context -> TableCopyExecutor(vstack, context, instruction) }

fun TableCopyDispatcher(instruction: TableSuperInstruction.TableCopyIss) = dispatchInstruction { vstack, context -> TableCopyExecutor(vstack, context, instruction) }

fun TableCopyDispatcher(instruction: TableSuperInstruction.TableCopySii) = dispatchInstruction { vstack, context -> TableCopyExecutor(vstack, context, instruction) }

fun TableCopyDispatcher(instruction: TableSuperInstruction.TableCopySis) = dispatchInstruction { vstack, context -> TableCopyExecutor(vstack, context, instruction) }

fun TableCopyDispatcher(instruction: TableSuperInstruction.TableCopySsi) = dispatchInstruction { vstack, context -> TableCopyExecutor(vstack, context, instruction) }

fun TableCopyDispatcher(instruction: TableSuperInstruction.TableCopySss) = dispatchInstruction { vstack, context -> TableCopyExecutor(vstack, context, instruction) }

fun TableFillDispatcher(instruction: TableSuperInstruction.TableFillIii) = dispatchInstruction { vstack, context -> TableFillExecutor(vstack, context, instruction) }

fun TableFillDispatcher(instruction: TableSuperInstruction.TableFillIis) = dispatchInstruction { vstack, context -> TableFillExecutor(vstack, context, instruction) }

fun TableFillDispatcher(instruction: TableSuperInstruction.TableFillIsi) = dispatchInstruction { vstack, context -> TableFillExecutor(vstack, context, instruction) }

fun TableFillDispatcher(instruction: TableSuperInstruction.TableFillIss) = dispatchInstruction { vstack, context -> TableFillExecutor(vstack, context, instruction) }

fun TableFillDispatcher(instruction: TableSuperInstruction.TableFillSii) = dispatchInstruction { vstack, context -> TableFillExecutor(vstack, context, instruction) }

fun TableFillDispatcher(instruction: TableSuperInstruction.TableFillSis) = dispatchInstruction { vstack, context -> TableFillExecutor(vstack, context, instruction) }

fun TableFillDispatcher(instruction: TableSuperInstruction.TableFillSsi) = dispatchInstruction { vstack, context -> TableFillExecutor(vstack, context, instruction) }

fun TableFillDispatcher(instruction: TableSuperInstruction.TableFillSss) = dispatchInstruction { vstack, context -> TableFillExecutor(vstack, context, instruction) }

fun TableGrowDispatcher(instruction: TableSuperInstruction.TableGrowIi) = dispatchInstruction { vstack, context -> TableGrowExecutor(vstack, context, instruction) }

fun TableGrowDispatcher(instruction: TableSuperInstruction.TableGrowIs) = dispatchInstruction { vstack, context -> TableGrowExecutor(vstack, context, instruction) }

fun TableGrowDispatcher(instruction: TableSuperInstruction.TableGrowSi) = dispatchInstruction { vstack, context -> TableGrowExecutor(vstack, context, instruction) }

fun TableGrowDispatcher(instruction: TableSuperInstruction.TableGrowSs) = dispatchInstruction { vstack, context -> TableGrowExecutor(vstack, context, instruction) }

fun TableInitDispatcher(instruction: TableSuperInstruction.TableInitIii) = dispatchInstruction { vstack, context -> TableInitExecutor(vstack, context, instruction) }

fun TableInitDispatcher(instruction: TableSuperInstruction.TableInitIis) = dispatchInstruction { vstack, context -> TableInitExecutor(vstack, context, instruction) }

fun TableInitDispatcher(instruction: TableSuperInstruction.TableInitIsi) = dispatchInstruction { vstack, context -> TableInitExecutor(vstack, context, instruction) }

fun TableInitDispatcher(instruction: TableSuperInstruction.TableInitIss) = dispatchInstruction { vstack, context -> TableInitExecutor(vstack, context, instruction) }

fun TableInitDispatcher(instruction: TableSuperInstruction.TableInitSii) = dispatchInstruction { vstack, context -> TableInitExecutor(vstack, context, instruction) }

fun TableInitDispatcher(instruction: TableSuperInstruction.TableInitSis) = dispatchInstruction { vstack, context -> TableInitExecutor(vstack, context, instruction) }

fun TableInitDispatcher(instruction: TableSuperInstruction.TableInitSsi) = dispatchInstruction { vstack, context -> TableInitExecutor(vstack, context, instruction) }

fun TableInitDispatcher(instruction: TableSuperInstruction.TableInitSss) = dispatchInstruction { vstack, context -> TableInitExecutor(vstack, context, instruction) }

fun TableGetDispatcher(instruction: TableSuperInstruction.TableGetI) = dispatchInstruction { vstack, context -> TableGetExecutor(vstack, context, instruction) }

fun TableGetDispatcher(instruction: TableSuperInstruction.TableGetS) = dispatchInstruction { vstack, context -> TableGetExecutor(vstack, context, instruction) }

fun TableSetDispatcher(instruction: TableSuperInstruction.TableSetIi) = dispatchInstruction { vstack, context -> TableSetExecutor(vstack, context, instruction) }

fun TableSetDispatcher(instruction: TableSuperInstruction.TableSetIs) = dispatchInstruction { vstack, context -> TableSetExecutor(vstack, context, instruction) }

fun TableSetDispatcher(instruction: TableSuperInstruction.TableSetSi) = dispatchInstruction { vstack, context -> TableSetExecutor(vstack, context, instruction) }

fun TableSetDispatcher(instruction: TableSuperInstruction.TableSetSs) = dispatchInstruction { vstack, context -> TableSetExecutor(vstack, context, instruction) }

fun TableSizeDispatcher(instruction: TableSuperInstruction.TableSizeS) = dispatchInstruction { vstack, context -> TableSizeExecutor(vstack, context, instruction) }
