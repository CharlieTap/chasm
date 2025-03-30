package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.ext.array
import io.github.charlietap.chasm.runtime.ext.packedField
import io.github.charlietap.chasm.runtime.ext.popArrayAddress
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun ArrayGetSignedExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayGetSigned,
): InstructionPointer = ArrayGetSignedExecutor(
    ip = ip,
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    fieldUnpacker = ::FieldUnpacker,
)

internal inline fun ArrayGetSignedExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    crossinline fieldUnpacker: FieldUnpacker,
): InstructionPointer {
    val fieldIndex = vstack.popI32()
    val address = vstack.popArrayAddress()
    val arrayInstance = store.array(address)

    val (packed, type) = arrayInstance.packedField(fieldIndex)
    val unpackedValue = fieldUnpacker(packed, type, true)

    vstack.push(unpackedValue)
    return ip + 1
}
