package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.admin.FrameInstructionExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.executor.runtime.stack.ControlStack
import io.github.charlietap.chasm.executor.runtime.stack.StackDepths

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
    val cstack = context.cstack
    val vstack = context.vstack
    val type = instance.functionType
    val params = type.params.types.size
    val results = type.results.types.size

    val valuesDepth = vstack.depth() - params
    vstack.push(instance.function.locals)

    val depths = StackDepths(
        handlers = cstack.handlersDepth(),
        instructions = cstack.instructionsDepth(),
        labels = cstack.labelsDepth(),
        values = valuesDepth,
    )
    val frame = ActivationFrame(
        arity = results,
        depths = depths,
        instance = instance.module,
        previousFramePointer = vstack.framePointer,
    )

    cstack.push(frame)
    cstack.push(frameCleaner)

    val labelDepths = StackDepths(
        handlers = cstack.handlersDepth(),
        instructions = cstack.instructionsDepth(),
        labels = cstack.labelsDepth(),
        values = vstack.depth(),
    )

    val label = ControlStack.Entry.Label(
        arity = results,
        depths = labelDepths,
        continuation = null,
    )

    vstack.framePointer = valuesDepth
    instructionBlockExecutor(cstack, label, instance.function.body.instructions, null)
}
