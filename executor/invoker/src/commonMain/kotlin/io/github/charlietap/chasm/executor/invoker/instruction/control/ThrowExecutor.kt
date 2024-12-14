package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ThrowRefDispatcher
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.ext.pushInstruction
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.ext.tag
import io.github.charlietap.chasm.executor.runtime.ext.tagAddress
import io.github.charlietap.chasm.executor.runtime.instance.ExceptionInstance
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal fun ThrowExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Throw,
): Result<Unit, InvocationError> =
    ThrowExecutor(
        context = context,
        instruction = instruction,
        throwRefDispatcher = ::ThrowRefDispatcher,
    )

internal inline fun ThrowExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Throw,
    crossinline throwRefDispatcher: Dispatcher<ControlInstruction.ThrowRef>,
): Result<Unit, InvocationError> = binding {

    val (stack, store) = context
    val frame = stack.peekFrame().bind()
    val address = frame.instance
        .tagAddress(instruction.tagIndex)
        .bind()

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
    stack.pushInstruction(throwRefDispatcher(ControlInstruction.ThrowRef))
}
