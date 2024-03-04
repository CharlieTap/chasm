package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.invoker.function.FunctionCall
import io.github.charlietap.chasm.executor.invoker.function.FunctionCallImpl
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store

internal fun ControlInstructionExecutorImpl(
    instruction: ControlInstruction,
    store: Store,
    stack: Stack,
): Result<Unit, InvocationError> =
    ControlInstructionExecutorImpl(
        instruction = instruction,
        store = store,
        stack = stack,
        call = ::FunctionCallImpl,
    )

internal fun ControlInstructionExecutorImpl(
    instruction: ControlInstruction,
    store: Store,
    stack: Stack,
    call: FunctionCall,
): Result<Unit, InvocationError> = binding {
    when (instruction) {
        is ControlInstruction.Call -> {
            stack.peekFrame()?.let { frame ->
                val address = frame.state.module.functionAddresses[instruction.functionIndex.idx.toInt()]
                call(store, stack, address).bind()
            }
        }

        else -> Err(InvocationError.UnimplementedInstruction).bind<Unit>()
    }
}
