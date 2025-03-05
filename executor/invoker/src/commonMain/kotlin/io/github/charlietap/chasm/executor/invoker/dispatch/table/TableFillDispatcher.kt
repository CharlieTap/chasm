package io.github.charlietap.chasm.executor.invoker.dispatch.table

import io.github.charlietap.chasm.executor.invoker.instruction.table.TableFillExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.TableInstruction

fun TableFillDispatcher(
    instruction: TableInstruction.TableFill,
) = TableFillDispatcher(
    instruction = instruction,
    executor = ::TableFillExecutor,
)

internal inline fun TableFillDispatcher(
    instruction: TableInstruction.TableFill,
    crossinline executor: Executor<TableInstruction.TableFill>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
