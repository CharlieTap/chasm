package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.ParametricInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

@Suppress("UNUSED_PARAMETER")
internal inline fun DropExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ParametricInstruction.Drop,
): InstructionPointer {
    vstack.pop()
    return ip + 1
}
