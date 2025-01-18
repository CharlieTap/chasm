package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.admin.FrameInstructionExecutor
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

internal typealias WasmFunctionCall = (ExecutionContext, FunctionInstance.WasmFunction) -> Unit

internal inline fun WasmFunctionCall(
    context: ExecutionContext,
    instance: FunctionInstance.WasmFunction,
) =
    WasmFunctionCall(
        context = context,
        instance = instance,
        instructionBlockExecutor = ::InstructionBlockExecutor,
        frameCleaner = ::FrameInstructionExecutor,
    )

internal inline fun WasmFunctionCall(
    context: ExecutionContext,
    instance: FunctionInstance.WasmFunction,
    crossinline instructionBlockExecutor: InstructionBlockExecutor,
    noinline frameCleaner: DispatchableInstruction,
) {

    val (stack) = context
    val type = instance.functionType
    val params = type.params.types.size
    val results = type.results.types.size

    val locals = MutableList<ExecutionValue>(params + instance.function.locals.size) { ExecutionValue.Uninitialised }
    for (i in (params - 1) downTo 0) {
        locals[i] = stack.popValue()
    }
    var idx = params
    for (local in instance.function.locals) {
        locals[idx++] = local
    }

    val depths = stack.depths()
    val frame = ActivationFrame(
        arity = results,
        depths = depths,
        locals = locals,
        instance = instance.module,
    )

    stack.push(frame)
    stack.push(frameCleaner)

    val label = Stack.Entry.Label(
        arity = results,
        depths = stack.depths(),
        continuation = null,
    )

    instructionBlockExecutor(stack, label, instance.function.body.instructions, null)
}
