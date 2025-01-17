package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ThrowRefDispatcher
import io.github.charlietap.chasm.executor.invoker.ext.tagAddress
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.tag
import io.github.charlietap.chasm.executor.runtime.instance.ExceptionInstance
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal fun ThrowExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Throw,
) = ThrowExecutor(
    context = context,
    instruction = instruction,
    throwRefDispatcher = ::ThrowRefDispatcher,
)

internal inline fun ThrowExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Throw,
    crossinline throwRefDispatcher: Dispatcher<ControlInstruction.ThrowRef>,
) {

    val (stack, store) = context
    val frame = stack.peekFrame()
    val address = frame.instance
        .tagAddress(instruction.tagIndex)

    val instance = store.tag(address)
    val functionType = instance.type.type

    val params = List(functionType.params.types.size) {
        stack.popValue()
    }

    val exceptionInstance = ExceptionInstance(
        tagAddress = address,
        fields = params,
    )

    store.exceptions.add(exceptionInstance)
    val exceptionAddress = Address.Exception(store.exceptions.size - 1)

    stack.push(ReferenceValue.Exception(exceptionAddress))
    stack.push(throwRefDispatcher(ControlInstruction.ThrowRef))
}
