package io.github.charlietap.chasm.executor.invoker.dispatch.tablefused

import io.github.charlietap.chasm.executor.invoker.instruction.tablefused.TableSizeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedTableInstruction

fun TableSizeDispatcher(
    instruction: FusedTableInstruction.TableSize,
) = TableSizeDispatcher(
    instruction = instruction,
    executor = ::TableSizeExecutor,
)

internal inline fun TableSizeDispatcher(
    instruction: FusedTableInstruction.TableSize,
    crossinline executor: Executor<FusedTableInstruction.TableSize>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
