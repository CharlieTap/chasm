package io.github.charlietap.chasm.executor.invoker.instruction.controlfused

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun CallExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedControlInstruction.WasmFunctionCall,
) {
    instruction.operands.forEach { operand ->
        vstack.push(operand(vstack))
    }
    instruction.instruction(vstack, cstack, store, context)
}
