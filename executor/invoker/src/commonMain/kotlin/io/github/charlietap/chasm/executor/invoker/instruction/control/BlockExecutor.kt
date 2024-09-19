@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.instruction.ModuleInstruction
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias BlockExecutor = (Store, Stack, ControlInstruction.BlockType, List<Instruction>) -> Result<Unit, InvocationError>

internal inline fun BlockExecutor(
    store: Store,
    stack: Stack,
    blockType: ControlInstruction.BlockType,
    instructions: List<Instruction>,
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
    blockType: ControlInstruction.BlockType,
    instructions: List<Instruction>,
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

    instructionBlockExecutor(stack, label, instructions.map(::ModuleInstruction), params, null).bind()
}
