package io.github.charlietap.chasm.executor.invoker.instruction.parametricfused

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedParametricInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun SelectExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedParametricInstruction.Select,
) {
    val select = instruction.const(vstack)
    val val2 = instruction.val2(vstack)
    val val1 = instruction.val1(vstack)

    if (select == 0L) {
        instruction.destination(val2, vstack)
    } else {
        instruction.destination(val1, vstack)
    }
}
