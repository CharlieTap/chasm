package io.github.charlietap.chasm.executor.invoker.dispatch.table

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.table.TableCopyExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.table.TableFillExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.table.TableGetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.table.TableGrowExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.table.TableInitExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.table.TableSetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.table.TableSizeExecutor
import io.github.charlietap.chasm.runtime.instruction.TableInstruction

fun TableCopyDispatcher(
    instruction: TableInstruction.TableCopyIii,
) = dispatchInstruction { vstack, context ->
    TableCopyExecutor(vstack, context, instruction)
}

fun TableCopyDispatcher(
    instruction: TableInstruction.TableCopyIis,
) = dispatchInstruction { vstack, context ->
    TableCopyExecutor(vstack, context, instruction)
}

fun TableCopyDispatcher(
    instruction: TableInstruction.TableCopyIsi,
) = dispatchInstruction { vstack, context ->
    TableCopyExecutor(vstack, context, instruction)
}

fun TableCopyDispatcher(
    instruction: TableInstruction.TableCopyIss,
) = dispatchInstruction { vstack, context ->
    TableCopyExecutor(vstack, context, instruction)
}

fun TableCopyDispatcher(
    instruction: TableInstruction.TableCopySii,
) = dispatchInstruction { vstack, context ->
    TableCopyExecutor(vstack, context, instruction)
}

fun TableCopyDispatcher(
    instruction: TableInstruction.TableCopySis,
) = dispatchInstruction { vstack, context ->
    TableCopyExecutor(vstack, context, instruction)
}

fun TableCopyDispatcher(
    instruction: TableInstruction.TableCopySsi,
) = dispatchInstruction { vstack, context ->
    TableCopyExecutor(vstack, context, instruction)
}

fun TableCopyDispatcher(
    instruction: TableInstruction.TableCopySss,
) = dispatchInstruction { vstack, context ->
    TableCopyExecutor(vstack, context, instruction)
}

fun TableFillDispatcher(
    instruction: TableInstruction.TableFillIsi,
) = dispatchInstruction { vstack, context ->
    TableFillExecutor(vstack, context, instruction)
}

fun TableFillDispatcher(
    instruction: TableInstruction.TableFillIss,
) = dispatchInstruction { vstack, context ->
    TableFillExecutor(vstack, context, instruction)
}

fun TableFillDispatcher(
    instruction: TableInstruction.TableFillSsi,
) = dispatchInstruction { vstack, context ->
    TableFillExecutor(vstack, context, instruction)
}

fun TableFillDispatcher(
    instruction: TableInstruction.TableFillSss,
) = dispatchInstruction { vstack, context ->
    TableFillExecutor(vstack, context, instruction)
}

fun TableGrowDispatcher(
    instruction: TableInstruction.TableGrowIs,
) = dispatchInstruction { vstack, context ->
    TableGrowExecutor(vstack, context, instruction)
}

fun TableGrowDispatcher(
    instruction: TableInstruction.TableGrowSs,
) = dispatchInstruction { vstack, context ->
    TableGrowExecutor(vstack, context, instruction)
}

fun TableInitDispatcher(
    instruction: TableInstruction.TableInitIii,
) = dispatchInstruction { vstack, context ->
    TableInitExecutor(vstack, context, instruction)
}

fun TableInitDispatcher(
    instruction: TableInstruction.TableInitIis,
) = dispatchInstruction { vstack, context ->
    TableInitExecutor(vstack, context, instruction)
}

fun TableInitDispatcher(
    instruction: TableInstruction.TableInitIsi,
) = dispatchInstruction { vstack, context ->
    TableInitExecutor(vstack, context, instruction)
}

fun TableInitDispatcher(
    instruction: TableInstruction.TableInitIss,
) = dispatchInstruction { vstack, context ->
    TableInitExecutor(vstack, context, instruction)
}

fun TableInitDispatcher(
    instruction: TableInstruction.TableInitSii,
) = dispatchInstruction { vstack, context ->
    TableInitExecutor(vstack, context, instruction)
}

fun TableInitDispatcher(
    instruction: TableInstruction.TableInitSis,
) = dispatchInstruction { vstack, context ->
    TableInitExecutor(vstack, context, instruction)
}

fun TableInitDispatcher(
    instruction: TableInstruction.TableInitSsi,
) = dispatchInstruction { vstack, context ->
    TableInitExecutor(vstack, context, instruction)
}

fun TableInitDispatcher(
    instruction: TableInstruction.TableInitSss,
) = dispatchInstruction { vstack, context ->
    TableInitExecutor(vstack, context, instruction)
}

fun TableGetDispatcher(
    instruction: TableInstruction.TableGetI,
) = dispatchInstruction { vstack, context ->
    TableGetExecutor(vstack, context, instruction)
}

fun TableGetDispatcher(
    instruction: TableInstruction.TableGetS,
) = dispatchInstruction { vstack, context ->
    TableGetExecutor(vstack, context, instruction)
}

fun TableSetDispatcher(
    instruction: TableInstruction.TableSetSi,
) = dispatchInstruction { vstack, context ->
    TableSetExecutor(vstack, context, instruction)
}

fun TableSetDispatcher(
    instruction: TableInstruction.TableSetSs,
) = dispatchInstruction { vstack, context ->
    TableSetExecutor(vstack, context, instruction)
}

fun TableSizeDispatcher(
    instruction: TableInstruction.TableSizeS,
) = dispatchInstruction { vstack, context ->
    TableSizeExecutor(vstack, context, instruction)
}
