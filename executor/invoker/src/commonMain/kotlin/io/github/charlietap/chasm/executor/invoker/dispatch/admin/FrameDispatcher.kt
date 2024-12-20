package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.instruction.admin.FrameInstructionExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.executor.runtime.stack.ActivationFrame

internal fun FrameDispatcher(
    frame: ActivationFrame,
) = FrameDispatcher(
    frame = frame,
    executor = ::FrameInstructionExecutor,
)

internal inline fun FrameDispatcher(
    frame: ActivationFrame,
    crossinline executor: Executor<AdminInstruction.Frame>,
): DispatchableInstruction = { context ->
    executor(context, AdminInstruction.Frame(frame))
}
