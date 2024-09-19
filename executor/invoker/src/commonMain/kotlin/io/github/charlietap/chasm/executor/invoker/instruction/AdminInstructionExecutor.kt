package io.github.charlietap.chasm.executor.invoker.instruction

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.instruction.admin.ExceptionHandlerInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.admin.FrameInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.admin.LabelInstructionExecutor
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias AdminInstructionExecutor = (AdminInstruction, Store, Stack) -> Result<Unit, InvocationError>

internal fun AdminInstructionExecutor(
    instruction: AdminInstruction,
    store: Store,
    stack: Stack,
): Result<Unit, InvocationError> =
    AdminInstructionExecutor(
        instruction = instruction,
        store = store,
        stack = stack,
        frameInstructionExecutor = ::FrameInstructionExecutor,
        handlerInstructionExecutor = ::ExceptionHandlerInstructionExecutor,
        labelInstructionExecutor = ::LabelInstructionExecutor,
    )

@Suppress("UNUSED_PARAMETER")
internal fun AdminInstructionExecutor(
    instruction: AdminInstruction,
    store: Store,
    stack: Stack,
    frameInstructionExecutor: FrameInstructionExecutor,
    handlerInstructionExecutor: ExceptionHandlerInstructionExecutor,
    labelInstructionExecutor: LabelInstructionExecutor,
): Result<Unit, InvocationError> = binding {
    when (instruction) {
        is AdminInstruction.Frame -> frameInstructionExecutor(instruction.frame, stack).bind()
        is AdminInstruction.Label -> labelInstructionExecutor(instruction.label, stack).bind()
        is AdminInstruction.Handler -> handlerInstructionExecutor(instruction.handler, stack).bind()
    }
}
