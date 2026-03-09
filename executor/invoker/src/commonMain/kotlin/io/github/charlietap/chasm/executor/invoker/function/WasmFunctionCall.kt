package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.StackDepths
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal typealias WasmFunctionCall = (ValueStack, ControlStack, Store, ExecutionContext, FunctionInstance.WasmFunction) -> Unit

internal inline fun WasmFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instance: FunctionInstance.WasmFunction,
) {
    val type = instance.functionType
    val params = type.params.types.size
    val results = type.results.types.size
    val interfaceSlots = maxOf(params, results)

    val valuesDepth = vstack.depth() - params
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
        frameSlotMode = instance.function.frameSlotMode,
    )

    cstack.push(frame)

    vstack.framePointer = valuesDepth
    if (instance.function.frameSlotMode) {
        vstack.reserveFrame(instance.function.frameSlots)
        instance.function.locals.forEachIndexed { index, value ->
            vstack.setFrameSlot(interfaceSlots + index, value)
        }
    } else {
        vstack.push(instance.function.locals)
        vstack.reserveFrame(instance.function.frameSlots)
    }

    val labelDepths = StackDepths(
        handlers = cstack.handlersDepth(),
        instructions = cstack.instructionsDepth() + 1, // account for endfunction instruction added later
        labels = cstack.labelsDepth(),
        values = vstack.depth(),
    )

    val label = ControlStack.Entry.Label(
        arity = results,
        depths = labelDepths,
        continuation = null,
    )

    cstack.push(label)
    cstack.push(instance.function.body.instructions)
}

internal inline fun WasmFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instance: FunctionInstance.WasmFunction,
    resultSlots: List<Int>,
    callFrameSlot: Int,
) {
    val type = instance.functionType
    val params = type.params.types.size
    val results = type.results.types.size
    val interfaceSlots = maxOf(params, results)
    val callerFramePointer = vstack.framePointer
    val valuesDepth = vstack.depth()
    val calleeFramePointer = callerFramePointer + callFrameSlot

    vstack.reserveDepth(calleeFramePointer + instance.function.frameSlots)
    instance.function.locals.forEachIndexed { index, value ->
        vstack.setFrameSlot(calleeFramePointer, interfaceSlots + index, value)
    }

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
        previousFramePointer = callerFramePointer,
        frameSlotMode = true,
        visibleResultBase = StrictVisibleResultBase(resultSlots),
    )

    cstack.push(frame)

    val labelDepths = StackDepths(
        handlers = cstack.handlersDepth(),
        instructions = cstack.instructionsDepth() + 1,
        labels = cstack.labelsDepth(),
        values = vstack.depth(),
    )

    val label = ControlStack.Entry.Label(
        arity = results,
        depths = labelDepths,
        continuation = null,
    )

    vstack.framePointer = calleeFramePointer
    vstack.reserveFrame(instance.function.frameSlots)
    cstack.push(label)
    cstack.push(instance.function.body.instructions)
}
