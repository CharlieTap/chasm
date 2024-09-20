package io.github.charlietap.chasm.executor.invoker.instruction

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.Executor
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.instruction.admin.ExceptionHandlerInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.admin.FrameInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.admin.LabelInstructionExecutor
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instruction.AdminInstruction

internal fun AdminInstructionExecutor(
    context: ExecutionContext,
    instruction: AdminInstruction,
): Result<Unit, InvocationError> =
    AdminInstructionExecutor(
        context = context,
        instruction = instruction,
        frameInstructionExecutor = ::FrameInstructionExecutor,
        handlerInstructionExecutor = ::ExceptionHandlerInstructionExecutor,
        labelInstructionExecutor = ::LabelInstructionExecutor,
    )

@Suppress("UNUSED_PARAMETER")
internal fun AdminInstructionExecutor(
    context: ExecutionContext,
    instruction: AdminInstruction,
    frameInstructionExecutor: Executor<AdminInstruction.Frame>,
    handlerInstructionExecutor: Executor<AdminInstruction.Handler>,
    labelInstructionExecutor: Executor<AdminInstruction.Label>,
): Result<Unit, InvocationError> = binding {
    when (instruction) {
        is AdminInstruction.Frame -> frameInstructionExecutor(context, instruction).bind()
        is AdminInstruction.Label -> labelInstructionExecutor(context, instruction).bind()
        is AdminInstruction.Handler -> handlerInstructionExecutor(context, instruction).bind()
    }
}
