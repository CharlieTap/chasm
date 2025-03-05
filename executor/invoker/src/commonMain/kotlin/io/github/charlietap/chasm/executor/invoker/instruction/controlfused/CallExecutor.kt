package io.github.charlietap.chasm.executor.invoker.instruction.controlfused

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedControlInstruction

internal inline fun CallExecutor(
    context: ExecutionContext,
    instruction: FusedControlInstruction.WasmFunctionCall,
) {
    val stack = context.vstack
    instruction.operands.forEach { operand ->
        stack.push(operand(stack))
    }
    instruction.instruction(context)
}
