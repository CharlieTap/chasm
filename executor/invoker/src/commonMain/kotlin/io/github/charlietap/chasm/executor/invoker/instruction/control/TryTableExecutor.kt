@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.instruction.ModuleInstruction
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias TryTableExecutor = (Store, Stack, ControlInstruction.TryTable) -> Result<Unit, InvocationError>

internal inline fun TryTableExecutor(
    store: Store,
    stack: Stack,
    instruction: ControlInstruction.TryTable,
): Result<Unit, InvocationError> = TryTableExecutor(
    store = store,
    stack = stack,
    instruction = instruction,
    expander = ::BlockTypeExpander,
    blockExecutor = ::InstructionBlockExecutor,
)

internal inline fun TryTableExecutor(
    store: Store,
    stack: Stack,
    instruction: ControlInstruction.TryTable,
    crossinline expander: BlockTypeExpander,
    crossinline blockExecutor: InstructionBlockExecutor,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrame().bind()
    val functionType = expander(frame.state.module, instruction.blockType).bind()

    val paramArity = functionType?.let {
        Arity.Argument(functionType.params.types.size)
    } ?: Arity.Argument.NULLARY

    val returnArity = functionType?.let {
        Arity.Return(functionType.results.types.size)
    } ?: Arity.Return.SIDE_EFFECT

    val executionInstructions = instruction.instructions.map(::ModuleInstruction)

    val params = List(paramArity.value) {
        stack.popValue().bind().value
    }

    val label = Stack.Entry.Label(
        arity = returnArity,
        stackValuesDepth = stack.valuesDepth(),
        continuation = emptyList(),
    )

    val handler = Stack.Entry.ExceptionHandler(
        handlers = instruction.handlers,
    )

    blockExecutor(stack, label, executionInstructions, params, handler).bind()
}
