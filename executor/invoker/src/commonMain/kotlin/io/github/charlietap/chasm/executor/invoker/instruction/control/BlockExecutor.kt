package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction.BlockType
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias BlockExecutor = (Store, Stack, BlockType, List<DispatchableInstruction>) -> Result<Unit, InvocationError>

internal inline fun BlockExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Block,
): Result<Unit, InvocationError> =
    BlockExecutor(
        store = context.store,
        stack = context.stack,
        blockType = instruction.blockType,
        instructions = instruction.instructions,
    )

internal inline fun BlockExecutor(
    store: Store,
    stack: Stack,
    blockType: BlockType,
    instructions: List<DispatchableInstruction>,
): Result<Unit, InvocationError> =
    BlockExecutor(
        store = store,
        stack = stack,
        blockType = blockType,
        instructions = instructions,
        expander = ::BlockTypeExpander,
        instructionBlockExecutor = ::InstructionBlockExecutor,
    )

@Suppress("UNUSED_PARAMETER")
internal inline fun BlockExecutor(
    store: Store,
    stack: Stack,
    blockType: BlockType,
    instructions: List<DispatchableInstruction>,
    crossinline expander: BlockTypeExpander,
    crossinline instructionBlockExecutor: InstructionBlockExecutor,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrame().bind()
    val functionType = expander(frame.state.module, blockType).bind()
    val (paramArity, resultArity) = functionType?.let {
        Arity.Argument(functionType.params.types.size) to Arity.Return(functionType.results.types.size)
    } ?: (Arity.Argument.NULLARY to Arity.Return.SIDE_EFFECT)

    val params = List(paramArity.value) {
        stack.popValue().bind().value
    }

    val label = Stack.Entry.Label(
        arity = resultArity,
        stackValuesDepth = stack.valuesDepth(),
        continuation = emptyList(),
    )

    instructionBlockExecutor(stack, label, instructions, params, null).bind()
}
