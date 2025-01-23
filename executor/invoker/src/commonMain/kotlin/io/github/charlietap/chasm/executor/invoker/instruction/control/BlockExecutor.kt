package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.stack.ControlStack
import io.github.charlietap.chasm.executor.runtime.stack.LabelStackDepths
import io.github.charlietap.chasm.executor.runtime.stack.ValueStack
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias BlockExecutor = (Store, ControlStack, ValueStack, FunctionType, Array<DispatchableInstruction>) -> Unit

internal inline fun BlockExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Block,
) =
    BlockExecutor(
        store = context.store,
        controlStack = context.cstack,
        valueStack = context.vstack,
        functionType = instruction.functionType,
        instructions = instruction.instructions,
    )

internal inline fun BlockExecutor(
    store: Store,
    controlStack: ControlStack,
    valueStack: ValueStack,
    functionType: FunctionType,
    instructions: Array<DispatchableInstruction>,
) = BlockExecutor(
    store = store,
    controlStack = controlStack,
    valueStack = valueStack,
    functionType = functionType,
    instructions = instructions,
    instructionBlockExecutor = ::InstructionBlockExecutor,
)

@Suppress("UNUSED_PARAMETER")
internal inline fun BlockExecutor(
    store: Store,
    controlStack: ControlStack,
    valueStack: ValueStack,
    functionType: FunctionType,
    instructions: Array<DispatchableInstruction>,
    crossinline instructionBlockExecutor: InstructionBlockExecutor,
) {
    val params = functionType.params.types.size
    val results = functionType.results.types.size

    val label = ControlStack.Entry.Label(
        arity = results,
        depths = LabelStackDepths(
            instructions = controlStack.instructionsDepth(),
            labels = controlStack.labelsDepth(),
            values = valueStack.depth() - params,
        ),
        continuation = null,
    )

    instructionBlockExecutor(controlStack, label, instructions, null)
}
