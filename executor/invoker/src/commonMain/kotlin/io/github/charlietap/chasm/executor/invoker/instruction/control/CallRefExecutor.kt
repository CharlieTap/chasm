package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.ext.instruction
import io.github.charlietap.chasm.runtime.ext.popFunctionAddress
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun CallRefExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.CallRef,
): InstructionPointer {
    val address = vstack.popFunctionAddress()
    val instruction = store.instruction(address)
    return instruction(ip, vstack, cstack, store, context)
}
