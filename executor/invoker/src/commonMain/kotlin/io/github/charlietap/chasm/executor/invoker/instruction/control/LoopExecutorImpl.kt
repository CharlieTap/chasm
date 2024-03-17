@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutorImpl
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.peekFrameOrError
import io.github.charlietap.chasm.executor.runtime.ext.popValueOrError
import io.github.charlietap.chasm.executor.runtime.store.Store

internal inline fun LoopExecutorImpl(
    store: Store,
    stack: Stack,
    blockType: ControlInstruction.BlockType,
    instructions: List<Instruction>,
): Result<Unit, InvocationError> =
    LoopExecutorImpl(
        store = store,
        stack = stack,
        blockType = blockType,
        instructions = instructions,
        expander = ::BlockTypeExpanderImpl,
        instructionBlockExecutor = ::InstructionBlockExecutorImpl,
    )

internal inline fun LoopExecutorImpl(
    store: Store,
    stack: Stack,
    blockType: ControlInstruction.BlockType,
    instructions: List<Instruction>,
    crossinline expander: BlockTypeExpander,
    crossinline instructionBlockExecutor: InstructionBlockExecutor,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrameOrError().bind()

    val functionType = expander(frame.state.module, blockType).bind()
    val paramArity = functionType?.let {
        Arity(functionType.params.types.size)
    } ?: Arity.SIDE_EFFECT

    val params = List(paramArity.value) {
        stack.popValueOrError().bind().value
    }

    val label = Stack.Entry.Label(
        arity = paramArity,
        continuation = listOf(ControlInstruction.Loop(blockType, instructions)),
    )

    instructionBlockExecutor(store, stack, label, instructions, params).bind()
}
