@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.variable

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.global
import io.github.charlietap.chasm.executor.runtime.ext.globalAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.store.Store

internal inline fun GlobalSetExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: VariableInstruction.GlobalSet,
): Result<Unit, InvocationError> = binding {
    val frame = stack.peekFrame().bind()
    val address = frame.state.module.globalAddress(instruction.globalIdx.index()).bind()
    val global = store.global(address).bind()

    val value = stack.popValue().bind()
    global.value = value.value
}
