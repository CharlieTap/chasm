package io.github.charlietap.chasm.executor.invoker.instruction.table

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.instruction.TableInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun TableGetExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: TableInstruction.TableGet,
): InstructionPointer {
    val tableInstance = instruction.table
    val elementIndex = vstack.popI32()

    vstack.push(tableInstance.element(elementIndex))
    return ip + 1
}
