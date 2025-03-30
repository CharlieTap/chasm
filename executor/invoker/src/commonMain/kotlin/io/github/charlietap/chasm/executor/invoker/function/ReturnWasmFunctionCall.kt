package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun ReturnWasmFunctionCall(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instance: FunctionInstance.WasmFunction,
): InstructionPointer {

    val frame = cstack.peekFrame()
    val type = instance.functionType
    val params = type.params.types.size
    val depths = frame.depths

    cstack.shrinkHandlers(depths.handlers)
    vstack.shrink(params, depths.values)

    vstack.framePointer = depths.values
    vstack.push(instance.function.locals)
    context.setInstructions(instance.function.body.instructions)

    return InstructionPointer(0)
}
