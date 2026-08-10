package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.function.WasmFunctionCallPlan
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
): Int = WasmFunctionCall(vstack, cstack, instance.callPlan, returnIp)

internal fun WasmFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    plan: WasmFunctionCallPlan,
    returnIp: Int,
): Int {
    val valueDepth = vstack.depth() - plan.params

    cstack.push(
        ActivationFrame(
            arity = plan.results,
            handlerDepth = cstack.handlersDepth(),
            valueDepth = valueDepth,
            instance = plan.module,
            previousFramePointer = vstack.framePointer,
            returnIp = returnIp,
        ),
    )

    vstack.framePointer = valueDepth
    vstack.reserveFrame(plan.frameSlots)
    plan.locals.forEachIndexed { index, value ->
        vstack.setFrameSlot(plan.interfaceSlots + index, value)
    }
    return plan.entryIp
}

internal fun WasmFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instance: FunctionInstance.WasmFunction,
    resultSlotBase: Int,
    callFrameSlot: Int,
    returnIp: Int,
): Int = WasmFunctionCall(
    vstack,
    cstack,
    instance.callPlan,
    resultSlotBase,
    callFrameSlot,
    returnIp,
)

internal fun WasmFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    plan: WasmFunctionCallPlan,
    resultSlotBase: Int,
    callFrameSlot: Int,
    returnIp: Int,
): Int {
    val callerFramePointer = vstack.framePointer
    val valueDepth = vstack.depth()
    val calleeFramePointer = callerFramePointer + callFrameSlot

    vstack.reserveDepth(calleeFramePointer + plan.frameSlots)
    plan.locals.forEachIndexed { index, value ->
        vstack.setFrameSlot(calleeFramePointer, plan.interfaceSlots + index, value)
    }

    cstack.push(
        ActivationFrame(
            arity = plan.results,
            handlerDepth = cstack.handlersDepth(),
            valueDepth = valueDepth,
            instance = plan.module,
            previousFramePointer = callerFramePointer,
            resultSlotBase = resultSlotBase,
            returnIp = returnIp,
        ),
    )

    vstack.framePointer = calleeFramePointer
    vstack.reserveFrame(plan.frameSlots)
    return plan.entryIp
}

internal fun WasmFunctionCallWithoutLocals(
    vstack: ValueStack,
    cstack: ControlStack,
    plan: WasmFunctionCallPlan,
    resultSlotBase: Int,
    callFrameSlot: Int,
    returnIp: Int,
): Int {
    val callerFramePointer = vstack.framePointer
    val valueDepth = vstack.depth()
    val calleeFramePointer = callerFramePointer + callFrameSlot

    vstack.reserveDepth(calleeFramePointer + plan.frameSlots)
    cstack.push(
        ActivationFrame(
            arity = plan.results,
            handlerDepth = cstack.handlersDepth(),
            valueDepth = valueDepth,
            instance = plan.module,
            previousFramePointer = callerFramePointer,
            resultSlotBase = resultSlotBase,
            returnIp = returnIp,
        ),
    )

    vstack.framePointer = calleeFramePointer
    vstack.reserveFrame(plan.frameSlots)
    return plan.entryIp
}
