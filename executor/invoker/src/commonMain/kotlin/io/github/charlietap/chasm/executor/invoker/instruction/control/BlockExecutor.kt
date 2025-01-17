package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.stack.LabelStackDepths
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias BlockExecutor = (Store, Stack, FunctionType, Array<DispatchableInstruction>) -> Unit

internal inline fun BlockExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Block,
) =
    BlockExecutor(
        store = context.store,
        stack = context.stack,
        functionType = instruction.functionType,
        instructions = instruction.instructions,
    )

internal inline fun BlockExecutor(
    store: Store,
    stack: Stack,
    functionType: FunctionType,
    instructions: Array<DispatchableInstruction>,
) = BlockExecutor(
    store = store,
    stack = stack,
    functionType = functionType,
    instructions = instructions,
    instructionBlockExecutor = ::InstructionBlockExecutor,
)

@Suppress("UNUSED_PARAMETER")
internal inline fun BlockExecutor(
    store: Store,
    stack: Stack,
    functionType: FunctionType,
    instructions: Array<DispatchableInstruction>,
    crossinline instructionBlockExecutor: InstructionBlockExecutor,
) {

    val params = functionType.params.types.size
    val results = functionType.results.types.size

    val label = Stack.Entry.Label(
        arity = results,
        depths = LabelStackDepths(
            instructions = stack.instructionsDepth(),
            labels = stack.labelsDepth(),
            values = stack.valuesDepth() - params,
        ),
        continuation = null,
    )

    instructionBlockExecutor(stack, label, instructions, null)
}
