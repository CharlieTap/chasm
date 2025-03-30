package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.ParametricInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

@Suppress("UNUSED_PARAMETER")
internal inline fun SelectExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ParametricInstruction.Select,
): InstructionPointer {
    val select = vstack.pop()
    val value = vstack.pop()

    if (select == 0L) {
        vstack.pop()
        vstack.push(value)
    }

    return ip + 1
}
