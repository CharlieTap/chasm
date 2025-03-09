package io.github.charlietap.chasm.executor.invoker.dispatch.tablefused

import io.github.charlietap.chasm.executor.invoker.instruction.tablefused.TableGetExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedTableInstruction

fun TableGetDispatcher(
    instruction: FusedTableInstruction.TableGet,
) = TableGetDispatcher(
    instruction = instruction,
    executor = ::TableGetExecutor,
)

internal inline fun TableGetDispatcher(
    instruction: FusedTableInstruction.TableGet,
    crossinline executor: Executor<FusedTableInstruction.TableGet>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
