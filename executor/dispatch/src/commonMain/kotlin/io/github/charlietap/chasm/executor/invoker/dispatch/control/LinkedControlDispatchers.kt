package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction
import io.github.charlietap.chasm.runtime.program.Program
import io.github.charlietap.chasm.runtime.stack.ValueStack

fun CallDispatcher(
    instruction: ControlInstruction.WasmCall,
    resultDestinationSlot: Int? = null,
): DispatchableInstruction = UnlinkedCall(instruction) { callSiteIp ->
    val strategy = instruction.strategy
    check(strategy.isInstalled) {
        "direct Wasm call target is not installed"
    }
    WasmCallDispatcher(
        operands = instruction.operands,
        callFrameOffset = instruction.callFrameOffset,
        frameSlots = strategy.frameSlots,
        entryIp = strategy.entryIp,
        activationHeaderSlot = strategy.interfaceSlotCount,
        localInitialization = strategy.localInitialization,
        callSiteIp = callSiteIp,
        resultDestinationSlot = resultDestinationSlot,
    )
}

fun CallDispatcher(
    instruction: ControlInstruction.HostCall,
    resultDestinationSlot: Int? = null,
): DispatchableInstruction = HostCallDispatcher(
    function = instruction.instance,
    caller = instruction.caller,
    operands = instruction.operands,
    callFrameOffset = instruction.callFrameOffset,
    resultDestinationSlot = resultDestinationSlot,
)

fun CallDispatcher(
    instruction: ControlInstruction.CallIndirectI,
    resultDestinationSlot: Int? = null,
): DispatchableInstruction = UnlinkedCall(instruction) { callSiteIp ->
    CallDispatcher(instruction, callSiteIp, resultDestinationSlot)
}

fun CallDispatcher(
    instruction: ControlInstruction.CallIndirectS,
    resultDestinationSlot: Int? = null,
): DispatchableInstruction = UnlinkedCall(instruction) { callSiteIp ->
    CallDispatcher(instruction, callSiteIp, resultDestinationSlot)
}

fun CallDispatcher(
    instruction: ControlInstruction.CallRefS,
    resultDestinationSlot: Int? = null,
): DispatchableInstruction = UnlinkedCall(instruction) { callSiteIp ->
    CallDispatcher(instruction, callSiteIp, resultDestinationSlot)
}

fun CallDispatcher(
    instruction: ControlInstruction.CallIndirectI,
    callSiteIp: Int,
    resultDestinationSlot: Int? = null,
): DispatchableInstruction = CallIndirectIDispatcher(
    elementIndex = instruction.elementIndex,
    operands = instruction.operands,
    type = instruction.type,
    table = instruction.table,
    caller = instruction.caller,
    callFrameOffset = instruction.callFrameOffset,
    callSiteIp = callSiteIp,
    resultDestinationSlot = resultDestinationSlot,
)

fun CallDispatcher(
    instruction: ControlInstruction.CallIndirectS,
    callSiteIp: Int,
    resultDestinationSlot: Int? = null,
): DispatchableInstruction = CallIndirectSDispatcher(
    elementIndexSlot = instruction.elementIndexSlot,
    operands = instruction.operands,
    type = instruction.type,
    table = instruction.table,
    caller = instruction.caller,
    callFrameOffset = instruction.callFrameOffset,
    callSiteIp = callSiteIp,
    resultDestinationSlot = resultDestinationSlot,
)

fun CallDispatcher(
    instruction: ControlInstruction.CallRefS,
    callSiteIp: Int,
    resultDestinationSlot: Int? = null,
): DispatchableInstruction = CallRefSDispatcher(
    functionSlot = instruction.functionSlot,
    operands = instruction.operands,
    caller = instruction.caller,
    callFrameOffset = instruction.callFrameOffset,
    callSiteIp = callSiteIp,
    resultDestinationSlot = resultDestinationSlot,
)

fun ReturnCallDispatcher(
    instruction: ControlInstruction.ReturnWasmCall,
): DispatchableInstruction = UnlinkedCall(instruction) {
    val strategy = instruction.strategy
    check(strategy.isInstalled) {
        "direct Wasm tail-call target is not installed"
    }
    ReturnWasmCallDispatcher(
        frameSlots = strategy.frameSlots,
        entryIp = strategy.entryIp,
        activationHeaderSlot = strategy.interfaceSlotCount,
        callerActivationHeaderSlot = instruction.callerActivationHeaderSlot,
        operands = instruction.operands,
        localInitialization = strategy.localInitialization,
    )
}

fun FunctionReturnDispatcher(
    instruction: ControlInstruction.FunctionReturn,
): DispatchableInstruction = FunctionReturnDispatcher(instruction.results, instruction.activationHeaderSlot)

fun ReturnCallDispatcher(
    instruction: ControlInstruction.ReturnHostCall,
): DispatchableInstruction = ReturnHostCallDispatcher(
    function = instruction.instance,
    caller = instruction.caller,
    operands = instruction.operands,
    callFrameOffset = instruction.callFrameOffset,
    activationHeaderSlot = instruction.activationHeaderSlot,
)

fun ReturnCallDispatcher(
    instruction: ControlInstruction.ReturnCallIndirectI,
): DispatchableInstruction = ReturnCallIndirectIDispatcher(
    elementIndex = instruction.elementIndex,
    operands = instruction.operands,
    type = instruction.type,
    table = instruction.table,
    caller = instruction.caller,
    callFrameOffset = instruction.callFrameOffset,
    callerActivationHeaderSlot = instruction.callerActivationHeaderSlot,
)

fun ReturnCallDispatcher(
    instruction: ControlInstruction.ReturnCallIndirectS,
): DispatchableInstruction = ReturnCallIndirectSDispatcher(
    elementIndexSlot = instruction.elementIndexSlot,
    operands = instruction.operands,
    type = instruction.type,
    table = instruction.table,
    caller = instruction.caller,
    callFrameOffset = instruction.callFrameOffset,
    callerActivationHeaderSlot = instruction.callerActivationHeaderSlot,
)

fun ReturnCallDispatcher(
    instruction: ControlInstruction.ReturnCallRefS,
): DispatchableInstruction = ReturnCallRefSDispatcher(
    functionSlot = instruction.functionSlot,
    operands = instruction.operands,
    caller = instruction.caller,
    callFrameOffset = instruction.callFrameOffset,
    callerActivationHeaderSlot = instruction.callerActivationHeaderSlot,
)

fun ThrowDispatcher(
    instruction: ControlInstruction.Throw,
): DispatchableInstruction = ThrowDispatcher(instruction.tagAddress, instruction.firstPayloadSlot)

fun ThrowRefDispatcher(
    instruction: ControlInstruction.ThrowRefS,
): DispatchableInstruction = ThrowRefSDispatcher(instruction.exceptionSlot)

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
        if (instruction is UnlinkedCall) {
            val linked = instruction.link(ip)
            onLinked?.invoke(linked, instruction.source)
            program.replace(ip, linked)
            linkedCount++
        }
    }
    return linkedCount
}

private class UnlinkedCall(
    val source: LinkedInstruction,
    private val linker: (Int) -> DispatchableInstruction,
) : DispatchableInstruction() {

    fun link(
        callSiteIp: Int,
    ): DispatchableInstruction = linker(callSiteIp)

    override fun invoke(
        vstack: ValueStack,
        context: ExecutionContext,
        nextIp: Int,
    ): Int = error("unlinked call cannot be dispatched")
}
