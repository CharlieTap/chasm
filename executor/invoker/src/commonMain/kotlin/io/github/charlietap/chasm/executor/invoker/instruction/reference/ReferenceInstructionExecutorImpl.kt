package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

internal fun ReferenceInstructionExecutorImpl(
    instruction: ReferenceInstruction,
    stack: Stack,
): Result<Unit, InvocationError> =
    ReferenceInstructionExecutorImpl(
        instruction = instruction,
        stack = stack,
        refNullExecutor = ::RefNullExecutorImpl,
        refIsNullExecutor = ::RefIsNullExecutorImpl,
        refFuncExecutor = ::RefFuncExecutorImpl,
    )

internal fun ReferenceInstructionExecutorImpl(
    instruction: ReferenceInstruction,
    stack: Stack,
    refNullExecutor: RefNullExecutor,
    refIsNullExecutor: RefIsNullExecutor,
    refFuncExecutor: RefFuncExecutor,
): Result<Unit, InvocationError> = binding {
    when (instruction) {
        is ReferenceInstruction.RefNull -> refNullExecutor(stack, instruction).bind()
        is ReferenceInstruction.RefIsNull -> refIsNullExecutor(stack).bind()
        is ReferenceInstruction.RefFunc -> refFuncExecutor(stack, instruction).bind()

        else -> Err(InvocationError.UnimplementedInstruction(instruction)).bind<Unit>()
    }
}
