package io.github.charlietap.chasm.executor.invoker.function

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.ext.forEachReversed
import io.github.charlietap.chasm.executor.invoker.ext.grow
import io.github.charlietap.chasm.executor.runtime.Stack.Entry.Instruction
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.default
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

internal inline fun ReturnWasmFunctionCall(
    context: ExecutionContext,
    instance: FunctionInstance.WasmFunction,
): Result<Unit, InvocationError> = binding {

    val (stack) = context
    val frame = stack.peekFrame().bind()
    val type = instance.functionType
    var params = type.params.types.size

    val locals = frame.locals
    locals.grow(params + instance.function.locals.size, ExecutionValue.Uninitialised)
    for (i in (params - 1) downTo 0) {
        locals[i] = stack.popValue().bind().value
    }
    for (local in instance.function.locals) {
        locals[params++] = local.type.default().bind()
    }

    stack.shrinkHandlers(0, frame.stackHandlersDepth)
    // leave frame and label admin instructions on the stack
    stack.shrinkInstructions(0, frame.stackInstructionsDepth + 2)
    // leave top label in place
    stack.shrinkLabels(0, frame.stackLabelsDepth + 1)
    stack.shrinkValues(0, frame.stackValuesDepth)

    instance.function.body.instructions.forEachReversed { instruction ->
        stack.push(Instruction(instruction))
    }
}
