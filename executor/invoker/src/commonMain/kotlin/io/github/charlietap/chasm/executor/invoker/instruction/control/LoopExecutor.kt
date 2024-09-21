@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.instruction.ModuleInstruction

internal inline fun LoopExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Loop,
): Result<Unit, InvocationError> =
    LoopExecutor(
        context = context,
        instruction = instruction,
        expander = ::BlockTypeExpander,
        instructionBlockExecutor = ::InstructionBlockExecutor,
    )

internal inline fun LoopExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Loop,
    crossinline expander: BlockTypeExpander,
    crossinline instructionBlockExecutor: InstructionBlockExecutor,
): Result<Unit, InvocationError> = binding {

    val (stack) = context
    val (blockType, instructions) = instruction
    val frame = stack.peekFrame().bind()
    val functionType = expander(frame.state.module, blockType).bind()
    val paramArity = functionType?.let {
        Arity.Argument(functionType.params.types.size)
    } ?: Arity.Argument.NULLARY

    val executionInstructions = instructions.map(::ModuleInstruction)

    val params = List(paramArity.value) {
        stack.popValue().bind().value
    }

    val label = Stack.Entry.Label(
        arity = paramArity,
        stackValuesDepth = stack.valuesDepth(),
        continuation = listOf(ModuleInstruction(ControlInstruction.Loop(blockType, instructions))),
    )

    instructionBlockExecutor(stack, label, executionInstructions, params, null).bind()
}
