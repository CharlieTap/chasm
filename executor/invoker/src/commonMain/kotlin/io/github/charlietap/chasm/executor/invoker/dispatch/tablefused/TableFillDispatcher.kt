package io.github.charlietap.chasm.executor.invoker.dispatch.tablefused

import io.github.charlietap.chasm.executor.invoker.instruction.tablefused.TableFillExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedTableInstruction

fun TableFillDispatcher(
    instruction: FusedTableInstruction.TableFill,
) = TableFillDispatcher(
    instruction = instruction,
    executor = ::TableFillExecutor,
)

internal inline fun TableFillDispatcher(
    instruction: FusedTableInstruction.TableFill,
    crossinline executor: Executor<FusedTableInstruction.TableFill>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
