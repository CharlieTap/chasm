package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
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
): Int {
    val frame = cstack.popFrame()
    val type = instance.functionType
    val params = type.params.types.size
    val results = type.results.types.size
    val interfaceSlots = maxOf(params, results)

    cstack.shrinkHandlers(frame.handlerDepth)
    vstack.shrink(params, frame.valueDepth)
    vstack.framePointer = frame.valueDepth
    vstack.reserveFrame(instance.function.frameSlots)
    instance.function.locals.forEachIndexed { index, value ->
        vstack.setFrameSlot(interfaceSlots + index, value)
    }
    cstack.push(frame.copy(instance = instance.module))
    return instance.function.body.entryIp
}

internal fun ReturnWasmFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instance: FunctionInstance.WasmFunction,
    operands: List<ControlSuperInstruction.CallOperand>,
): Int {
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

    cstack.shrinkHandlers(frame.handlerDepth)
    vstack.shrink(0, frame.valueDepth)

    val calleeFramePointer = frame.valueDepth
    vstack.reserveDepth(calleeFramePointer + instance.function.frameSlots)
    operandValues.forEachIndexed { index, value ->
        vstack.setFrameSlot(calleeFramePointer, index, value)
    }
    instance.function.locals.forEachIndexed { index, value ->
        vstack.setFrameSlot(calleeFramePointer, interfaceSlots + index, value)
    }

    vstack.framePointer = calleeFramePointer
    vstack.reserveFrame(instance.function.frameSlots)
    cstack.push(frame.copy(instance = instance.module))
    return instance.function.body.entryIp
}
