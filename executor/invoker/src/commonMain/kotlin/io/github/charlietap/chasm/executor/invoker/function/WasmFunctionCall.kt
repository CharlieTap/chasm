package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.StackDepths
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal typealias WasmFunctionCall = (InstructionPointer, ValueStack, ControlStack, Store, ExecutionContext, FunctionInstance.WasmFunction) -> InstructionPointer

internal inline fun WasmFunctionCall(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instance: FunctionInstance.WasmFunction,
): InstructionPointer {
    val type = instance.functionType
    val params = type.params.types.size
    val results = type.results.types.size

    val valuesDepth = vstack.depth() - params
    vstack.push(instance.function.locals)

    val depths = StackDepths(
        handlers = cstack.handlersDepth(),
        values = valuesDepth,
    )
    val frame = ActivationFrame(
        arity = results,
        depths = depths,
        instance = instance.module,
        previousInstructions = context.getInstructions(),
        previousInstructionPointer = ip,
        previousFramePointer = vstack.framePointer,
    )

    cstack.push(frame)
    vstack.framePointer = valuesDepth
    context.setInstructions(instance.function.body.instructions)

    return InstructionPointer(0)
}
