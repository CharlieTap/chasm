package io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused

import io.github.charlietap.chasm.executor.invoker.ext.allocateArray
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ReferenceValue

internal inline fun ArrayNewExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedAggregateInstruction.ArrayNew,
) {
    val size = instruction.size(vstack).toInt()
    val value = instruction.value(vstack)

    val fields = LongArray(size) {
        value
    }

    val instance = ArrayInstance(instruction.rtt, instruction.arrayType, fields)
    val address = store.allocateArray(instance)
    val reference = ReferenceValue.Array(address)

    instruction.destination(reference.toLong(), vstack)
}
