package io.github.charlietap.chasm.executor.invoker.dispatch.tablefused

import io.github.charlietap.chasm.executor.invoker.instruction.tablefused.TableGrowExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedTableInstruction

fun TableGrowDispatcher(
    instruction: FusedTableInstruction.TableGrow,
) = TableGrowDispatcher(
    instruction = instruction,
    executor = ::TableGrowExecutor,
)

internal inline fun TableGrowDispatcher(
    instruction: FusedTableInstruction.TableGrow,
    crossinline executor: Executor<FusedTableInstruction.TableGrow>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
