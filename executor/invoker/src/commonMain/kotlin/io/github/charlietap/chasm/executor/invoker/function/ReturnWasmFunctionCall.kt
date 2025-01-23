package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance

internal inline fun ReturnWasmFunctionCall(
    context: ExecutionContext,
    instance: FunctionInstance.WasmFunction,
) {
    val cstack = context.cstack
    val vstack = context.vstack
    val frame = cstack.peekFrame()
    val type = instance.functionType
    val params = type.params.types.size
    val depths = frame.depths

    cstack.shrinkHandlers(0, depths.handlers)
    // leave frame and label admin instructions on the stack
    cstack.shrinkInstructions(0, depths.instructions + 2)
    // leave top label in place
    cstack.shrinkLabels(0, depths.labels + 1)
    vstack.shrink(params, depths.values)

    vstack.framePointer = depths.values
    vstack.push(instance.function.locals)
    cstack.push(instance.function.body.instructions)
}
