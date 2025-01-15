package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.invoker.ext.grow
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.default
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

internal inline fun ReturnWasmFunctionCall(
    context: ExecutionContext,
    instance: FunctionInstance.WasmFunction,
) {

    val (stack) = context
    val frame = stack.peekFrame()
    val type = instance.functionType
    var params = type.params.types.size

    val locals = frame.locals
    locals.grow(params + instance.function.locals.size, ExecutionValue.Uninitialised)
    for (i in (params - 1) downTo 0) {
        locals[i] = stack.popValue()
    }
    for (local in instance.function.locals) {
        locals[params++] = local.type.default().bind()
    }

    val depths = frame.depths
    stack.shrinkHandlers(0, depths.handlers)
    // leave frame and label admin instructions on the stack
    stack.shrinkInstructions(0, depths.instructions + 2)
    // leave top label in place
    stack.shrinkLabels(0, depths.labels + 1)
    stack.shrinkValues(0, depths.values)

    stack.push(instance.function.body.instructions)
}
