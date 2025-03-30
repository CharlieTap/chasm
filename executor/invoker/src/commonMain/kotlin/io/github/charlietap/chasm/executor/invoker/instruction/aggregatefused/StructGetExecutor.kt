package io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.ext.field
import io.github.charlietap.chasm.runtime.ext.struct
import io.github.charlietap.chasm.runtime.ext.toStructAddress
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun StructGetExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedAggregateInstruction.StructGet,
): InstructionPointer {
    val address = instruction.address(vstack).toStructAddress()
    val structInstance = store.struct(address)
    val fieldValue = structInstance.field(instruction.fieldIndex)

    instruction.destination(fieldValue, vstack)

    return ip + 1
}
