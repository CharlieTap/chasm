package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.function.WasmFunctionCallPlan
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instruction.CopyOperand
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun ReturnWasmFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instance: FunctionInstance.WasmFunction,
): Int = ReturnWasmFunctionCall(vstack, cstack, instance.callPlan)

internal fun ReturnWasmFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    plan: WasmFunctionCallPlan,
): Int {
    val frame = cstack.popFrame()

    cstack.shrinkHandlers(frame.handlerDepth)
    vstack.shrink(plan.params, frame.valueDepth)
    vstack.framePointer = frame.valueDepth
    vstack.reserveFrame(plan.frameSlots)
    plan.locals.forEachIndexed { index, value ->
        vstack.setFrameSlot(plan.interfaceSlots + index, value)
    }
    cstack.push(frame.copy(instance = plan.module))
    return plan.entryIp
}

internal fun ReturnWasmFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instance: FunctionInstance.WasmFunction,
    operands: List<CopyOperand>,
): Int = ReturnWasmFunctionCall(vstack, cstack, instance.callPlan, operands)

internal fun ReturnWasmFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    plan: WasmFunctionCallPlan,
    operands: List<CopyOperand>,
): Int {
    val currentFramePointer = vstack.framePointer
    val frame = cstack.popFrame()
    cstack.shrinkHandlers(frame.handlerDepth)
    val calleeFramePointer = frame.valueDepth
    vstack.reserveDepth(calleeFramePointer + plan.frameSlots)
    copyTailCallOperands(
        vstack = vstack,
        currentFramePointer = currentFramePointer,
        calleeFramePointer = calleeFramePointer,
        operands = operands,
    )
    vstack.shrink(0, calleeFramePointer)
    vstack.reserveDepth(calleeFramePointer + plan.frameSlots)
    plan.locals.forEachIndexed { index, value ->
        vstack.setFrameSlot(calleeFramePointer, plan.interfaceSlots + index, value)
    }

    vstack.framePointer = calleeFramePointer
    vstack.reserveFrame(plan.frameSlots)
    cstack.push(frame.copy(instance = plan.module))
    return plan.entryIp
}

private fun copyTailCallOperands(
    vstack: ValueStack,
    currentFramePointer: Int,
    calleeFramePointer: Int,
    operands: List<CopyOperand>,
) {
    copyOperands(
        vstack = vstack,
        currentFramePointer = currentFramePointer,
        destinationFramePointer = calleeFramePointer,
        operands = operands,
        order = operandCopyOrder(currentFramePointer, calleeFramePointer, operands),
    )
}
