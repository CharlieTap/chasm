package io.github.charlietap.chasm.compiler.backend.chasm.link

import io.github.charlietap.chasm.executor.invoker.dispatch.control.ReturnWasmCallDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.WasmCallDispatcher
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction

internal typealias PendingWasmCallLinker = (PendingWasmCall) -> DispatchableInstruction

internal fun PendingWasmCallLinker(
    call: PendingWasmCall,
): DispatchableInstruction {
    val strategy = call.callee.callStrategy
    check(strategy.isInstalled) { "direct Wasm call target is not installed" }
    return when (call) {
        is PendingWasmCall.Call -> WasmCallDispatcher(
            operands = call.operands,
            callFrameOffset = call.callFrameOffset,
            frameSlots = strategy.frameSlots,
            entryIp = strategy.entryIp,
            activationHeaderSlot = strategy.interfaceSlotCount,
            localInitialization = strategy.localInitialization,
            callSiteIp = call.ip,
            resultDestinationSlot = call.resultDestinationSlot,
        )
        is PendingWasmCall.ReturnCall -> ReturnWasmCallDispatcher(
            frameSlots = strategy.frameSlots,
            entryIp = strategy.entryIp,
            activationHeaderSlot = strategy.interfaceSlotCount,
            callerActivationHeaderSlot = call.callerActivationHeaderSlot,
            operands = call.operands,
            localInitialization = strategy.localInitialization,
        )
    }
}
