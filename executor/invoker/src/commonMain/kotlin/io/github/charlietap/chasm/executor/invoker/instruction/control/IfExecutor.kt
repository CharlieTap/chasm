@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias IfExecutor = (Store, Stack, ControlInstruction.If) -> Result<Unit, InvocationError>

internal inline fun IfExecutor(
    store: Store,
    stack: Stack,
    instruction: ControlInstruction.If,
): Result<Unit, InvocationError> =
    IfExecutor(
        store = store,
        stack = stack,
        instruction = instruction,
        blockExecutor = ::BlockExecutor,
    )

internal inline fun IfExecutor(
    store: Store,
    stack: Stack,
    instruction: ControlInstruction.If,
    crossinline blockExecutor: BlockExecutor,
): Result<Unit, InvocationError> = binding {
    val firstBlock = stack.popI32().bind() != 0

    if (firstBlock) {
        blockExecutor(store, stack, instruction.blockType, instruction.thenInstructions).bind()
    } else {
        blockExecutor(store, stack, instruction.blockType, instruction.elseInstructions ?: emptyList()).bind()
    }
}
