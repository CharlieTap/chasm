package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.function.CallSiteResultDestination
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.resultCallSiteIp
import io.github.charlietap.chasm.executor.invoker.function.returnToCaller
import io.github.charlietap.chasm.executor.invoker.function.withHostExceptionHandling
import io.github.charlietap.chasm.executor.invoker.instruction.control.CallExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.control.ReturnCallExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.control.ReturnExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.control.ThrowExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.control.ThrowRefExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.function.LocalInitialization
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction
import io.github.charlietap.chasm.runtime.instruction.OperandTransfer
import io.github.charlietap.chasm.runtime.instruction.TransferSource
import io.github.charlietap.chasm.runtime.program.Program
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.stack.activationHeader

private const val NO_RESULT_DESTINATION = -1
private const val RESULT_CALL_SITE_IP_LIMIT = 1 shl 30
private const val CALLER_FRAME_DELTA_LIMIT = 1 shl 25

fun CallDispatcher(
    instruction: ControlInstruction.WasmCall,
    resultDestinationSlot: Int? = null,
): DispatchableInstruction = UnlinkedWasmCallByStrategy(
    instruction,
    instruction.callFrameOffset,
    resultDestinationSlot,
)

private fun wasmCallWithoutLocalsDispatcher(
    instruction: ControlInstruction.WasmCall,
    operands: OperandTransfer,
    callFrameOffset: Int,
    resultDestinationSlot: Int?,
): UnlinkedWasmCall {
    val sources = operands.sources
    val operand = sources.singleOrNull()
    val firstOperand = sources.getOrNull(0)
    val secondOperand = sources.getOrNull(1)
    val thirdOperand = sources.getOrNull(2)
    val fourthOperand = sources.getOrNull(3)
    return when {
        operands.isInPlace -> {
            UnlinkedWasmCallWithoutLocalsOrOperandTransfer(instruction, callFrameOffset, resultDestinationSlot)
        }
        sources.size == 2 && firstOperand is TransferSource.Slot && secondOperand is TransferSource.Slot -> {
            UnlinkedWasmCallWithoutLocalsWithTwoSlotOperands(
                instruction = instruction,
                callFrameOffset = callFrameOffset,
                firstSourceSlot = firstOperand.slot,
                secondSourceSlot = secondOperand.slot,
                resultDestinationSlot = resultDestinationSlot,
            )
        }
        sources.size == 3 &&
            firstOperand is TransferSource.Slot &&
            secondOperand is TransferSource.Slot &&
            thirdOperand is TransferSource.Slot -> {
            UnlinkedWasmCallWithoutLocalsWithThreeSlotOperands(
                instruction = instruction,
                callFrameOffset = callFrameOffset,
                firstSourceSlot = firstOperand.slot,
                secondSourceSlot = secondOperand.slot,
                thirdSourceSlot = thirdOperand.slot,
                resultDestinationSlot = resultDestinationSlot,
            )
        }
        sources.size == 4 &&
            firstOperand is TransferSource.Slot &&
            secondOperand is TransferSource.Slot &&
            thirdOperand is TransferSource.Slot &&
            fourthOperand is TransferSource.Slot -> {
            UnlinkedWasmCallWithoutLocalsWithFourSlotOperands(
                instruction = instruction,
                callFrameOffset = callFrameOffset,
                firstSourceSlot = firstOperand.slot,
                secondSourceSlot = secondOperand.slot,
                thirdSourceSlot = thirdOperand.slot,
                fourthSourceSlot = fourthOperand.slot,
                resultDestinationSlot = resultDestinationSlot,
            )
        }
        operand is TransferSource.Immediate -> {
            val value = operand.value
            UnlinkedWasmCallWithoutLocalsWithImmediateOperand(
                instruction,
                callFrameOffset,
                value,
                resultDestinationSlot,
            )
        }
        operand is TransferSource.Slot -> {
            val slot = operand.slot
            UnlinkedWasmCallWithoutLocalsWithSlotOperand(
                instruction,
                callFrameOffset,
                slot,
                resultDestinationSlot,
            )
        }
        else -> UnlinkedWasmCallWithOperands(instruction, callFrameOffset, resultDestinationSlot)
    }
}

/** Resolves Wasm call metadata after final program addresses and function strategies are known. */
fun LinkWasmCallDispatchers(
    program: Program,
    firstIp: Int,
    onLinked: ((DispatchableInstruction, LinkedInstruction) -> Unit)? = null,
): Int {
    require(firstIp in 0..program.size) {
        "first linkable Wasm call instruction index is out of bounds"
    }
    var linkedCount = 0
    for (ip in firstIp until program.size) {
        val instruction = program.instructions[ip]
        if (instruction is LinkableWasmCall) {
            val linked = instruction.link(ip)
            onLinked?.invoke(linked, instruction.source)
            program.replace(ip, linked)
            linkedCount++
        }
    }
    return linkedCount
}

private interface LinkableWasmCall {
    val source: LinkedInstruction

    fun link(callSiteIp: Int): DispatchableInstruction
}

private abstract class UnlinkedWasmCall(
    final override val source: ControlInstruction.WasmCall,
    val resultDestinationSlot: Int?,
) : DispatchableInstruction(), LinkableWasmCall {

    protected val strategy = source.strategy

    protected fun linkedTarget(): Triple<Int, Int, Int> {
        check(strategy.isInstalled) {
            "direct Wasm call target is not installed"
        }
        return Triple(strategy.frameSlots, strategy.entryIp, strategy.interfaceSlotCount)
    }

    protected fun linkedActivationHeader(callSiteIp: Int): Long {
        checkWasmCallSite(callSiteIp, source.callFrameOffset, resultDestinationSlot)
        val returnAddress = if (resultDestinationSlot == null) {
            callSiteIp + 1
        } else {
            resultCallSiteIp(callSiteIp + 1)
        }
        return activationHeader(returnAddress, source.callFrameOffset)
    }

    final override fun invoke(
        vstack: ValueStack,
        context: ExecutionContext,
        nextIp: Int,
    ): Int = error("unlinked Wasm call cannot be dispatched")
}

private class UnlinkedWasmCallWithOperands(
    instruction: ControlInstruction.WasmCall,
    private val callFrameOffset: Int,
    resultDestinationSlot: Int?,
) : UnlinkedWasmCall(instruction, resultDestinationSlot) {

    override fun link(callSiteIp: Int): DispatchableInstruction {
        val (frameSlots, entryIp, activationHeaderSlot) = linkedTarget()
        return linkedWasmCallDispatcher(
            operands = source.operands,
            callFrameOffset = callFrameOffset,
            frameEndOffset = callFrameOffset + frameSlots,
            entryIp = entryIp,
            activationHeaderSlot = activationHeaderSlot,
            activationHeader = linkedActivationHeader(callSiteIp),
            localInitialization = LocalInitialization.None,
            resultDestinationSlot = resultDestinationSlot,
        )
    }
}

private class UnlinkedWasmCallByStrategy(
    instruction: ControlInstruction.WasmCall,
    private val callFrameOffset: Int,
    resultDestinationSlot: Int?,
) : UnlinkedWasmCall(instruction, resultDestinationSlot) {

    override fun link(callSiteIp: Int): DispatchableInstruction {
        if (strategy.localInitialization == LocalInitialization.None) {
            return wasmCallWithoutLocalsDispatcher(
                source,
                source.operands,
                callFrameOffset,
                resultDestinationSlot,
            ).link(callSiteIp)
        }
        val (frameSlots, entryIp, activationHeaderSlot) = linkedTarget()
        return linkedWasmCallDispatcher(
            operands = source.operands,
            callFrameOffset = callFrameOffset,
            frameEndOffset = callFrameOffset + frameSlots,
            entryIp = entryIp,
            activationHeaderSlot = activationHeaderSlot,
            activationHeader = linkedActivationHeader(callSiteIp),
            localInitialization = strategy.localInitialization,
            resultDestinationSlot = resultDestinationSlot,
        )
    }
}

private class UnlinkedWasmCallWithoutLocalsOrOperandTransfer(
    instruction: ControlInstruction.WasmCall,
    private val callFrameOffset: Int,
    resultDestinationSlot: Int?,
) : UnlinkedWasmCall(instruction, resultDestinationSlot) {

    override fun link(callSiteIp: Int): DispatchableInstruction {
        val (frameSlots, entryIp, activationHeaderSlot) = linkedTarget()
        return LinkedWasmCallWithoutLocalsOrOperandTransfer(
            callFrameOffset = callFrameOffset,
            frameEndOffset = callFrameOffset + frameSlots,
            entryIp = entryIp,
            activationHeaderSlot = activationHeaderSlot,
            activationHeader = linkedActivationHeader(callSiteIp),
            resultDestinationSlot = resultDestinationSlot ?: NO_RESULT_DESTINATION,
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

private class UnlinkedWasmCallWithoutLocalsWithImmediateOperand(
    instruction: ControlInstruction.WasmCall,
    private val callFrameOffset: Int,
    private val operand: Long,
    resultDestinationSlot: Int?,
) : UnlinkedWasmCall(instruction, resultDestinationSlot) {

    override fun link(callSiteIp: Int): DispatchableInstruction {
        val (frameSlots, entryIp, activationHeaderSlot) = linkedTarget()
        return LinkedWasmCallWithoutLocalsWithImmediateOperand(
            callFrameOffset = callFrameOffset,
            frameEndOffset = callFrameOffset + frameSlots,
            entryIp = entryIp,
            operand = operand,
            activationHeaderSlot = activationHeaderSlot,
            activationHeader = linkedActivationHeader(callSiteIp),
            resultDestinationSlot = resultDestinationSlot ?: NO_RESULT_DESTINATION,
        )
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

private class UnlinkedWasmCallWithoutLocalsWithSlotOperand(
    instruction: ControlInstruction.WasmCall,
    private val callFrameOffset: Int,
    private val sourceSlot: Int,
    resultDestinationSlot: Int?,
) : UnlinkedWasmCall(instruction, resultDestinationSlot) {

    override fun link(callSiteIp: Int): DispatchableInstruction {
        val (frameSlots, entryIp, activationHeaderSlot) = linkedTarget()
        return LinkedWasmCallWithoutLocalsWithSlotOperand(
            callFrameOffset = callFrameOffset,
            frameEndOffset = callFrameOffset + frameSlots,
            entryIp = entryIp,
            sourceSlot = sourceSlot,
            activationHeaderSlot = activationHeaderSlot,
            activationHeader = linkedActivationHeader(callSiteIp),
            resultDestinationSlot = resultDestinationSlot ?: NO_RESULT_DESTINATION,
        )
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

private class UnlinkedWasmCallWithoutLocalsWithTwoSlotOperands(
    instruction: ControlInstruction.WasmCall,
    private val callFrameOffset: Int,
    private val firstSourceSlot: Int,
    private val secondSourceSlot: Int,
    resultDestinationSlot: Int?,
) : UnlinkedWasmCall(instruction, resultDestinationSlot) {

    override fun link(callSiteIp: Int): DispatchableInstruction {
        val (frameSlots, entryIp, activationHeaderSlot) = linkedTarget()
        return LinkedWasmCallWithoutLocalsWithTwoSlotOperands(
            callFrameOffset = callFrameOffset,
            frameEndOffset = callFrameOffset + frameSlots,
            entryIp = entryIp,
            firstSourceSlot = firstSourceSlot,
            secondSourceSlot = secondSourceSlot,
            activationHeaderSlot = activationHeaderSlot,
            activationHeader = linkedActivationHeader(callSiteIp),
            resultDestinationSlot = resultDestinationSlot ?: NO_RESULT_DESTINATION,
        )
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

private class UnlinkedWasmCallWithoutLocalsWithThreeSlotOperands(
    instruction: ControlInstruction.WasmCall,
    private val callFrameOffset: Int,
    private val firstSourceSlot: Int,
    private val secondSourceSlot: Int,
    private val thirdSourceSlot: Int,
    resultDestinationSlot: Int?,
) : UnlinkedWasmCall(instruction, resultDestinationSlot) {

    override fun link(callSiteIp: Int): DispatchableInstruction {
        val (frameSlots, entryIp, activationHeaderSlot) = linkedTarget()
        return LinkedWasmCallWithoutLocalsWithThreeSlotOperands(
            callFrameOffset = callFrameOffset,
            frameEndOffset = callFrameOffset + frameSlots,
            entryIp = entryIp,
            firstSourceSlot = firstSourceSlot,
            secondSourceSlot = secondSourceSlot,
            thirdSourceSlot = thirdSourceSlot,
            activationHeaderSlot = activationHeaderSlot,
            activationHeader = linkedActivationHeader(callSiteIp),
            resultDestinationSlot = resultDestinationSlot ?: NO_RESULT_DESTINATION,
        )
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

private class UnlinkedWasmCallWithoutLocalsWithFourSlotOperands(
    instruction: ControlInstruction.WasmCall,
    private val callFrameOffset: Int,
    private val firstSourceSlot: Int,
    private val secondSourceSlot: Int,
    private val thirdSourceSlot: Int,
    private val fourthSourceSlot: Int,
    resultDestinationSlot: Int?,
) : UnlinkedWasmCall(instruction, resultDestinationSlot) {

    override fun link(callSiteIp: Int): DispatchableInstruction {
        val (frameSlots, entryIp, activationHeaderSlot) = linkedTarget()
        return LinkedWasmCallWithoutLocalsWithFourSlotOperands(
            callFrameOffset = callFrameOffset,
            frameEndOffset = callFrameOffset + frameSlots,
            entryIp = entryIp,
            firstSourceSlot = firstSourceSlot,
            secondSourceSlot = secondSourceSlot,
            thirdSourceSlot = thirdSourceSlot,
            fourthSourceSlot = fourthSourceSlot,
            activationHeaderSlot = activationHeaderSlot,
            activationHeader = linkedActivationHeader(callSiteIp),
            resultDestinationSlot = resultDestinationSlot ?: NO_RESULT_DESTINATION,
        )
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

fun CallDispatcher(
    instruction: ControlInstruction.HostCall,
    resultDestinationSlot: Int? = null,
): DispatchableInstruction {
    val function = instruction.instance
    val caller = instruction.caller
    val operands = instruction.operands
    val callFrameOffset = instruction.callFrameOffset
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

fun CallDispatcher(
    instruction: ControlInstruction.CallIndirectI,
    resultDestinationSlot: Int? = null,
): DispatchableInstruction = UnlinkedCallIndirectI(instruction, resultDestinationSlot)

private class UnlinkedCallIndirectI(
    override val source: ControlInstruction.CallIndirectI,
    private val resultDestinationSlot: Int?,
) : DispatchableInstruction(), LinkableWasmCall {

    override fun link(callSiteIp: Int): DispatchableInstruction =
        CallDispatcher(source, callSiteIp, resultDestinationSlot)

    override fun invoke(
        vstack: ValueStack,
        context: ExecutionContext,
        nextIp: Int,
    ): Int = error("unlinked indirect Wasm call cannot be dispatched")
}

fun CallDispatcher(
    instruction: ControlInstruction.CallIndirectS,
    resultDestinationSlot: Int? = null,
): DispatchableInstruction = UnlinkedCallIndirectS(instruction, resultDestinationSlot)

private class UnlinkedCallIndirectS(
    override val source: ControlInstruction.CallIndirectS,
    private val resultDestinationSlot: Int?,
) : DispatchableInstruction(), LinkableWasmCall {

    override fun link(callSiteIp: Int): DispatchableInstruction =
        CallDispatcher(source, callSiteIp, resultDestinationSlot)

    override fun invoke(
        vstack: ValueStack,
        context: ExecutionContext,
        nextIp: Int,
    ): Int = error("unlinked indirect Wasm call cannot be dispatched")
}

fun CallDispatcher(
    instruction: ControlInstruction.CallRefS,
    resultDestinationSlot: Int? = null,
): DispatchableInstruction = UnlinkedCallRef(instruction, resultDestinationSlot)

private class UnlinkedCallRef(
    override val source: ControlInstruction.CallRefS,
    private val resultDestinationSlot: Int?,
) : DispatchableInstruction(), LinkableWasmCall {

    override fun link(callSiteIp: Int): DispatchableInstruction =
        CallDispatcher(source, callSiteIp, resultDestinationSlot)

    override fun invoke(
        vstack: ValueStack,
        context: ExecutionContext,
        nextIp: Int,
    ): Int = error("unlinked reference Wasm call cannot be dispatched")
}

fun CallDispatcher(
    instruction: ControlInstruction.CallIndirectI,
    callSiteIp: Int,
    resultDestinationSlot: Int? = null,
): DispatchableInstruction {
    checkWasmCallSite(callSiteIp, instruction.callFrameOffset, resultDestinationSlot)
    val returnIp = callSiteIp + 1
    val header = activationHeader(
        if (resultDestinationSlot == null) returnIp else resultCallSiteIp(returnIp),
        instruction.callFrameOffset,
    )
    return if (resultDestinationSlot == null) {
        DispatchableInstruction { vstack, context, _ ->
            CallExecutor(vstack, context, instruction, returnIp, header)
        }
    } else {
        ResultCallIndirectIDispatcher(instruction, returnIp, header, resultDestinationSlot)
    }
}

fun CallDispatcher(
    instruction: ControlInstruction.CallIndirectS,
    callSiteIp: Int,
    resultDestinationSlot: Int? = null,
): DispatchableInstruction {
    checkWasmCallSite(callSiteIp, instruction.callFrameOffset, resultDestinationSlot)
    val returnIp = callSiteIp + 1
    val header = activationHeader(
        if (resultDestinationSlot == null) returnIp else resultCallSiteIp(returnIp),
        instruction.callFrameOffset,
    )
    return if (resultDestinationSlot == null) {
        DispatchableInstruction { vstack, context, _ ->
            CallExecutor(vstack, context, instruction, returnIp, header)
        }
    } else {
        ResultCallIndirectSDispatcher(instruction, returnIp, header, resultDestinationSlot)
    }
}

fun CallDispatcher(
    instruction: ControlInstruction.CallRefS,
    callSiteIp: Int,
    resultDestinationSlot: Int? = null,
): DispatchableInstruction {
    checkWasmCallSite(callSiteIp, instruction.callFrameOffset, resultDestinationSlot)
    val returnIp = callSiteIp + 1
    val header = activationHeader(
        if (resultDestinationSlot == null) returnIp else resultCallSiteIp(returnIp),
        instruction.callFrameOffset,
    )
    return if (resultDestinationSlot == null) {
        DispatchableInstruction { vstack, context, _ ->
            CallExecutor(vstack, context, instruction, returnIp, header)
        }
    } else {
        ResultCallRefDispatcher(instruction, returnIp, header, resultDestinationSlot)
    }
}

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
    private val instruction: ControlInstruction.CallIndirectI,
    private val returnIp: Int,
    private val activationHeader: Long,
    override val resultDestinationSlot: Int,
) : DispatchableInstruction(), CallSiteResultDestination {

    override fun invoke(
        vstack: ValueStack,
        context: ExecutionContext,
        nextIp: Int,
    ): Int = CallExecutor(vstack, context, instruction, returnIp, activationHeader, resultDestinationSlot)
}

private class ResultCallIndirectSDispatcher(
    private val instruction: ControlInstruction.CallIndirectS,
    private val returnIp: Int,
    private val activationHeader: Long,
    override val resultDestinationSlot: Int,
) : DispatchableInstruction(), CallSiteResultDestination {

    override fun invoke(
        vstack: ValueStack,
        context: ExecutionContext,
        nextIp: Int,
    ): Int = CallExecutor(vstack, context, instruction, returnIp, activationHeader, resultDestinationSlot)
}

private class ResultCallRefDispatcher(
    private val instruction: ControlInstruction.CallRefS,
    private val returnIp: Int,
    private val activationHeader: Long,
    override val resultDestinationSlot: Int,
) : DispatchableInstruction(), CallSiteResultDestination {

    override fun invoke(
        vstack: ValueStack,
        context: ExecutionContext,
        nextIp: Int,
    ): Int = CallExecutor(vstack, context, instruction, returnIp, activationHeader, resultDestinationSlot)
}

fun ReturnCallDispatcher(
    instruction: ControlInstruction.ReturnWasmCall,
): DispatchableInstruction = UnlinkedReturnWasmCall(instruction)

private class UnlinkedReturnWasmCall(
    override val source: ControlInstruction.ReturnWasmCall,
) : DispatchableInstruction(), LinkableWasmCall {

    override fun link(callSiteIp: Int): DispatchableInstruction {
        val strategy = source.strategy
        check(strategy.isInstalled) {
            "direct Wasm tail-call target is not installed"
        }
        return linkedReturnWasmCallDispatcher(
            frameSlots = strategy.frameSlots,
            entryIp = strategy.entryIp,
            activationHeaderSlot = strategy.interfaceSlotCount,
            callerActivationHeaderSlot = source.callerActivationHeaderSlot,
            operands = source.operands,
            localInitialization = strategy.localInitialization,
        )
    }

    override fun invoke(
        vstack: ValueStack,
        context: ExecutionContext,
        nextIp: Int,
    ): Int = error("unlinked Wasm tail call cannot be dispatched")
}

private fun linkedReturnWasmCallDispatcher(
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
    instruction: ControlInstruction.FunctionReturn,
): DispatchableInstruction {
    val results = instruction.results
    val resultCount = results.sources.size
    val activationHeaderSlot = instruction.activationHeaderSlot
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

fun ReturnCallDispatcher(
    instruction: ControlInstruction.ReturnHostCall,
): DispatchableInstruction {
    val function = instruction.instance
    val caller = instruction.caller
    val operands = instruction.operands
    val callFrameOffset = instruction.callFrameOffset
    val activationHeaderSlot = instruction.activationHeaderSlot
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

fun ReturnCallDispatcher(
    instruction: ControlInstruction.ReturnCallIndirectI,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    ReturnCallExecutor(vstack, context, instruction, nextIp)
}

fun ReturnCallDispatcher(
    instruction: ControlInstruction.ReturnCallIndirectS,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    ReturnCallExecutor(vstack, context, instruction, nextIp)
}

fun ReturnCallDispatcher(
    instruction: ControlInstruction.ReturnCallRefS,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    ReturnCallExecutor(vstack, context, instruction, nextIp)
}

fun ThrowDispatcher(
    instruction: ControlInstruction.Throw,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    ThrowExecutor(vstack, context, instruction, nextIp - 1)
}

fun ThrowRefDispatcher(
    instruction: ControlInstruction.ThrowRefS,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    ThrowRefExecutor(vstack, context, instruction, nextIp - 1)
}
