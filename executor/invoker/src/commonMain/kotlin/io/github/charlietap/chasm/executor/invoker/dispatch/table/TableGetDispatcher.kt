package io.github.charlietap.chasm.executor.invoker.dispatch.table

import io.github.charlietap.chasm.executor.invoker.instruction.table.TableGetExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.TableInstruction

fun TableGetDispatcher(
    instruction: TableInstruction.TableGet,
) = TableGetDispatcher(
    instruction = instruction,
    executor = ::TableGetExecutor,
)

internal inline fun TableGetDispatcher(
    instruction: TableInstruction.TableGet,
    crossinline executor: Executor<TableInstruction.TableGet>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
