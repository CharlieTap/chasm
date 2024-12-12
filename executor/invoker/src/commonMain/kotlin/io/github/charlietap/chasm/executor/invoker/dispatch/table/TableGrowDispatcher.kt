package io.github.charlietap.chasm.executor.invoker.dispatch.table

import io.github.charlietap.chasm.executor.invoker.instruction.table.TableGrowExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction

fun TableGrowDispatcher(
    instruction: TableInstruction.TableGrow,
) = TableGrowDispatcher(
    instruction = instruction,
    executor = ::TableGrowExecutor,
)

internal inline fun TableGrowDispatcher(
    instruction: TableInstruction.TableGrow,
    crossinline executor: Executor<TableInstruction.TableGrow>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
