package io.github.charlietap.chasm.executor.invoker.dispatch.table

import io.github.charlietap.chasm.executor.invoker.instruction.table.TableInitExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.TableInstruction

fun TableInitDispatcher(
    instruction: TableInstruction.TableInit,
) = TableInitDispatcher(
    instruction = instruction,
    executor = ::TableInitExecutor,
)

internal inline fun TableInitDispatcher(
    instruction: TableInstruction.TableInit,
    crossinline executor: Executor<TableInstruction.TableInit>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
