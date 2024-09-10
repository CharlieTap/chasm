@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.ext.pushInstruction
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.ext.tag
import io.github.charlietap.chasm.executor.runtime.ext.tagAddress
import io.github.charlietap.chasm.executor.runtime.instance.ExceptionInstance
import io.github.charlietap.chasm.executor.runtime.instruction.ModuleInstruction
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal typealias ThrowExecutor = (Store, Stack, ControlInstruction.Throw) -> Result<Unit, InvocationError>

internal inline fun ThrowExecutor(
    store: Store,
    stack: Stack,
    instruction: ControlInstruction.Throw,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrame().bind()
    val address = frame.state.module.tagAddress(instruction.tagIndex).bind()

    val instance = store.tag(address).bind()
    val functionType = instance.type.type

    val params = List(functionType.params.types.size) {
        stack.popValue().bind().value
    }

    val exceptionInstance = ExceptionInstance(
        tagAddress = address,
        fields = params,
    )

    store.exceptions.add(exceptionInstance)
    val exceptionAddress = Address.Exception(store.exceptions.size - 1)

    stack.pushValue(ReferenceValue.Exception(exceptionAddress))
    stack.pushInstruction(ModuleInstruction(ControlInstruction.ThrowRef))
}
