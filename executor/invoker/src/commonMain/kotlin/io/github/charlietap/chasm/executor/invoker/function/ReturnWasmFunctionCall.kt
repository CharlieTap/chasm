package io.github.charlietap.chasm.executor.invoker.function

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.FrameDispatcher
import io.github.charlietap.chasm.executor.invoker.ext.functionType
import io.github.charlietap.chasm.executor.invoker.ext.grow
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.Stack.Entry.Instruction
import io.github.charlietap.chasm.executor.runtime.Stack.Entry.InstructionTag
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
): Result<Unit, InvocationError> =
    ReturnWasmFunctionCall(
        context = context,
        instance = instance,
        instructionBlockExecutor = ::InstructionBlockExecutor,
        frameDispatcher = ::FrameDispatcher,
    )

@Suppress("UNUSED_PARAMETER")
internal inline fun ReturnWasmFunctionCall(
    context: ExecutionContext,
    instance: FunctionInstance.WasmFunction,
    crossinline instructionBlockExecutor: InstructionBlockExecutor,
    crossinline frameDispatcher: Dispatcher<Stack.Entry.ActivationFrame>,
): Result<Unit, InvocationError> = binding {

    val (stack) = context
    val frame = stack.peekFrame().bind()
    val type = instance.functionType().bind()
    var params = type.params.types.size
    val results = type.results.types.size

    val locals = frame.state.locals
    locals.grow(params + instance.function.locals.size, ExecutionValue.Uninitialised)
    for (i in (params - 1) downTo 0) {
        locals[i] = stack.popValue().bind().value
    }
    for (local in instance.function.locals) {
        locals[params++] = local.type.default().bind()
    }

    do {
        val instruction = stack.popInstruction().bind()
    } while (instruction.tag != InstructionTag.FRAME)

    while (stack.labelsDepth() > frame.stackLabelsDepth) {
        stack.popLabel().bind()
    }

    while (stack.valuesDepth() > frame.stackValuesDepth) {
        stack.popValue().bind()
    }

    stack.push(Instruction(frameDispatcher(frame), InstructionTag.FRAME))

    val label = Stack.Entry.Label(
        arity = Arity.Return(results),
        stackValuesDepth = stack.valuesDepth(),
        continuation = emptyList(),
    )

    instructionBlockExecutor(stack, label, instance.function.body.instructions, emptyList(), null).bind()
}
