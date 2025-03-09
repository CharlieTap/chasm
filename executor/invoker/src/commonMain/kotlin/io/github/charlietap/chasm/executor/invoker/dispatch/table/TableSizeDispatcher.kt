package io.github.charlietap.chasm.executor.invoker.dispatch.table

import io.github.charlietap.chasm.executor.invoker.instruction.table.TableSizeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.TableInstruction

fun TableSizeDispatcher(
    instruction: TableInstruction.TableSize,
) = TableSizeDispatcher(
    instruction = instruction,
    executor = ::TableSizeExecutor,
)

internal inline fun TableSizeDispatcher(
    instruction: TableInstruction.TableSize,
    crossinline executor: Executor<TableInstruction.TableSize>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
