package io.github.charlietap.chasm.executor.invoker.dispatch.table

import io.github.charlietap.chasm.executor.invoker.instruction.table.TableInitExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.TableInstruction

fun TableInitDispatcher(
    instruction: TableInstruction.TableInit,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    TableInitExecutor(vstack, context, instruction)
    nextIp
}
