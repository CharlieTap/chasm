package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.compiler.backend.abi.UNREACHABLE
import io.github.charlietap.chasm.compiler.backend.analysis.form.operands

internal fun lowerControlInstruction(
    instruction: ControlInstruction,
    context: LowerContext,
) {
    when (instruction) {
        ControlInstruction.Unreachable -> context.trap(UNREACHABLE)
        is ControlInstruction.FunctionReturn -> context.ret(instruction.results.operands())
        is ControlInstruction.Call, is ControlInstruction.CallIndirectI, is ControlInstruction.CallIndirectS,
        is ControlInstruction.CallRefS,
        -> context.lowerCall(instruction)
        is ControlInstruction.ReturnCall, is ControlInstruction.ReturnCallIndirectI, is ControlInstruction.ReturnCallIndirectS,
        is ControlInstruction.ReturnCallRefS,
        -> context.lowerTailCall(instruction)
        is ControlInstruction.Throw -> context.lowerThrow(instruction.exceptionTag, instruction.firstPayloadSlot)
        is ControlInstruction.ThrowRefS -> context.lowerThrowRef(instruction.exceptionSlot)
    }
}
