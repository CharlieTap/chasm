package io.github.charlietap.chasm.executor.invoker.dispatch.tablefused

import io.github.charlietap.chasm.executor.invoker.instruction.tablefused.TableSetExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedTableInstruction

fun TableSetDispatcher(
    instruction: FusedTableInstruction.TableSet,
) = TableSetDispatcher(
    instruction = instruction,
    executor = ::TableSetExecutor,
)

internal inline fun TableSetDispatcher(
    instruction: FusedTableInstruction.TableSet,
    crossinline executor: Executor<FusedTableInstruction.TableSet>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
