package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance

internal inline fun ReturnWasmFunctionCall(
    context: ExecutionContext,
    instance: FunctionInstance.WasmFunction,
) {
    val (stack) = context
    val frame = stack.peekFrame()
    val type = instance.functionType
    val params = type.params.types.size
    val depths = frame.depths

    stack.shrinkHandlers(0, depths.handlers)
    // leave frame and label admin instructions on the stack
    stack.shrinkInstructions(0, depths.instructions + 2)
    // leave top label in place
    stack.shrinkLabels(0, depths.labels + 1)
    stack.shrinkValues(params, depths.values)

    stack.setFramePointer(depths.values)
    stack.push(instance.function.locals)
    stack.push(instance.function.body.instructions)
}
