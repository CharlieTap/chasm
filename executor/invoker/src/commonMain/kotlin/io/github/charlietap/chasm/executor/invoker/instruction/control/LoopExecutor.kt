package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.dispatch.control.LoopDispatcher
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.StackDepths
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun LoopExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.Loop,
) {
    val label = ControlStack.Entry.Label(
        arity = instruction.params,
        depths = StackDepths(
            handlers = cstack.handlersDepth(),
            instructions = cstack.instructionsDepth(),
            labels = cstack.labelsDepth(),
            values = vstack.depth() - instruction.params,
        ),
        continuation = LoopDispatcher(instruction),
    )

    cstack.push(label)
    cstack.push(instruction.instructions)
}
