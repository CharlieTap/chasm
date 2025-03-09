package io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.struct
import io.github.charlietap.chasm.runtime.ext.toStructAddress
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun StructSetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedAggregateInstruction.StructSet,
) {
    val value = instruction.value(vstack)
    val address = instruction.address(vstack).toStructAddress()
    val structInstance = store.struct(address)

    structInstance.fields[instruction.fieldIndex] = value
}
