package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.dispatch.control.LoopDispatcher
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.StackDepths
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun LoopExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.Loop,
) = LoopExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    instructionBlockExecutor = ::InstructionBlockExecutor,
)

internal inline fun LoopExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.Loop,
    crossinline instructionBlockExecutor: InstructionBlockExecutor,
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

    instructionBlockExecutor(cstack, label, instruction.instructions, null)
}
