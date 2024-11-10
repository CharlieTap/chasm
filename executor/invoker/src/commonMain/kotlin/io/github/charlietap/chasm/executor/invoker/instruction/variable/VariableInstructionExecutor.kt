package io.github.charlietap.chasm.executor.invoker.instruction.variable

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.executor.invoker.Executor
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

internal inline fun VariableInstructionExecutor(
    context: ExecutionContext,
    instruction: VariableInstruction,
    crossinline localGetExecutor: Executor<VariableInstruction.LocalGet>,
    crossinline localSetExecutor: Executor<VariableInstruction.LocalSet>,
    crossinline localTeeExecutor: Executor<VariableInstruction.LocalTee>,
    crossinline globalGetExecutor: Executor<VariableInstruction.GlobalGet>,
    crossinline globalSetExecutor: Executor<VariableInstruction.GlobalSet>,
): Result<Unit, InvocationError> = binding {
    when (instruction) {
        is VariableInstruction.LocalGet -> localGetExecutor(context, instruction).bind()
        is VariableInstruction.LocalSet -> localSetExecutor(context, instruction).bind()
        is VariableInstruction.LocalTee -> localTeeExecutor(context, instruction).bind()
        is VariableInstruction.GlobalGet -> globalGetExecutor(context, instruction).bind()
        is VariableInstruction.GlobalSet -> globalSetExecutor(context, instruction).bind()
    }
}
