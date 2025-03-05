package io.github.charlietap.chasm.executor.invoker.instruction

import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.HandlerDispatcher
import io.github.charlietap.chasm.executor.invoker.instruction.admin.LabelInstructionExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.runtime.stack.ControlStack

internal typealias InstructionBlockExecutor = (ControlStack, ControlStack.Entry.Label, Array<DispatchableInstruction>, ExceptionHandler?) -> Unit

internal inline fun InstructionBlockExecutor(
    controlStack: ControlStack,
    label: ControlStack.Entry.Label,
    instructions: Array<DispatchableInstruction>,
    handler: ExceptionHandler?,
) = InstructionBlockExecutor(
    controlStack = controlStack,
    label = label,
    instructions = instructions,
    handler = handler,
    handlerDispatcher = ::HandlerDispatcher,
    labelCleaner = ::LabelInstructionExecutor,
)

internal inline fun InstructionBlockExecutor(
    controlStack: ControlStack,
    label: ControlStack.Entry.Label,
    instructions: Array<DispatchableInstruction>,
    handler: ExceptionHandler?,
    crossinline handlerDispatcher: Dispatcher<ExceptionHandler>,
    noinline labelCleaner: DispatchableInstruction,
) {
    handler?.let {
        controlStack.push(handler)
        val instruction = handlerDispatcher(handler)
        controlStack.push(instruction)
    }
    controlStack.push(label)
    controlStack.push(labelCleaner)

    controlStack.push(instructions)
}
