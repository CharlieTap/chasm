package io.github.charlietap.chasm.executor.invoker.dispatch.table

import io.github.charlietap.chasm.executor.invoker.instruction.table.TableCopyExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.TableInstruction

fun TableCopyDispatcher(
    instruction: TableInstruction.TableCopy,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    TableCopyExecutor(vstack, context, instruction)
    nextIp
}
