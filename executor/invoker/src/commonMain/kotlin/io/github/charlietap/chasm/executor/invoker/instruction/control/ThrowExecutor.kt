package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.ext.tagAddress
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.ext.tag
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.instance.ExceptionInstance
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ReferenceValue

internal fun ThrowExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    instruction: ControlInstruction.Throw,
): Int {
    val address = cstack.frameInstance()
        .tagAddress(instruction.tagIndex)

    val instance = store.tag(address)
    val functionType = instance.type.functionType

    val params = LongArray(functionType.params.types.size) {
        vstack.pop()
    }

    val exceptionInstance = ExceptionInstance(
        tagAddress = address,
        fields = params,
    )

    store.exceptions.add(exceptionInstance)
    val exceptionAddress = Address.Exception(store.exceptions.size - 1)

    return ThrowRefValueExecutor(
        vstack = vstack,
        cstack = cstack,
        store = store,
        ref = ReferenceValue.Exception(exceptionAddress).toLong(),
    )
}
