package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal typealias WasmFunctionCall = (
    ValueStack,
    ControlStack,
    Store,
    ExecutionContext,
    FunctionInstance.WasmFunction,
    Int,
) -> Int

internal fun WasmFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instance: FunctionInstance.WasmFunction,
    returnIp: Int,
): Int {
    val type = instance.functionType
    val params = type.params.types.size
    val results = type.results.types.size
    val interfaceSlots = maxOf(params, results)
    val valueDepth = vstack.depth() - params

    cstack.push(
        ActivationFrame(
            arity = results,
            handlerDepth = cstack.handlersDepth(),
            valueDepth = valueDepth,
            instance = instance.module,
            previousFramePointer = vstack.framePointer,
            returnIp = returnIp,
        ),
    )

    vstack.framePointer = valueDepth
    vstack.reserveFrame(instance.function.frameSlots)
    instance.function.locals.forEachIndexed { index, value ->
        vstack.setFrameSlot(interfaceSlots + index, value)
    }
    return instance.function.body.entryIp
}

internal fun WasmFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instance: FunctionInstance.WasmFunction,
    resultSlots: List<Int>,
    callFrameSlot: Int,
    returnIp: Int,
): Int {
    val type = instance.functionType
    val params = type.params.types.size
    val results = type.results.types.size
    val interfaceSlots = maxOf(params, results)
    val callerFramePointer = vstack.framePointer
    val valueDepth = vstack.depth()
    val calleeFramePointer = callerFramePointer + callFrameSlot

    vstack.reserveDepth(calleeFramePointer + instance.function.frameSlots)
    instance.function.locals.forEachIndexed { index, value ->
        vstack.setFrameSlot(calleeFramePointer, interfaceSlots + index, value)
    }

    cstack.push(
        ActivationFrame(
            arity = results,
            handlerDepth = cstack.handlersDepth(),
            valueDepth = valueDepth,
            instance = instance.module,
            previousFramePointer = callerFramePointer,
            visibleResultBase = StrictVisibleResultBase(resultSlots),
            returnIp = returnIp,
        ),
    )

    vstack.framePointer = calleeFramePointer
    vstack.reserveFrame(instance.function.frameSlots)
    return instance.function.body.entryIp
}
