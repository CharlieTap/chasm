package io.github.charlietap.chasm.executor.invoker.dispatch.tablefused

import io.github.charlietap.chasm.executor.invoker.instruction.tablefused.TableInitExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedTableInstruction

fun TableInitDispatcher(
    instruction: FusedTableInstruction.TableInit,
) = TableInitDispatcher(
    instruction = instruction,
    executor = ::TableInitExecutor,
)

internal inline fun TableInitDispatcher(
    instruction: FusedTableInstruction.TableInit,
    crossinline executor: Executor<FusedTableInstruction.TableInit>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
