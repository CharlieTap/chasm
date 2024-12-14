@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.function

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.FrameDispatcher
import io.github.charlietap.chasm.executor.invoker.ext.functionType
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.Stack.Entry.Instruction
import io.github.charlietap.chasm.executor.runtime.Stack.Entry.InstructionTag
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.default
import io.github.charlietap.chasm.executor.runtime.ext.popFrame
import io.github.charlietap.chasm.executor.runtime.ext.popInstruction
import io.github.charlietap.chasm.executor.runtime.ext.popLabel
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.ext.pushFrame
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

internal inline fun ReturnWasmFunctionCall(
    store: Store,
    stack: Stack,
    instance: FunctionInstance.WasmFunction,
): Result<Unit, InvocationError> =
    ReturnWasmFunctionCall(
        store = store,
        stack = stack,
        instance = instance,
        instructionBlockExecutor = ::InstructionBlockExecutor,
        frameDispatcher = ::FrameDispatcher,
    )

@Suppress("UNUSED_PARAMETER")
internal inline fun ReturnWasmFunctionCall(
    store: Store,
    stack: Stack,
    instance: FunctionInstance.WasmFunction,
    crossinline instructionBlockExecutor: InstructionBlockExecutor,
    crossinline frameDispatcher: Dispatcher<Stack.Entry.ActivationFrame>,
): Result<Unit, InvocationError> = binding {

    val frame = stack.popFrame().bind()
    val type = instance.functionType().bind()
    var params = type.params.types.size
    val results = type.results.types.size

    val locals = MutableList<ExecutionValue>(params + instance.function.locals.size) { ExecutionValue.Uninitialised }
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

    frame.state.locals = locals
    stack.pushFrame(frame).bind()
    stack.push(Instruction(frameDispatcher(frame), InstructionTag.FRAME))

    val label = Stack.Entry.Label(
        arity = Arity.Return(results),
        stackValuesDepth = stack.valuesDepth(),
        continuation = emptyList(),
    )

    instructionBlockExecutor(stack, label, instance.function.body.instructions, emptyList(), null).bind()
}
