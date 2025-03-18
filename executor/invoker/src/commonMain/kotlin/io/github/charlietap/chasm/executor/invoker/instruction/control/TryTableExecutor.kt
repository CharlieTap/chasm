package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.HandlerDispatcher
import io.github.charlietap.chasm.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.StackDepths
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun TryTableExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.TryTable,
) = TryTableExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    handlerDispatcher = ::HandlerDispatcher,
)

internal inline fun TryTableExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.TryTable,
    crossinline handlerDispatcher: Dispatcher<ExceptionHandler>,
) {
    val label = ControlStack.Entry.Label(
        arity = instruction.results,
        depths = StackDepths(
            handlers = cstack.handlersDepth(),
            instructions = cstack.instructionsDepth(),
            labels = cstack.labelsDepth(),
            values = vstack.depth() - instruction.params,
        ),
        continuation = null,
    )

    val handler = ExceptionHandler(
        instructions = instruction.handlers,
        framesDepth = cstack.framesDepth(),
        labelsDepth = cstack.labelsDepth(),
        instructionsDepth = cstack.instructionsDepth(),
        framePointer = vstack.framePointer,
    )

    cstack.push(handler)
    cstack.push(handlerDispatcher(handler))
    cstack.push(label)
    cstack.push(instruction.instructions)
}
