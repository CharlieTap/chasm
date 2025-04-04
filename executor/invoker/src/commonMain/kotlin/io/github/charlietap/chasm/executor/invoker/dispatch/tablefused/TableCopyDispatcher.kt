package io.github.charlietap.chasm.executor.invoker.dispatch.tablefused

import io.github.charlietap.chasm.executor.invoker.instruction.tablefused.TableCopyExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedTableInstruction

fun TableCopyDispatcher(
    instruction: FusedTableInstruction.TableCopy,
) = TableCopyDispatcher(
    instruction = instruction,
    executor = ::TableCopyExecutor,
)

internal inline fun TableCopyDispatcher(
    instruction: FusedTableInstruction.TableCopy,
    crossinline executor: Executor<FusedTableInstruction.TableCopy>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
