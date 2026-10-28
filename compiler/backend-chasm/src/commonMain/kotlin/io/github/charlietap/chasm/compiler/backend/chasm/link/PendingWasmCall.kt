package io.github.charlietap.chasm.compiler.backend.chasm.link

import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instruction.OperandTransfer

internal sealed class PendingWasmCall(
    val ip: Int,
    val callee: FunctionInstance.WasmFunction,
) {
    class Call(
        ip: Int,
        callee: FunctionInstance.WasmFunction,
        val operands: OperandTransfer,
        val callFrameOffset: Int,
        val resultDestinationSlot: Int?,
    ) : PendingWasmCall(ip, callee)

    class ReturnCall(
        ip: Int,
        callee: FunctionInstance.WasmFunction,
        val operands: OperandTransfer,
        val callerActivationHeaderSlot: Int,
    ) : PendingWasmCall(ip, callee)
}
