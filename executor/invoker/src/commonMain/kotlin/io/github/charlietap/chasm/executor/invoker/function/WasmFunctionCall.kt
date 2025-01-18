package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.admin.FrameInstructionExecutor
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.executor.runtime.stack.FrameStackDepths
import io.github.charlietap.chasm.executor.runtime.stack.LabelStackDepths

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

    val valuesDepth = stack.valuesDepth() - params
    stack.push(instance.function.locals)

    val depths = FrameStackDepths(
        handlers = stack.handlersDepth(),
        instructions = stack.instructionsDepth(),
        labels = stack.labelsDepth(),
        values = valuesDepth,
    )
    val frame = ActivationFrame(
        arity = results,
        depths = depths,
        instance = instance.module,
        previousFramePointer = stack.getFramePointer(),
    )

    stack.push(frame)
    stack.push(frameCleaner)

    val labelDepths = LabelStackDepths(
        instructions = stack.instructionsDepth(),
        labels = stack.labelsDepth(),
        values = stack.valuesDepth(),
    )

    val label = Stack.Entry.Label(
        arity = results,
        depths = labelDepths,
        continuation = null,
    )

    stack.setFramePointer(valuesDepth)
    instructionBlockExecutor(stack, label, instance.function.body.instructions, null)
}
