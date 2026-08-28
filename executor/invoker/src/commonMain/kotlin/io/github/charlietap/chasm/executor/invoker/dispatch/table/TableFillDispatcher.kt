package io.github.charlietap.chasm.executor.invoker.dispatch.table

import io.github.charlietap.chasm.executor.invoker.instruction.table.TableFillExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.TableInstruction

fun TableFillDispatcher(
    instruction: TableInstruction.TableFill,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    TableFillExecutor(vstack, context, instruction)
    nextIp
}
