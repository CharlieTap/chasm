package io.github.charlietap.chasm.executor.invoker.function

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.runCatching
import io.github.charlietap.chasm.executor.invoker.flow.ReturnException
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionExecutorImpl
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store

internal fun FunctionExecutorImpl(
    store: Store,
    stack: Stack,
): Result<Unit, InvocationError> =
    FunctionExecutorImpl(
        store = store,
        stack = stack,
        instructionExecutor = ::InstructionExecutorImpl,
    )

internal fun FunctionExecutorImpl(
    store: Store,
    stack: Stack,
    instructionExecutor: InstructionExecutor,
): Result<Unit, InvocationError> = binding {

    val instructions = stack.popLabel()?.continuation ?: emptyList()

    if (instructions.isEmpty()) Err(InvocationError.MissingStackLabel).bind<Unit>()

    val result = runCatching {
        instructions.forEach { instruction ->
            instructionExecutor(instruction, store, stack).bind()
        }
    }

    result.fold({
        stack.popFrame() ?: Err(InvocationError.MissingStackFrame).bind<Unit>()
    }) { err ->
        if (err !is ReturnException) {
            Err(InvocationError.InstructionFailure).bind<Unit>()
        }
    }
}
