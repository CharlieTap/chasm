package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.instruction.admin.LabelInstructionExecutor
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.AdminInstruction

internal fun LabelDispatcher(
    label: Stack.Entry.Label,
) = LabelDispatcher(
    label = label,
    executor = ::LabelInstructionExecutor,
)

internal inline fun LabelDispatcher(
    label: Stack.Entry.Label,
    crossinline executor: Executor<AdminInstruction.Label>,
): DispatchableInstruction = { context ->
    executor(context, AdminInstruction.Label(label))
}
