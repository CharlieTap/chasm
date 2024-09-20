package io.github.charlietap.chasm.executor.invoker.instruction.variable

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

internal fun VariableInstructionExecutor(
    context: ExecutionContext,
    instruction: VariableInstruction,
): Result<Unit, InvocationError> =
    VariableInstructionExecutor(
        context = context,
        instruction = instruction,
        localGetExecutor = ::LocalGetExecutor,
        localSetExecutor = ::LocalSetExecutor,
        localTeeExecutor = ::LocalTeeExecutor,
        globalGetExecutor = ::GlobalGetExecutor,
        globalSetExecutor = ::GlobalSetExecutor,
    )

internal fun VariableInstructionExecutor(
    context: ExecutionContext,
    instruction: VariableInstruction,
    localGetExecutor: LocalGetExecutor,
    localSetExecutor: LocalSetExecutor,
    localTeeExecutor: LocalTeeExecutor,
    globalGetExecutor: GlobalGetExecutor,
    globalSetExecutor: GlobalSetExecutor,
): Result<Unit, InvocationError> = binding {
    val (stack, store) = context
    when (instruction) {
        is VariableInstruction.LocalGet -> localGetExecutor(stack, instruction).bind()
        is VariableInstruction.LocalSet -> localSetExecutor(stack, instruction).bind()
        is VariableInstruction.LocalTee -> localTeeExecutor(stack, instruction).bind()
        is VariableInstruction.GlobalGet -> globalGetExecutor(store, stack, instruction).bind()
        is VariableInstruction.GlobalSet -> globalSetExecutor(store, stack, instruction).bind()
    }
}
