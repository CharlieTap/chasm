package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun F32ReinterpretI32Executor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32ReinterpretI32,
): InstructionPointer {
    val operand = instruction.operand(vstack).toInt()
    val result = try {
        Float.fromBits(operand).toRawBits().toLong()
    } catch (_: Throwable) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }

    instruction.destination(result, vstack)
    return ip + 1
}
