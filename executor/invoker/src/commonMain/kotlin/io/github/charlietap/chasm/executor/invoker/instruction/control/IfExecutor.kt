@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popI32

internal inline fun IfExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.If,
): Result<Unit, InvocationError> =
    IfExecutor(
        context = context,
        instruction = instruction,
        blockExecutor = ::BlockExecutor,
    )

internal inline fun IfExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.If,
    crossinline blockExecutor: BlockExecutor,
): Result<Unit, InvocationError> = binding {
    val (stack, store) = context
    val firstBlock = stack.popI32().bind() != 0

    if (firstBlock) {
        blockExecutor(store, stack, instruction.blockType, instruction.thenInstructions).bind()
    } else {
        blockExecutor(store, stack, instruction.blockType, instruction.elseInstructions ?: emptyList()).bind()
    }
}
