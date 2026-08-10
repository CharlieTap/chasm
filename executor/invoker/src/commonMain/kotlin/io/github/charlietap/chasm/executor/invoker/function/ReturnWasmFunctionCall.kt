package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.function.WasmFunctionCallPlan
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instruction.ControlSuperInstruction
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
    operands: List<ControlSuperInstruction.CallOperand>,
): Int = ReturnWasmFunctionCall(vstack, cstack, instance.callPlan, operands)

internal fun ReturnWasmFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    plan: WasmFunctionCallPlan,
    operands: List<ControlSuperInstruction.CallOperand>,
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
    operands: List<ControlSuperInstruction.CallOperand>,
) {
    when (tailCallOperandCopyOrder(currentFramePointer, calleeFramePointer, operands)) {
        TailCallOperandCopyOrder.None -> Unit
        TailCallOperandCopyOrder.Forward -> {
            var index = 0
            while (index < operands.size) {
                copyTailCallOperand(vstack, currentFramePointer, calleeFramePointer, operands, index)
                index++
            }
        }
        TailCallOperandCopyOrder.Reverse -> {
            var index = operands.lastIndex
            while (index >= 0) {
                copyTailCallOperand(vstack, currentFramePointer, calleeFramePointer, operands, index)
                index--
            }
        }
        TailCallOperandCopyOrder.Staged -> {
            val values = LongArray(operands.size) { index ->
                tailCallOperand(vstack, currentFramePointer, operands[index])
            }
            var index = 0
            while (index < values.size) {
                vstack.setFrameSlot(calleeFramePointer, index, values[index])
                index++
            }
        }
    }
}

private fun tailCallOperandCopyOrder(
    currentFramePointer: Int,
    calleeFramePointer: Int,
    operands: List<ControlSuperInstruction.CallOperand>,
): TailCallOperandCopyOrder {
    var index = 0
    while (index < operands.size) {
        val operand = operands[index]
        if (
            operand !is ControlSuperInstruction.CallOperand.Slot ||
            currentFramePointer + operand.slot != calleeFramePointer + index
        ) {
            break
        }
        index++
    }
    if (index == operands.size) return TailCallOperandCopyOrder.None

    index = 0
    while (index < operands.size) {
        val destination = calleeFramePointer + index
        var remainingIndex = index + 1
        while (remainingIndex < operands.size) {
            if (operands[remainingIndex].readsSlot(currentFramePointer, destination)) {
                break
            }
            remainingIndex++
        }
        if (remainingIndex < operands.size) break
        index++
    }
    if (index == operands.size) return TailCallOperandCopyOrder.Forward

    index = operands.lastIndex
    while (index >= 0) {
        val destination = calleeFramePointer + index
        var remainingIndex = index - 1
        while (remainingIndex >= 0) {
            if (operands[remainingIndex].readsSlot(currentFramePointer, destination)) {
                break
            }
            remainingIndex--
        }
        if (remainingIndex >= 0) break
        index--
    }
    return if (index < 0) TailCallOperandCopyOrder.Reverse else TailCallOperandCopyOrder.Staged
}

private fun copyTailCallOperand(
    vstack: ValueStack,
    currentFramePointer: Int,
    calleeFramePointer: Int,
    operands: List<ControlSuperInstruction.CallOperand>,
    index: Int,
) {
    val value = tailCallOperand(vstack, currentFramePointer, operands[index])
    vstack.setFrameSlot(calleeFramePointer, index, value)
}

private fun tailCallOperand(
    vstack: ValueStack,
    framePointer: Int,
    operand: ControlSuperInstruction.CallOperand,
): Long = when (operand) {
    is ControlSuperInstruction.CallOperand.Immediate -> operand.value
    is ControlSuperInstruction.CallOperand.Slot -> vstack.getFrameSlot(framePointer, operand.slot)
}

private fun ControlSuperInstruction.CallOperand.readsSlot(framePointer: Int, slot: Int): Boolean =
    this is ControlSuperInstruction.CallOperand.Slot && framePointer + this.slot == slot

private enum class TailCallOperandCopyOrder {
    None,
    Forward,
    Reverse,
    Staged,
}
