package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.runtime.function.WasmFunctionCallStrategy
import io.github.charlietap.chasm.runtime.instruction.OperandTransfer
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal fun WasmFunctionCall(
    vstack: ValueStack,
    strategy: WasmFunctionCallStrategy,
    operands: OperandTransfer,
    callFrameOffset: Int,
    activationHeader: Long,
): Int {
    val callerFp = vstack.fp
    val calleeFp = callerFp + callFrameOffset

    vstack.ensureCapacity(calleeFp + strategy.frameSlots)
    vstack.transferOperands(
        currentFp = callerFp,
        destinationFp = calleeFp,
        transfer = operands,
    )
    initializeLocals(vstack, strategy, calleeFp)
    vstack.writeActivationHeader(calleeFp, strategy.interfaceSlotCount, activationHeader)
    vstack.activateFrame(calleeFp, strategy.frameSlots)
    return strategy.entryIp
}
