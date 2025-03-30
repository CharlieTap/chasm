package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.ext.tagAddress
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.ext.tag
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.instance.ExceptionInstance
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ReferenceValue

internal inline fun ThrowExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.Throw,
): InstructionPointer {
    val frame = cstack.peekFrame()
    val address = frame.instance
        .tagAddress(instruction.tagIndex)

    val instance = store.tag(address)
    val functionType = instance.type.type

    val params = LongArray(functionType.params.types.size) {
        vstack.pop()
    }

    val exceptionInstance = ExceptionInstance(
        tagAddress = address,
        fields = params,
    )

    store.exceptions.add(exceptionInstance)
    val exceptionAddress = Address.Exception(store.exceptions.size - 1)

    vstack.push(ReferenceValue.Exception(exceptionAddress).toLong())
//    cstack.push(throwRefDispatcher(ControlInstruction.ThrowRef))

    return ip + 1
}
