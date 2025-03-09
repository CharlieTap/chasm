package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.StackDepths
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal typealias BlockExecutor = (Store, ControlStack, ValueStack, Int, Int, Array<DispatchableInstruction>) -> Unit

internal inline fun BlockExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.Block,
) = BlockExecutor(
    store = context.store,
    controlStack = cstack,
    valueStack = vstack,
    params = instruction.params,
    results = instruction.results,
    instructions = instruction.instructions,
)

internal inline fun BlockExecutor(
    store: Store,
    controlStack: ControlStack,
    valueStack: ValueStack,
    params: Int,
    results: Int,
    instructions: Array<DispatchableInstruction>,
) = BlockExecutor(
    store = store,
    controlStack = controlStack,
    valueStack = valueStack,
    params = params,
    results = results,
    instructions = instructions,
    instructionBlockExecutor = ::InstructionBlockExecutor,
)

@Suppress("UNUSED_PARAMETER")
internal inline fun BlockExecutor(
    store: Store,
    controlStack: ControlStack,
    valueStack: ValueStack,
    params: Int,
    results: Int,
    instructions: Array<DispatchableInstruction>,
    crossinline instructionBlockExecutor: InstructionBlockExecutor,
) {
    val label = ControlStack.Entry.Label(
        arity = results,
        depths = StackDepths(
            handlers = controlStack.handlersDepth(),
            instructions = controlStack.instructionsDepth(),
            labels = controlStack.labelsDepth(),
            values = valueStack.depth() - params,
        ),
        continuation = null,
    )

    instructionBlockExecutor(controlStack, label, instructions, null)
}
