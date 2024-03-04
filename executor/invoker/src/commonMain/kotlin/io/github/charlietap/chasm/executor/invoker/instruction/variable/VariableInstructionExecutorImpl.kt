package io.github.charlietap.chasm.executor.invoker.instruction.variable

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store

internal fun VariableInstructionExecutorImpl(
    instruction: VariableInstruction,
    store: Store,
    stack: Stack,
): Result<Unit, InvocationError> = binding {
    when (instruction) {
        is VariableInstruction.LocalGet -> {
            stack.peekFrame()?.state?.locals?.getOrNull(instruction.localIdx.idx.toInt())?.let { value ->
                stack.push(Stack.Entry.Value(value))
            } ?: Err(InvocationError.MissingLocal).bind<Unit>()
        }
        is VariableInstruction.LocalSet -> {
            stack.popValue()?.let { value ->
                stack.peekFrame()?.state?.locals?.set(instruction.localIdx.idx.toInt(), value.value)
            } ?: Err(InvocationError.MissingStackValue).bind<Unit>()
        }
        is VariableInstruction.LocalTee -> {
            stack.peekValue()?.let { value ->
                stack.peekFrame()?.state?.locals?.set(instruction.localIdx.idx.toInt(), value.value)
            } ?: Err(InvocationError.MissingStackValue).bind<Unit>()
        }
        is VariableInstruction.GlobalGet -> {
            // todo make all address lookups results and also store lookups
            stack.peekFrame()?.state?.module?.let { instance ->
                instance.globalAddresses.getOrNull(instruction.globalIdx.idx.toInt())?.let { address ->
                    val global = store.global(address).bind()
                    stack.push(Stack.Entry.Value(global.value))
                }
            }
        }
        is VariableInstruction.GlobalSet -> {
            TODO()
        }

        else -> Err(InvocationError.UnimplementedInstruction).bind<Unit>()
    }
}
