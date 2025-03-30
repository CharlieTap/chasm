package io.github.charlietap.chasm.executor.invoker.dispatch.table

import io.github.charlietap.chasm.executor.invoker.instruction.table.TableGrowExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.TableInstruction

fun TableGrowDispatcher(
    instruction: TableInstruction.TableGrow,
) = TableGrowDispatcher(
    instruction = instruction,
    executor = ::TableGrowExecutor,
)

internal inline fun TableGrowDispatcher(
    instruction: TableInstruction.TableGrow,
    crossinline executor: Executor<TableInstruction.TableGrow>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
