package io.github.charlietap.chasm.executor.invoker.dispatch.table

import io.github.charlietap.chasm.executor.invoker.instruction.table.TableSetExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.TableInstruction

fun TableSetDispatcher(
    instruction: TableInstruction.TableSet,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    TableSetExecutor(vstack, context, instruction)
    nextIp
}
