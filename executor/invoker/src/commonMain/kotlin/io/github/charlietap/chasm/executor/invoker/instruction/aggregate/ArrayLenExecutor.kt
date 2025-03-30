package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.ext.array
import io.github.charlietap.chasm.runtime.ext.popArrayAddress
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun ArrayLenExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayLen,
): InstructionPointer {
    val address = vstack.popArrayAddress()
    val arrayInstance = store.array(address)

    vstack.pushI32(arrayInstance.fields.size)

    return ip + 1
}
