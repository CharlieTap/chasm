package io.github.charlietap.chasm.executor.invoker.instruction.numeric.testop

import io.github.charlietap.chasm.executor.invoker.ext.eqz
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.ext.testOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I64EqzExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericInstruction.I64Eqz,
): InstructionPointer {
    vstack.testOperation(Long::eqz)
    return ip + 1
}
