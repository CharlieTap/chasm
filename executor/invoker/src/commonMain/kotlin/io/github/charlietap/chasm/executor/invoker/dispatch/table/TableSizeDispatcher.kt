package io.github.charlietap.chasm.executor.invoker.dispatch.table

import io.github.charlietap.chasm.executor.invoker.instruction.table.TableSizeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.TableInstruction

fun TableSizeDispatcher(
    instruction: TableInstruction.TableSize,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    TableSizeExecutor(vstack, context, instruction)
    nextIp
}
