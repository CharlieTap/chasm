package io.github.charlietap.chasm.executor.invoker.dispatch.table

import io.github.charlietap.chasm.executor.invoker.instruction.table.TableGrowExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.TableInstruction

fun TableGrowDispatcher(
    instruction: TableInstruction.TableGrow,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    TableGrowExecutor(vstack, context, instruction)
    nextIp
}
