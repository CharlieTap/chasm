package io.github.charlietap.chasm.executor.invoker.function

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.FrameDispatcher
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.Stack.Entry.Instruction
import io.github.charlietap.chasm.executor.runtime.Stack.Entry.InstructionTag
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.default
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.ext.pushFrame
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

internal typealias WasmFunctionCall = (ExecutionContext, FunctionInstance.WasmFunction) -> Result<Unit, InvocationError>

internal inline fun WasmFunctionCall(
    context: ExecutionContext,
    instance: FunctionInstance.WasmFunction,
): Result<Unit, InvocationError> =
    WasmFunctionCall(
        context = context,
        instance = instance,
        instructionBlockExecutor = ::InstructionBlockExecutor,
        frameDispatcher = ::FrameDispatcher,
    )

internal inline fun WasmFunctionCall(
    context: ExecutionContext,
    instance: FunctionInstance.WasmFunction,
    crossinline instructionBlockExecutor: InstructionBlockExecutor,
    crossinline frameDispatcher: Dispatcher<Stack.Entry.ActivationFrame>,
): Result<Unit, InvocationError> = binding {

    val (stack) = context
    val type = instance.functionType
    val params = type.params.types.size
    val results = type.results.types.size

    val locals = MutableList<ExecutionValue>(params + instance.function.locals.size) { ExecutionValue.Uninitialised }
    for (i in (params - 1) downTo 0) {
        locals[i] = stack.popValue().bind().value
    }
    var idx = params
    for (local in instance.function.locals) {
        locals[idx++] = local.type.default().bind()
    }

    val frame = Stack.Entry.ActivationFrame(
        arity = Arity.Return(results),
        stackInstructionsDepth = stack.instructionsDepth(),
        stackLabelsDepth = stack.labelsDepth(),
        stackValuesDepth = stack.valuesDepth(),
        locals = locals,
        instance = instance.module,
    )

    stack.pushFrame(frame).bind()
    stack.push(Instruction(frameDispatcher(frame), InstructionTag.FRAME))

    val label = Stack.Entry.Label(
        arity = Arity.Return(results),
        stackInstructionsDepth = stack.instructionsDepth(),
        stackLabelsDepth = stack.labelsDepth(),
        stackValuesDepth = stack.valuesDepth(),
        continuation = emptyList(),
    )

    instructionBlockExecutor(stack, label, instance.function.body.instructions, null).bind()
}
