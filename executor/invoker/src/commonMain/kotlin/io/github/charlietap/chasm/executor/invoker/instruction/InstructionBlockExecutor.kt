package io.github.charlietap.chasm.executor.invoker.instruction

import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.HandlerDispatcher
import io.github.charlietap.chasm.executor.invoker.instruction.admin.LabelInstructionExecutor
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.exception.ExceptionHandler

internal typealias InstructionBlockExecutor = (Stack, Stack.Entry.Label, Array<DispatchableInstruction>, ExceptionHandler?) -> Unit

internal inline fun InstructionBlockExecutor(
    stack: Stack,
    label: Stack.Entry.Label,
    instructions: Array<DispatchableInstruction>,
    handler: ExceptionHandler?,
) = InstructionBlockExecutor(
        stack = stack,
        label = label,
        instructions = instructions,
        handler = handler,
        handlerDispatcher = ::HandlerDispatcher,
        labelCleaner = ::LabelInstructionExecutor,
    )

internal inline fun InstructionBlockExecutor(
    stack: Stack,
    label: Stack.Entry.Label,
    instructions: Array<DispatchableInstruction>,
    handler: ExceptionHandler?,
    crossinline handlerDispatcher: Dispatcher<ExceptionHandler>,
    noinline labelCleaner: DispatchableInstruction,
) {
    handler?.let {
        stack.push(handler)
        val instruction = handlerDispatcher(handler)
        stack.push(instruction)
    }
    stack.push(label)
    stack.push(labelCleaner)

    stack.push(instructions)
}
