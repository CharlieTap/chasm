package io.github.charlietap.chasm.executor.invoker.instruction.tablefused

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.instruction.FusedTableInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun TableGetExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableGet,
): InstructionPointer {
    val tableInstance = instruction.table

    instruction.destination(
        tableInstance.element(
            instruction.elementIndex(vstack).toInt(),
        ),
        vstack,
    )

    return ip + 1
}
