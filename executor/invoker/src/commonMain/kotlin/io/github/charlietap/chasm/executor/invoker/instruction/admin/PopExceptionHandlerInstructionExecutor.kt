package io.github.charlietap.chasm.executor.invoker.instruction.admin

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

@Suppress("UNUSED_PARAMETER")
internal inline fun PopExceptionHandlerInstructionExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AdminInstruction.Handler,
): InstructionPointer {
    cstack.popHandler()
    return ip + 1
}
