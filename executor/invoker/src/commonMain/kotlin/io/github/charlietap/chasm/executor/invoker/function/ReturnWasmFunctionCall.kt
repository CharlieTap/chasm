package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.runtime.function.WasmFunctionCallStrategy
import io.github.charlietap.chasm.runtime.instruction.OperandTransfer
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal fun ReturnWasmFunctionCall(
    vstack: ValueStack,
    strategy: WasmFunctionCallStrategy,
    operands: OperandTransfer,
    callerActivationHeaderSlot: Int,
): Int {
    val fp = vstack.fp
    val activationHeader = vstack.getFrameSlot(fp, callerActivationHeaderSlot)
    vstack.ensureCapacity(fp + strategy.frameSlots)
    vstack.transferOperands(
        currentFp = fp,
        destinationFp = fp,
        transfer = operands,
    )
    initializeLocals(vstack, strategy, fp)
    vstack.setFrameSlot(fp, strategy.interfaceSlotCount, activationHeader)
    vstack.activateFrame(fp, strategy.frameSlots)
    return strategy.entryIp
}
