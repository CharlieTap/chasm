package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias BlockExecutor = (Store, Stack, FunctionType, List<DispatchableInstruction>) -> Result<Unit, InvocationError>

internal inline fun BlockExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Block,
): Result<Unit, InvocationError> =
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
    instructions: List<DispatchableInstruction>,
): Result<Unit, InvocationError> =
    BlockExecutor(
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
    instructions: List<DispatchableInstruction>,
    crossinline instructionBlockExecutor: InstructionBlockExecutor,
): Result<Unit, InvocationError> = binding {

    val (paramArity, resultArity) = functionType.let {
        Arity.Argument(functionType.params.types.size) to Arity.Return(functionType.results.types.size)
    }

    val label = Stack.Entry.Label(
        arity = resultArity,
        stackInstructionsDepth = stack.instructionsDepth(),
        stackLabelsDepth = stack.labelsDepth(),
        stackValuesDepth = stack.valuesDepth() - paramArity.value,
        continuation = emptyList(),
    )

    instructionBlockExecutor(stack, label, instructions, null).bind()
}
