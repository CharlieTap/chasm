package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun ReturnWasmFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instance: FunctionInstance.WasmFunction,
) {

    val frame = cstack.peekFrame()
    val type = instance.functionType
    val params = type.params.types.size
    val depths = frame.depths

    cstack.shrinkHandlers(depths.handlers)
    // leave frame and label admin instructions on the stack
    cstack.shrinkInstructions(depths.instructions + 2)
    // leave top label in place
    cstack.shrinkLabels(depths.labels + 1)
    vstack.shrink(params, depths.values)

    vstack.framePointer = depths.values
    vstack.push(instance.function.locals)
    cstack.push(instance.function.body.instructions)
}
