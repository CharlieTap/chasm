@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.executor.invoker.function.CallSiteResultDestination
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.resultCallSiteIp
import io.github.charlietap.chasm.executor.invoker.function.returnToCaller
import io.github.charlietap.chasm.executor.invoker.function.withHostExceptionHandling
import io.github.charlietap.chasm.executor.invoker.instruction.control.ReturnExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.control.ThrowRefValueExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.control.strictIndirectCall
import io.github.charlietap.chasm.executor.invoker.instruction.control.strictIndirectReturnCall
import io.github.charlietap.chasm.executor.invoker.instruction.control.strictReferenceCall
import io.github.charlietap.chasm.executor.invoker.instruction.control.strictReferenceReturnCall
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.function.LocalInitialization
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.instruction.OperandTransfer
import io.github.charlietap.chasm.runtime.instruction.TailCallOperandTransfer
import io.github.charlietap.chasm.runtime.instruction.TransferSource
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.stack.activationHeader
import io.github.charlietap.chasm.runtime.type.RTT

private const val NO_RESULT_DESTINATION = -1
private const val RESULT_CALL_SITE_IP_LIMIT = 1 shl 30
private const val CALLER_FRAME_DELTA_LIMIT = 1 shl 25

fun WasmCallDispatcher(
    operands: OperandTransfer,
    callFrameOffset: Int,
    frameSlots: Int,
    entryIp: Int,
    activationHeaderSlot: Int,
    localInitialization: LocalInitialization,
    callSiteIp: Int,
    resultDestinationSlot: Int?,
): DispatchableInstruction {
    checkWasmCallSite(callSiteIp, callFrameOffset, resultDestinationSlot)
    val returnAddress = if (resultDestinationSlot == null) callSiteIp + 1 else resultCallSiteIp(callSiteIp + 1)
    val activationHeader = activationHeader(returnAddress, callFrameOffset)
    val frameEndOffset = callFrameOffset + frameSlots
    if (localInitialization !== LocalInitialization.None) {
        return linkedWasmCallDispatcher(
            operands = operands,
            callFrameOffset = callFrameOffset,
            frameEndOffset = frameEndOffset,
            entryIp = entryIp,
            activationHeaderSlot = activationHeaderSlot,
            activationHeader = activationHeader,
            localInitialization = localInitialization,
            resultDestinationSlot = resultDestinationSlot,
        )
    }
    val destination = resultDestinationSlot ?: NO_RESULT_DESTINATION
    val sources = operands.sources
    val operand = sources.singleOrNull()
    val firstOperand = sources.getOrNull(0)
    val secondOperand = sources.getOrNull(1)
    val thirdOperand = sources.getOrNull(2)
    val fourthOperand = sources.getOrNull(3)
    return when {
        operands.isInPlace -> LinkedWasmCallWithoutLocalsOrOperandTransfer(
            callFrameOffset = callFrameOffset,
            frameEndOffset = frameEndOffset,
            entryIp = entryIp,
            activationHeaderSlot = activationHeaderSlot,
            activationHeader = activationHeader,
            resultDestinationSlot = destination,
        )
        sources.size == 2 && firstOperand is TransferSource.Slot && secondOperand is TransferSource.Slot ->
            LinkedWasmCallWithoutLocalsWithTwoSlotOperands(
                callFrameOffset = callFrameOffset,
                frameEndOffset = frameEndOffset,
                entryIp = entryIp,
                firstSourceSlot = firstOperand.slot,
                secondSourceSlot = secondOperand.slot,
                activationHeaderSlot = activationHeaderSlot,
                activationHeader = activationHeader,
                resultDestinationSlot = destination,
            )
        sources.size == 3 &&
            firstOperand is TransferSource.Slot &&
            secondOperand is TransferSource.Slot &&
            thirdOperand is TransferSource.Slot ->
            LinkedWasmCallWithoutLocalsWithThreeSlotOperands(
                callFrameOffset = callFrameOffset,
                frameEndOffset = frameEndOffset,
                entryIp = entryIp,
                firstSourceSlot = firstOperand.slot,
                secondSourceSlot = secondOperand.slot,
                thirdSourceSlot = thirdOperand.slot,
                activationHeaderSlot = activationHeaderSlot,
                activationHeader = activationHeader,
                resultDestinationSlot = destination,
            )
        sources.size == 4 &&
            firstOperand is TransferSource.Slot &&
            secondOperand is TransferSource.Slot &&
            thirdOperand is TransferSource.Slot &&
            fourthOperand is TransferSource.Slot ->
            LinkedWasmCallWithoutLocalsWithFourSlotOperands(
                callFrameOffset = callFrameOffset,
                frameEndOffset = frameEndOffset,
                entryIp = entryIp,
                firstSourceSlot = firstOperand.slot,
                secondSourceSlot = secondOperand.slot,
                thirdSourceSlot = thirdOperand.slot,
                fourthSourceSlot = fourthOperand.slot,
                activationHeaderSlot = activationHeaderSlot,
                activationHeader = activationHeader,
                resultDestinationSlot = destination,
            )
        operand is TransferSource.Immediate -> LinkedWasmCallWithoutLocalsWithImmediateOperand(
            callFrameOffset = callFrameOffset,
            frameEndOffset = frameEndOffset,
            entryIp = entryIp,
            operand = operand.value,
            activationHeaderSlot = activationHeaderSlot,
            activationHeader = activationHeader,
            resultDestinationSlot = destination,
        )
        operand is TransferSource.Slot -> LinkedWasmCallWithoutLocalsWithSlotOperand(
            callFrameOffset = callFrameOffset,
            frameEndOffset = frameEndOffset,
            entryIp = entryIp,
            sourceSlot = operand.slot,
            activationHeaderSlot = activationHeaderSlot,
            activationHeader = activationHeader,
            resultDestinationSlot = destination,
        )
        else -> linkedWasmCallDispatcher(
            operands = operands,
            callFrameOffset = callFrameOffset,
            frameEndOffset = frameEndOffset,
            entryIp = entryIp,
            activationHeaderSlot = activationHeaderSlot,
            activationHeader = activationHeader,
            localInitialization = LocalInitialization.None,
            resultDestinationSlot = resultDestinationSlot,
        )
    }
}

private class LinkedWasmCallWithoutLocalsOrOperandTransfer(
    private val callFrameOffset: Int,
    private val frameEndOffset: Int,
    private val entryIp: Int,
    private val activationHeaderSlot: Int,
    private val activationHeader: Long,
    override val resultDestinationSlot: Int,
) : DispatchableInstruction(), CallSiteResultDestination {

    override fun invoke(
        vstack: ValueStack,
        context: ExecutionContext,
        nextIp: Int,
    ): Int {
        vstack.activateLinkedFrame(callFrameOffset, frameEndOffset, activationHeaderSlot, activationHeader)
        return entryIp
    }
}

private class LinkedWasmCallWithoutLocalsWithImmediateOperand(
    private val callFrameOffset: Int,
    private val frameEndOffset: Int,
    private val entryIp: Int,
    private val operand: Long,
    private val activationHeaderSlot: Int,
    private val activationHeader: Long,
    override val resultDestinationSlot: Int,
) : DispatchableInstruction(), CallSiteResultDestination {

    override fun invoke(
        vstack: ValueStack,
        context: ExecutionContext,
        nextIp: Int,
    ): Int {
        vstack.activateLinkedFrameWithImmediate(
            callFrameOffset,
            frameEndOffset,
            activationHeaderSlot,
            activationHeader,
            operand,
        )
        return entryIp
    }
}

private class LinkedWasmCallWithoutLocalsWithSlotOperand(
    private val callFrameOffset: Int,
    private val frameEndOffset: Int,
    private val entryIp: Int,
    private val sourceSlot: Int,
    private val activationHeaderSlot: Int,
    private val activationHeader: Long,
    override val resultDestinationSlot: Int,
) : DispatchableInstruction(), CallSiteResultDestination {

    override fun invoke(
        vstack: ValueStack,
        context: ExecutionContext,
        nextIp: Int,
    ): Int {
        vstack.activateLinkedFrameWithSlot(
            callFrameOffset,
            frameEndOffset,
            activationHeaderSlot,
            activationHeader,
            sourceSlot,
        )
        return entryIp
    }
}

private class LinkedWasmCallWithoutLocalsWithTwoSlotOperands(
    private val callFrameOffset: Int,
    private val frameEndOffset: Int,
    private val entryIp: Int,
    private val firstSourceSlot: Int,
    private val secondSourceSlot: Int,
    private val activationHeaderSlot: Int,
    private val activationHeader: Long,
    override val resultDestinationSlot: Int,
) : DispatchableInstruction(), CallSiteResultDestination {

    override fun invoke(
        vstack: ValueStack,
        context: ExecutionContext,
        nextIp: Int,
    ): Int {
        vstack.activateLinkedFrameWithTwoSlots(
            callFrameOffset,
            frameEndOffset,
            activationHeaderSlot,
            activationHeader,
            firstSourceSlot,
            secondSourceSlot,
        )
        return entryIp
    }
}

private class LinkedWasmCallWithoutLocalsWithThreeSlotOperands(
    private val callFrameOffset: Int,
    private val frameEndOffset: Int,
    private val entryIp: Int,
    private val firstSourceSlot: Int,
    private val secondSourceSlot: Int,
    private val thirdSourceSlot: Int,
    private val activationHeaderSlot: Int,
    private val activationHeader: Long,
    override val resultDestinationSlot: Int,
) : DispatchableInstruction(), CallSiteResultDestination {

    override fun invoke(
        vstack: ValueStack,
        context: ExecutionContext,
        nextIp: Int,
    ): Int {
        vstack.activateLinkedFrameWithThreeSlots(
            callFrameOffset,
            frameEndOffset,
            activationHeaderSlot,
            activationHeader,
            firstSourceSlot,
            secondSourceSlot,
            thirdSourceSlot,
        )
        return entryIp
    }
}

private class LinkedWasmCallWithoutLocalsWithFourSlotOperands(
    private val callFrameOffset: Int,
    private val frameEndOffset: Int,
    private val entryIp: Int,
    private val firstSourceSlot: Int,
    private val secondSourceSlot: Int,
    private val thirdSourceSlot: Int,
    private val fourthSourceSlot: Int,
    private val activationHeaderSlot: Int,
    private val activationHeader: Long,
    override val resultDestinationSlot: Int,
) : DispatchableInstruction(), CallSiteResultDestination {

    override fun invoke(
        vstack: ValueStack,
        context: ExecutionContext,
        nextIp: Int,
    ): Int {
        vstack.activateLinkedFrameWithFourSlots(
            callFrameOffset,
            frameEndOffset,
            activationHeaderSlot,
            activationHeader,
            firstSourceSlot,
            secondSourceSlot,
            thirdSourceSlot,
            fourthSourceSlot,
        )
        return entryIp
    }
}

private fun linkedWasmCallDispatcher(
    operands: OperandTransfer,
    callFrameOffset: Int,
    frameEndOffset: Int,
    entryIp: Int,
    activationHeaderSlot: Int,
    activationHeader: Long,
    localInitialization: LocalInitialization,
    resultDestinationSlot: Int?,
): DispatchableInstruction {
    val compiledResultDestinationSlot = resultDestinationSlot ?: NO_RESULT_DESTINATION
    val firstLocalSlot = activationHeaderSlot + 1
    return when (localInitialization) {
        LocalInitialization.None -> linkedWasmCallDispatcher(
            operands,
            callFrameOffset,
            frameEndOffset,
            entryIp,
            activationHeaderSlot,
            activationHeader,
            compiledResultDestinationSlot,
        ) { _, _ -> }
        LocalInitialization.Zero1 -> linkedWasmCallDispatcher(
            operands,
            callFrameOffset,
            frameEndOffset,
            entryIp,
            activationHeaderSlot,
            activationHeader,
            compiledResultDestinationSlot,
        ) { stack, fp -> stack.setFrameSlot(fp, firstLocalSlot, 0L) }
        LocalInitialization.Zero2 -> linkedWasmCallDispatcher(
            operands,
            callFrameOffset,
            frameEndOffset,
            entryIp,
            activationHeaderSlot,
            activationHeader,
            compiledResultDestinationSlot,
        ) { stack, fp ->
            stack.setFrameSlot(fp, firstLocalSlot, 0L)
            stack.setFrameSlot(fp, firstLocalSlot + 1, 0L)
        }
        LocalInitialization.Zero3 -> linkedWasmCallDispatcher(
            operands,
            callFrameOffset,
            frameEndOffset,
            entryIp,
            activationHeaderSlot,
            activationHeader,
            compiledResultDestinationSlot,
        ) { stack, fp ->
            stack.setFrameSlot(fp, firstLocalSlot, 0L)
            stack.setFrameSlot(fp, firstLocalSlot + 1, 0L)
            stack.setFrameSlot(fp, firstLocalSlot + 2, 0L)
        }
        LocalInitialization.Zero4 -> linkedWasmCallDispatcher(
            operands,
            callFrameOffset,
            frameEndOffset,
            entryIp,
            activationHeaderSlot,
            activationHeader,
            compiledResultDestinationSlot,
        ) { stack, fp ->
            stack.setFrameSlot(fp, firstLocalSlot, 0L)
            stack.setFrameSlot(fp, firstLocalSlot + 1, 0L)
            stack.setFrameSlot(fp, firstLocalSlot + 2, 0L)
            stack.setFrameSlot(fp, firstLocalSlot + 3, 0L)
        }
        is LocalInitialization.ZeroRange -> {
            val count = localInitialization.count
            linkedWasmCallDispatcher(
                operands,
                callFrameOffset,
                frameEndOffset,
                entryIp,
                activationHeaderSlot,
                activationHeader,
                compiledResultDestinationSlot,
            ) { stack, fp -> stack.fillFrameSlots(fp, firstLocalSlot, count, 0L) }
        }
        is LocalInitialization.ConstantStores -> {
            val values = localInitialization.values
            linkedWasmCallDispatcher(
                operands,
                callFrameOffset,
                frameEndOffset,
                entryIp,
                activationHeaderSlot,
                activationHeader,
                compiledResultDestinationSlot,
            ) { stack, fp -> stack.copyValuesToFrame(values, fp, firstLocalSlot) }
        }
    }
}

private inline fun linkedWasmCallDispatcher(
    operands: OperandTransfer,
    callFrameOffset: Int,
    frameEndOffset: Int,
    entryIp: Int,
    activationHeaderSlot: Int,
    activationHeader: Long,
    resultDestinationSlot: Int,
    crossinline initializeLocals: (ValueStack, Int) -> Unit,
): DispatchableInstruction {
    val sources = operands.sources
    val operand = sources.singleOrNull()
    val first = sources.getOrNull(0)
    val second = sources.getOrNull(1)
    val third = sources.getOrNull(2)
    val fourth = sources.getOrNull(3)
    return when {
        operands.isInPlace -> linkedWasmCallDispatcher(resultDestinationSlot) { vstack ->
            linkedWasmFunctionCall(
                vstack,
                callFrameOffset,
                frameEndOffset,
                entryIp,
                activationHeaderSlot,
                activationHeader,
                initializeLocals,
            ) { _, _ -> }
        }
        sources.size == 2 && first is TransferSource.Slot && second is TransferSource.Slot -> {
            val firstSlot = first.slot
            val secondSlot = second.slot
            linkedWasmCallDispatcher(resultDestinationSlot) { vstack ->
                linkedWasmFunctionCall(
                    vstack,
                    callFrameOffset,
                    frameEndOffset,
                    entryIp,
                    activationHeaderSlot,
                    activationHeader,
                    initializeLocals,
                ) { callerFp, calleeFp ->
                    val firstValue = vstack.getFrameSlot(callerFp, firstSlot)
                    val secondValue = vstack.getFrameSlot(callerFp, secondSlot)
                    vstack.setFrameSlot(calleeFp, 0, firstValue)
                    vstack.setFrameSlot(calleeFp, 1, secondValue)
                }
            }
        }
        sources.size == 3 &&
            first is TransferSource.Slot &&
            second is TransferSource.Slot &&
            third is TransferSource.Slot -> {
            val firstSlot = first.slot
            val secondSlot = second.slot
            val thirdSlot = third.slot
            linkedWasmCallDispatcher(resultDestinationSlot) { vstack ->
                linkedWasmFunctionCall(
                    vstack,
                    callFrameOffset,
                    frameEndOffset,
                    entryIp,
                    activationHeaderSlot,
                    activationHeader,
                    initializeLocals,
                ) { callerFp, calleeFp ->
                    val firstValue = vstack.getFrameSlot(callerFp, firstSlot)
                    val secondValue = vstack.getFrameSlot(callerFp, secondSlot)
                    val thirdValue = vstack.getFrameSlot(callerFp, thirdSlot)
                    vstack.setFrameSlot(calleeFp, 0, firstValue)
                    vstack.setFrameSlot(calleeFp, 1, secondValue)
                    vstack.setFrameSlot(calleeFp, 2, thirdValue)
                }
            }
        }
        sources.size == 4 &&
            first is TransferSource.Slot &&
            second is TransferSource.Slot &&
            third is TransferSource.Slot &&
            fourth is TransferSource.Slot -> {
            val firstSlot = first.slot
            val secondSlot = second.slot
            val thirdSlot = third.slot
            val fourthSlot = fourth.slot
            linkedWasmCallDispatcher(resultDestinationSlot) { vstack ->
                linkedWasmFunctionCall(
                    vstack,
                    callFrameOffset,
                    frameEndOffset,
                    entryIp,
                    activationHeaderSlot,
                    activationHeader,
                    initializeLocals,
                ) { callerFp, calleeFp ->
                    val firstValue = vstack.getFrameSlot(callerFp, firstSlot)
                    val secondValue = vstack.getFrameSlot(callerFp, secondSlot)
                    val thirdValue = vstack.getFrameSlot(callerFp, thirdSlot)
                    val fourthValue = vstack.getFrameSlot(callerFp, fourthSlot)
                    vstack.setFrameSlot(calleeFp, 0, firstValue)
                    vstack.setFrameSlot(calleeFp, 1, secondValue)
                    vstack.setFrameSlot(calleeFp, 2, thirdValue)
                    vstack.setFrameSlot(calleeFp, 3, fourthValue)
                }
            }
        }
        operand is TransferSource.Immediate -> {
            val value = operand.value
            linkedWasmCallDispatcher(resultDestinationSlot) { vstack ->
                linkedWasmFunctionCall(
                    vstack,
                    callFrameOffset,
                    frameEndOffset,
                    entryIp,
                    activationHeaderSlot,
                    activationHeader,
                    initializeLocals,
                ) { _, calleeFp -> vstack.setFrameSlot(calleeFp, 0, value) }
            }
        }
        operand is TransferSource.Slot -> {
            val slot = operand.slot
            linkedWasmCallDispatcher(resultDestinationSlot) { vstack ->
                linkedWasmFunctionCall(
                    vstack,
                    callFrameOffset,
                    frameEndOffset,
                    entryIp,
                    activationHeaderSlot,
                    activationHeader,
                    initializeLocals,
                ) { callerFp, calleeFp ->
                    vstack.setFrameSlot(calleeFp, 0, vstack.getFrameSlot(callerFp, slot))
                }
            }
        }
        else -> linkedWasmCallDispatcher(resultDestinationSlot) { vstack ->
            linkedWasmFunctionCall(
                vstack,
                callFrameOffset,
                frameEndOffset,
                entryIp,
                activationHeaderSlot,
                activationHeader,
                initializeLocals,
            ) { callerFp, calleeFp ->
                vstack.transferOperands(callerFp, calleeFp, operands)
            }
        }
    }
}

private inline fun linkedWasmCallDispatcher(
    destinationSlot: Int,
    crossinline call: (ValueStack) -> Int,
): DispatchableInstruction = object : DispatchableInstruction(), CallSiteResultDestination {
    override val resultDestinationSlot = destinationSlot

    override fun invoke(
        vstack: ValueStack,
        context: ExecutionContext,
        nextIp: Int,
    ): Int = call(vstack)
}

private inline fun linkedWasmFunctionCall(
    vstack: ValueStack,
    callFrameOffset: Int,
    frameEndOffset: Int,
    entryIp: Int,
    activationHeaderSlot: Int,
    activationHeader: Long,
    initializeLocals: (ValueStack, Int) -> Unit,
    transfer: (callerFp: Int, calleeFp: Int) -> Unit,
): Int {
    val callerFp = vstack.fp
    val calleeFp = callerFp + callFrameOffset
    val frameEnd = callerFp + frameEndOffset
    vstack.ensureCapacity(frameEnd)
    transfer(callerFp, calleeFp)
    initializeLocals(vstack, calleeFp)
    vstack.writeActivationHeader(calleeFp, activationHeaderSlot, activationHeader)
    vstack.activateFrameAtDepth(calleeFp, frameEnd)
    return entryIp
}

fun HostCallDispatcher(
    function: FunctionInstance.HostFunction,
    caller: ModuleInstance,
    operands: OperandTransfer,
    callFrameOffset: Int,
    resultDestinationSlot: Int?,
): DispatchableInstruction {
    val resultSlotBase = resultDestinationSlot ?: callFrameOffset
    val operand = operands.sources.singleOrNull()

    return when {
        operands.isInPlace -> DispatchableInstruction { vstack, context, nextIp ->
            withHostExceptionHandling(vstack, context, nextIp) {
                HostFunctionCall(vstack, context, caller, function, callFrameOffset, resultSlotBase)
                nextIp
            }
        }
        operand is TransferSource.Immediate -> {
            val value = operand.value
            DispatchableInstruction { vstack, context, nextIp ->
                vstack.setFrameSlot(callFrameOffset, value)
                withHostExceptionHandling(vstack, context, nextIp) {
                    HostFunctionCall(vstack, context, caller, function, callFrameOffset, resultSlotBase)
                    nextIp
                }
            }
        }
        operand is TransferSource.Slot -> {
            val sourceSlot = operand.slot
            DispatchableInstruction { vstack, context, nextIp ->
                vstack.setFrameSlot(callFrameOffset, vstack.getFrameSlot(sourceSlot))
                withHostExceptionHandling(vstack, context, nextIp) {
                    HostFunctionCall(vstack, context, caller, function, callFrameOffset, resultSlotBase)
                    nextIp
                }
            }
        }
        else -> DispatchableInstruction { vstack, context, nextIp ->
            val fp = vstack.fp
            vstack.transferOperands(
                currentFp = fp,
                destinationFp = fp + callFrameOffset,
                transfer = operands,
            )
            withHostExceptionHandling(vstack, context, nextIp) {
                HostFunctionCall(vstack, context, caller, function, callFrameOffset, resultSlotBase)
                nextIp
            }
        }
    }
}

fun CallIndirectIDispatcher(
    elementIndex: Int,
    operands: OperandTransfer,
    type: RTT,
    table: TableInstance,
    caller: ModuleInstance,
    callFrameOffset: Int,
    callSiteIp: Int,
    resultDestinationSlot: Int?,
): DispatchableInstruction {
    checkWasmCallSite(callSiteIp, callFrameOffset, resultDestinationSlot)
    val returnIp = callSiteIp + 1
    val header = callActivationHeader(returnIp, callFrameOffset, resultDestinationSlot)
    return if (resultDestinationSlot == null) {
        DispatchableInstruction { vstack, context, _ ->
            strictIndirectCall(vstack, context, elementIndex, operands, type, table, caller, callFrameOffset, returnIp, header, null)
        }
    } else {
        ResultCallIndirectIDispatcher(elementIndex, operands, type, table, caller, callFrameOffset, returnIp, header, resultDestinationSlot)
    }
}

fun CallIndirectSDispatcher(
    elementIndexSlot: Int,
    operands: OperandTransfer,
    type: RTT,
    table: TableInstance,
    caller: ModuleInstance,
    callFrameOffset: Int,
    callSiteIp: Int,
    resultDestinationSlot: Int?,
): DispatchableInstruction {
    checkWasmCallSite(callSiteIp, callFrameOffset, resultDestinationSlot)
    val returnIp = callSiteIp + 1
    val header = callActivationHeader(returnIp, callFrameOffset, resultDestinationSlot)
    return if (resultDestinationSlot == null) {
        DispatchableInstruction { vstack, context, _ ->
            val elementIndex = vstack.getFrameSlot(elementIndexSlot).toInt()
            strictIndirectCall(vstack, context, elementIndex, operands, type, table, caller, callFrameOffset, returnIp, header, null)
        }
    } else {
        ResultCallIndirectSDispatcher(elementIndexSlot, operands, type, table, caller, callFrameOffset, returnIp, header, resultDestinationSlot)
    }
}

fun CallRefSDispatcher(
    functionSlot: Int,
    operands: OperandTransfer,
    caller: ModuleInstance,
    callFrameOffset: Int,
    callSiteIp: Int,
    resultDestinationSlot: Int?,
): DispatchableInstruction {
    checkWasmCallSite(callSiteIp, callFrameOffset, resultDestinationSlot)
    val returnIp = callSiteIp + 1
    val header = callActivationHeader(returnIp, callFrameOffset, resultDestinationSlot)
    return if (resultDestinationSlot == null) {
        DispatchableInstruction { vstack, context, _ ->
            strictReferenceCall(vstack, context, functionSlot, operands, caller, callFrameOffset, returnIp, header, null)
        }
    } else {
        ResultCallRefDispatcher(functionSlot, operands, caller, callFrameOffset, returnIp, header, resultDestinationSlot)
    }
}

private fun callActivationHeader(
    returnIp: Int,
    callFrameOffset: Int,
    resultDestinationSlot: Int?,
): Long = activationHeader(
    if (resultDestinationSlot == null) returnIp else resultCallSiteIp(returnIp),
    callFrameOffset,
)

private fun checkWasmCallSite(
    callSiteIp: Int,
    callFrameOffset: Int,
    resultDestinationSlot: Int?,
) {
    check(callFrameOffset in 0 until CALLER_FRAME_DELTA_LIMIT) {
        "Wasm caller-frame displacement exceeds the activation-header representation"
    }
    val limit = if (resultDestinationSlot == null) Int.MAX_VALUE else RESULT_CALL_SITE_IP_LIMIT
    check(callSiteIp in 0 until limit) {
        "Wasm call-site IP exceeds the activation-header representation"
    }
}

private class ResultCallIndirectIDispatcher(
    private val elementIndex: Int,
    private val operands: OperandTransfer,
    private val type: RTT,
    private val table: TableInstance,
    private val caller: ModuleInstance,
    private val callFrameOffset: Int,
    private val returnIp: Int,
    private val activationHeader: Long,
    override val resultDestinationSlot: Int,
) : DispatchableInstruction(), CallSiteResultDestination {

    override fun invoke(
        vstack: ValueStack,
        context: ExecutionContext,
        nextIp: Int,
    ): Int = strictIndirectCall(
        vstack,
        context,
        elementIndex,
        operands,
        type,
        table,
        caller,
        callFrameOffset,
        returnIp,
        activationHeader,
        resultDestinationSlot,
    )
}

private class ResultCallIndirectSDispatcher(
    private val elementIndexSlot: Int,
    private val operands: OperandTransfer,
    private val type: RTT,
    private val table: TableInstance,
    private val caller: ModuleInstance,
    private val callFrameOffset: Int,
    private val returnIp: Int,
    private val activationHeader: Long,
    override val resultDestinationSlot: Int,
) : DispatchableInstruction(), CallSiteResultDestination {

    override fun invoke(
        vstack: ValueStack,
        context: ExecutionContext,
        nextIp: Int,
    ): Int = strictIndirectCall(
        vstack,
        context,
        vstack.getFrameSlot(elementIndexSlot).toInt(),
        operands,
        type,
        table,
        caller,
        callFrameOffset,
        returnIp,
        activationHeader,
        resultDestinationSlot,
    )
}

private class ResultCallRefDispatcher(
    private val functionSlot: Int,
    private val operands: OperandTransfer,
    private val caller: ModuleInstance,
    private val callFrameOffset: Int,
    private val returnIp: Int,
    private val activationHeader: Long,
    override val resultDestinationSlot: Int,
) : DispatchableInstruction(), CallSiteResultDestination {

    override fun invoke(
        vstack: ValueStack,
        context: ExecutionContext,
        nextIp: Int,
    ): Int = strictReferenceCall(
        vstack,
        context,
        functionSlot,
        operands,
        caller,
        callFrameOffset,
        returnIp,
        activationHeader,
        resultDestinationSlot,
    )
}

fun ReturnWasmCallDispatcher(
    frameSlots: Int,
    entryIp: Int,
    activationHeaderSlot: Int,
    callerActivationHeaderSlot: Int,
    operands: OperandTransfer,
    localInitialization: LocalInitialization,
): DispatchableInstruction {
    val firstLocalSlot = activationHeaderSlot + 1
    return when (localInitialization) {
        LocalInitialization.None -> linkedReturnWasmCallDispatcher(
            frameSlots,
            entryIp,
            activationHeaderSlot,
            callerActivationHeaderSlot,
            operands,
        ) { _, _ -> }
        LocalInitialization.Zero1 -> linkedReturnWasmCallDispatcher(
            frameSlots,
            entryIp,
            activationHeaderSlot,
            callerActivationHeaderSlot,
            operands,
        ) { stack, fp -> stack.setFrameSlot(fp, firstLocalSlot, 0L) }
        LocalInitialization.Zero2 -> linkedReturnWasmCallDispatcher(
            frameSlots,
            entryIp,
            activationHeaderSlot,
            callerActivationHeaderSlot,
            operands,
        ) { stack, fp ->
            stack.setFrameSlot(fp, firstLocalSlot, 0L)
            stack.setFrameSlot(fp, firstLocalSlot + 1, 0L)
        }
        LocalInitialization.Zero3 -> linkedReturnWasmCallDispatcher(
            frameSlots,
            entryIp,
            activationHeaderSlot,
            callerActivationHeaderSlot,
            operands,
        ) { stack, fp ->
            stack.setFrameSlot(fp, firstLocalSlot, 0L)
            stack.setFrameSlot(fp, firstLocalSlot + 1, 0L)
            stack.setFrameSlot(fp, firstLocalSlot + 2, 0L)
        }
        LocalInitialization.Zero4 -> linkedReturnWasmCallDispatcher(
            frameSlots,
            entryIp,
            activationHeaderSlot,
            callerActivationHeaderSlot,
            operands,
        ) { stack, fp ->
            stack.setFrameSlot(fp, firstLocalSlot, 0L)
            stack.setFrameSlot(fp, firstLocalSlot + 1, 0L)
            stack.setFrameSlot(fp, firstLocalSlot + 2, 0L)
            stack.setFrameSlot(fp, firstLocalSlot + 3, 0L)
        }
        is LocalInitialization.ZeroRange -> {
            val count = localInitialization.count
            linkedReturnWasmCallDispatcher(
                frameSlots,
                entryIp,
                activationHeaderSlot,
                callerActivationHeaderSlot,
                operands,
            ) { stack, fp -> stack.fillFrameSlots(fp, firstLocalSlot, count, 0L) }
        }
        is LocalInitialization.ConstantStores -> {
            val values = localInitialization.values
            linkedReturnWasmCallDispatcher(
                frameSlots,
                entryIp,
                activationHeaderSlot,
                callerActivationHeaderSlot,
                operands,
            ) { stack, fp -> stack.copyValuesToFrame(values, fp, firstLocalSlot) }
        }
    }
}

private inline fun linkedReturnWasmCallDispatcher(
    frameSlots: Int,
    entryIp: Int,
    activationHeaderSlot: Int,
    callerActivationHeaderSlot: Int,
    operands: OperandTransfer,
    crossinline initializeLocals: (ValueStack, Int) -> Unit,
): DispatchableInstruction {
    val sources = operands.sources
    val operand = sources.singleOrNull()
    val first = sources.getOrNull(0)
    val second = sources.getOrNull(1)
    val third = sources.getOrNull(2)
    val fourth = sources.getOrNull(3)
    return when {
        operands.isInPlace -> DispatchableInstruction { vstack, _, _ ->
            linkedReturnWasmFunctionCall(
                vstack,
                frameSlots,
                entryIp,
                activationHeaderSlot,
                callerActivationHeaderSlot,
                initializeLocals,
            ) { _ -> }
        }
        sources.size == 2 && first is TransferSource.Slot && second is TransferSource.Slot -> {
            val firstSlot = first.slot
            val secondSlot = second.slot
            DispatchableInstruction { vstack, _, _ ->
                linkedReturnWasmFunctionCall(
                    vstack,
                    frameSlots,
                    entryIp,
                    activationHeaderSlot,
                    callerActivationHeaderSlot,
                    initializeLocals,
                ) { fp ->
                    val firstValue = vstack.getFrameSlot(fp, firstSlot)
                    val secondValue = vstack.getFrameSlot(fp, secondSlot)
                    vstack.setFrameSlot(fp, 0, firstValue)
                    vstack.setFrameSlot(fp, 1, secondValue)
                }
            }
        }
        sources.size == 3 &&
            first is TransferSource.Slot &&
            second is TransferSource.Slot &&
            third is TransferSource.Slot -> {
            val firstSlot = first.slot
            val secondSlot = second.slot
            val thirdSlot = third.slot
            DispatchableInstruction { vstack, _, _ ->
                linkedReturnWasmFunctionCall(
                    vstack,
                    frameSlots,
                    entryIp,
                    activationHeaderSlot,
                    callerActivationHeaderSlot,
                    initializeLocals,
                ) { fp ->
                    val firstValue = vstack.getFrameSlot(fp, firstSlot)
                    val secondValue = vstack.getFrameSlot(fp, secondSlot)
                    val thirdValue = vstack.getFrameSlot(fp, thirdSlot)
                    vstack.setFrameSlot(fp, 0, firstValue)
                    vstack.setFrameSlot(fp, 1, secondValue)
                    vstack.setFrameSlot(fp, 2, thirdValue)
                }
            }
        }
        sources.size == 4 &&
            first is TransferSource.Slot &&
            second is TransferSource.Slot &&
            third is TransferSource.Slot &&
            fourth is TransferSource.Slot -> {
            val firstSlot = first.slot
            val secondSlot = second.slot
            val thirdSlot = third.slot
            val fourthSlot = fourth.slot
            DispatchableInstruction { vstack, _, _ ->
                linkedReturnWasmFunctionCall(
                    vstack,
                    frameSlots,
                    entryIp,
                    activationHeaderSlot,
                    callerActivationHeaderSlot,
                    initializeLocals,
                ) { fp ->
                    val firstValue = vstack.getFrameSlot(fp, firstSlot)
                    val secondValue = vstack.getFrameSlot(fp, secondSlot)
                    val thirdValue = vstack.getFrameSlot(fp, thirdSlot)
                    val fourthValue = vstack.getFrameSlot(fp, fourthSlot)
                    vstack.setFrameSlot(fp, 0, firstValue)
                    vstack.setFrameSlot(fp, 1, secondValue)
                    vstack.setFrameSlot(fp, 2, thirdValue)
                    vstack.setFrameSlot(fp, 3, fourthValue)
                }
            }
        }
        operand is TransferSource.Immediate -> {
            val value = operand.value
            DispatchableInstruction { vstack, _, _ ->
                linkedReturnWasmFunctionCall(
                    vstack,
                    frameSlots,
                    entryIp,
                    activationHeaderSlot,
                    callerActivationHeaderSlot,
                    initializeLocals,
                ) { fp -> vstack.setFrameSlot(fp, 0, value) }
            }
        }
        operand is TransferSource.Slot -> {
            val slot = operand.slot
            DispatchableInstruction { vstack, _, _ ->
                linkedReturnWasmFunctionCall(
                    vstack,
                    frameSlots,
                    entryIp,
                    activationHeaderSlot,
                    callerActivationHeaderSlot,
                    initializeLocals,
                ) { fp -> vstack.setFrameSlot(fp, 0, vstack.getFrameSlot(fp, slot)) }
            }
        }
        else -> DispatchableInstruction { vstack, _, _ ->
            linkedReturnWasmFunctionCall(
                vstack,
                frameSlots,
                entryIp,
                activationHeaderSlot,
                callerActivationHeaderSlot,
                initializeLocals,
            ) { fp -> vstack.transferOperands(fp, fp, operands) }
        }
    }
}

private inline fun linkedReturnWasmFunctionCall(
    vstack: ValueStack,
    frameSlots: Int,
    entryIp: Int,
    activationHeaderSlot: Int,
    callerActivationHeaderSlot: Int,
    initializeLocals: (ValueStack, Int) -> Unit,
    transfer: (fp: Int) -> Unit,
): Int {
    val fp = vstack.fp
    val activationHeader = vstack.getFrameSlot(fp, callerActivationHeaderSlot)
    vstack.ensureCapacity(fp + frameSlots)
    transfer(fp)
    initializeLocals(vstack, fp)
    vstack.setFrameSlot(fp, activationHeaderSlot, activationHeader)
    vstack.activateFrame(fp, frameSlots)
    return entryIp
}

fun FunctionReturnDispatcher(
    results: OperandTransfer,
    activationHeaderSlot: Int,
): DispatchableInstruction {
    val resultCount = results.sources.size
    if (results.isInPlace) {
        return when (resultCount) {
            0 -> DispatchableInstruction { vstack, _, _ ->
                vstack.restoreCallerFrame(0, activationHeaderSlot)
            }
            1 -> DispatchableInstruction { vstack, context, _ ->
                returnToCaller(vstack, context.store, 1, activationHeaderSlot)
            }
            else -> DispatchableInstruction { vstack, _, _ ->
                vstack.restoreCallerFrame(resultCount, activationHeaderSlot)
            }
        }
    }
    val result = results.sources.singleOrNull()
    return when (result) {
        is TransferSource.Immediate -> {
            val value = result.value
            DispatchableInstruction { vstack, context, _ ->
                vstack.setFrameSlot(0, value)
                returnToCaller(vstack, context.store, 1, activationHeaderSlot)
            }
        }
        is TransferSource.Slot -> {
            val sourceSlot = result.slot
            DispatchableInstruction { vstack, context, _ ->
                vstack.setFrameSlot(0, vstack.getFrameSlot(sourceSlot))
                returnToCaller(vstack, context.store, 1, activationHeaderSlot)
            }
        }
        null -> DispatchableInstruction { vstack, context, _ ->
            val fp = vstack.fp
            vstack.transferOperands(
                currentFp = fp,
                destinationFp = fp,
                transfer = results,
            )
            ReturnExecutor(vstack, context, resultCount, activationHeaderSlot)
        }
    }
}

fun ReturnHostCallDispatcher(
    function: FunctionInstance.HostFunction,
    caller: ModuleInstance,
    operands: OperandTransfer,
    callFrameOffset: Int,
    activationHeaderSlot: Int,
): DispatchableInstruction {
    val operand = operands.sources.singleOrNull()

    return when {
        operands.isInPlace -> DispatchableInstruction { vstack, context, nextIp ->
            withHostExceptionHandling(vstack, context, nextIp) {
                HostFunctionCall(vstack, context, caller, function, callFrameOffset, 0)
                ReturnExecutor(vstack, context, function.functionType.results.types.size, activationHeaderSlot)
            }
        }
        operand is TransferSource.Immediate -> {
            val value = operand.value
            DispatchableInstruction { vstack, context, nextIp ->
                vstack.setFrameSlot(callFrameOffset, value)
                withHostExceptionHandling(vstack, context, nextIp) {
                    HostFunctionCall(vstack, context, caller, function, callFrameOffset, 0)
                    ReturnExecutor(vstack, context, function.functionType.results.types.size, activationHeaderSlot)
                }
            }
        }
        operand is TransferSource.Slot -> {
            val sourceSlot = operand.slot
            DispatchableInstruction { vstack, context, nextIp ->
                vstack.setFrameSlot(callFrameOffset, vstack.getFrameSlot(sourceSlot))
                withHostExceptionHandling(vstack, context, nextIp) {
                    HostFunctionCall(vstack, context, caller, function, callFrameOffset, 0)
                    ReturnExecutor(vstack, context, function.functionType.results.types.size, activationHeaderSlot)
                }
            }
        }
        else -> DispatchableInstruction { vstack, context, nextIp ->
            val fp = vstack.fp
            vstack.transferOperands(
                currentFp = fp,
                destinationFp = fp + callFrameOffset,
                transfer = operands,
            )
            withHostExceptionHandling(vstack, context, nextIp) {
                HostFunctionCall(vstack, context, caller, function, callFrameOffset, 0)
                ReturnExecutor(vstack, context, function.functionType.results.types.size, activationHeaderSlot)
            }
        }
    }
}

fun ReturnCallIndirectIDispatcher(
    elementIndex: Int,
    operands: TailCallOperandTransfer,
    type: RTT,
    table: TableInstance,
    caller: ModuleInstance,
    callFrameOffset: Int,
    callerActivationHeaderSlot: Int,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    strictIndirectReturnCall(
        vstack,
        context,
        elementIndex,
        operands,
        type,
        table,
        caller,
        callFrameOffset,
        callerActivationHeaderSlot,
        nextIp,
    )
}

fun ReturnCallIndirectSDispatcher(
    elementIndexSlot: Int,
    operands: TailCallOperandTransfer,
    type: RTT,
    table: TableInstance,
    caller: ModuleInstance,
    callFrameOffset: Int,
    callerActivationHeaderSlot: Int,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    strictIndirectReturnCall(
        vstack,
        context,
        vstack.getFrameSlot(elementIndexSlot).toInt(),
        operands,
        type,
        table,
        caller,
        callFrameOffset,
        callerActivationHeaderSlot,
        nextIp,
    )
}

fun ReturnCallRefSDispatcher(
    functionSlot: Int,
    operands: TailCallOperandTransfer,
    caller: ModuleInstance,
    callFrameOffset: Int,
    callerActivationHeaderSlot: Int,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    strictReferenceReturnCall(
        vstack,
        context,
        functionSlot,
        operands,
        caller,
        callFrameOffset,
        callerActivationHeaderSlot,
        nextIp,
    )
}

fun ThrowDispatcher(
    tagAddress: Address.Tag,
    firstPayloadSlot: Int,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    ThrowRefValueExecutor(
        vstack = vstack,
        context = context,
        ref = context.heap.allocateExceptionFromFrame(context, tagAddress, firstPayloadSlot),
        faultIp = nextIp - 1,
    )
}

fun ThrowRefSDispatcher(
    exceptionSlot: Int,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    ThrowRefValueExecutor(
        vstack = vstack,
        context = context,
        ref = vstack.getFrameSlot(exceptionSlot),
        faultIp = nextIp - 1,
    )
}
