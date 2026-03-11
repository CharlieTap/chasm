package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instruction.ControlSuperInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun ReturnWasmFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instance: FunctionInstance.WasmFunction,
) {
    val frame = cstack.popFrame()
    val type = instance.functionType
    val params = type.params.types.size
    val results = type.results.types.size
    val interfaceSlots = maxOf(params, results)
    val depths = frame.depths

    cstack.shrinkHandlers(depths.handlers)
    cstack.shrinkInstructions(depths.instructions)
    // leave top label in place
    cstack.shrinkLabels(depths.labels + 1)
    vstack.shrink(params, depths.values)

    vstack.framePointer = depths.values
    if (instance.function.frameSlotMode) {
        vstack.reserveFrame(instance.function.frameSlots)
        instance.function.locals.forEachIndexed { index, value ->
            vstack.setFrameSlot(interfaceSlots + index, value)
        }
    } else {
        vstack.push(instance.function.locals)
        vstack.reserveFrame(instance.function.frameSlots)
    }
    cstack.push(
        frame.copy(
            instance = instance.module,
            frameSlotMode = instance.function.frameSlotMode,
            visibleResultBase = FrameSlotVisibleResultBase(frame),
        ),
    )
    cstack.push(instance.function.body.instructions)
}

internal inline fun ReturnWasmFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instance: FunctionInstance.WasmFunction,
    operands: List<ControlSuperInstruction.CallOperand>,
) {
    val currentFramePointer = vstack.framePointer
    val operandValues = LongArray(operands.size) { index ->
        when (val operand = operands[index]) {
            is ControlSuperInstruction.CallOperand.Immediate -> operand.value
            is ControlSuperInstruction.CallOperand.Slot -> vstack.getFrameSlot(currentFramePointer, operand.slot)
        }
    }

    val frame = cstack.popFrame()
    val type = instance.functionType
    val params = type.params.types.size
    val results = type.results.types.size
    val interfaceSlots = maxOf(params, results)
    val depths = frame.depths

    cstack.shrinkHandlers(depths.handlers)
    cstack.shrinkInstructions(depths.instructions)
    // leave top label in place
    cstack.shrinkLabels(depths.labels + 1)
    vstack.shrink(0, depths.values)

    val calleeFramePointer = depths.values
    vstack.reserveDepth(calleeFramePointer + instance.function.frameSlots)
    operandValues.forEachIndexed { index, value ->
        vstack.setFrameSlot(calleeFramePointer, index, value)
    }
    instance.function.locals.forEachIndexed { index, value ->
        vstack.setFrameSlot(calleeFramePointer, interfaceSlots + index, value)
    }

    vstack.framePointer = calleeFramePointer
    vstack.reserveFrame(instance.function.frameSlots)
    cstack.push(
        frame.copy(
            instance = instance.module,
            frameSlotMode = instance.function.frameSlotMode,
            visibleResultBase = FrameSlotVisibleResultBase(frame),
        ),
    )
    cstack.push(instance.function.body.instructions)
}
