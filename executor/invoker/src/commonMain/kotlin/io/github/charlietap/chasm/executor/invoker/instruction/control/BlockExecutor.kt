package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.StackDepths
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal typealias BlockExecutor = (ControlStack, Array<DispatchableInstruction>) -> Unit

internal inline fun BlockExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.Block,
) = BlockExecutor(
    controlStack = cstack,
    instructions = instruction.instructions,
)

internal inline fun BlockExecutor(
    controlStack: ControlStack,
    instructions: Array<DispatchableInstruction>,
) {
    val label = ControlStack.Entry.Label(
        depths = StackDepths(
            handlers = controlStack.handlersDepth(),
            instructions = controlStack.instructionsDepth(),
            labels = controlStack.labelsDepth(),
            values = 0,
        ),
        continuation = null,
    )

    controlStack.push(label)
    controlStack.push(instructions)
}
