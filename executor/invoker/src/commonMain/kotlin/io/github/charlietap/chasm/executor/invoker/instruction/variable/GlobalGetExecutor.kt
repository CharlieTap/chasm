@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.variable

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.global
import io.github.charlietap.chasm.executor.runtime.ext.globalAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame

internal inline fun GlobalGetExecutor(
    context: ExecutionContext,
    instruction: VariableInstruction.GlobalGet,
): Result<Unit, InvocationError> = binding {
    val (stack, store) = context
    val frame = stack.peekFrame().bind()
    val address = frame.state.module.globalAddress(instruction.globalIdx).bind()
    val global = store.global(address).bind()
    stack.push(Stack.Entry.Value(global.value))
}
