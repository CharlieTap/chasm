package io.github.charlietap.chasm.executor.invoker.instruction

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.HandlerDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.LabelDispatcher
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.ExceptionHandler

internal typealias InstructionBlockExecutor = (Stack, Stack.Entry.Label, Array<DispatchableInstruction>, ExceptionHandler?) -> Result<Unit, InvocationError>

internal inline fun InstructionBlockExecutor(
    stack: Stack,
    label: Stack.Entry.Label,
    instructions: Array<DispatchableInstruction>,
    handler: ExceptionHandler?,
): Result<Unit, InvocationError> =
    InstructionBlockExecutor(
        stack = stack,
        label = label,
        instructions = instructions,
        handler = handler,
        handlerDispatcher = ::HandlerDispatcher,
        labelDispatcher = ::LabelDispatcher,
    )

internal inline fun InstructionBlockExecutor(
    stack: Stack,
    label: Stack.Entry.Label,
    instructions: Array<DispatchableInstruction>,
    handler: ExceptionHandler?,
    crossinline handlerDispatcher: Dispatcher<ExceptionHandler>,
    crossinline labelDispatcher: Dispatcher<Stack.Entry.Label>,
): Result<Unit, InvocationError> = binding {

    handler?.let {
        stack.push(handler)
        val instruction = handlerDispatcher(handler)
        stack.push(instruction)
    }
    stack.push(label)
    stack.push(labelDispatcher(label))

    stack.push(instructions)
}
