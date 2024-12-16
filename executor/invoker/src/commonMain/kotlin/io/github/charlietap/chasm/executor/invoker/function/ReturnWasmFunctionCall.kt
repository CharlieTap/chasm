package io.github.charlietap.chasm.executor.invoker.function

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.ext.grow
import io.github.charlietap.chasm.executor.runtime.Stack.Entry.Instruction
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.default
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popInstruction
import io.github.charlietap.chasm.executor.runtime.ext.popLabel
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

    while (stack.instructionsDepth() > frame.stackInstructionsDepth + 2) {
        stack.popInstruction().bind()
    }

    while (stack.labelsDepth() > frame.stackLabelsDepth + 1) {
        stack.popLabel().bind()
    }

    while (stack.valuesDepth() > frame.stackValuesDepth) {
        stack.popValue().bind()
    }

    instance.function.body.instructions.asReversed().forEach { instruction ->
        stack.push(Instruction(instruction))
    }
}
