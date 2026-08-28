package io.github.charlietap.chasm.executor.invoker.dispatch.table

import io.github.charlietap.chasm.executor.invoker.instruction.table.TableGetExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.TableInstruction

fun TableGetDispatcher(
    instruction: TableInstruction.TableGet,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    TableGetExecutor(vstack, context, instruction)
    nextIp
}
