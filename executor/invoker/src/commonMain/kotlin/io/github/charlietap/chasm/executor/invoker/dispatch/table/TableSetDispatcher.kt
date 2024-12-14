package io.github.charlietap.chasm.executor.invoker.dispatch.table

import io.github.charlietap.chasm.executor.invoker.instruction.table.TableSetExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction

fun TableSetDispatcher(
    instruction: TableInstruction.TableSet,
) = TableSetDispatcher(
    instruction = instruction,
    executor = ::TableSetExecutor,
)

internal inline fun TableSetDispatcher(
    instruction: TableInstruction.TableSet,
    crossinline executor: Executor<TableInstruction.TableSet>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}