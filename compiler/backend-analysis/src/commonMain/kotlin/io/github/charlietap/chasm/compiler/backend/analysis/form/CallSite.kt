package io.github.charlietap.chasm.compiler.backend.analysis.form

import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.compiler.backend.analysis.ModuleIndexSpaces
import io.github.charlietap.chasm.type.FunctionType

class CallSite(
    val type: FunctionType,
    val arguments: List<Operand>,
    val resultBase: Int,
)

fun ControlInstruction.callSite(
    declarations: ModuleIndexSpaces,
): CallSite? = when (val i = this) {
    is ControlInstruction.Call ->
        CallSite(declarations.functionType(i.function), i.operands.operands(), i.resultDestinationSlot ?: i.callFrameOffset)
    is ControlInstruction.CallIndirectI ->
        CallSite(declarations.functionType(i.type), i.operands.operands(), i.resultDestinationSlot ?: i.callFrameOffset)
    is ControlInstruction.CallIndirectS ->
        CallSite(declarations.functionType(i.type), i.operands.operands(), i.resultDestinationSlot ?: i.callFrameOffset)
    is ControlInstruction.CallRefS ->
        CallSite(declarations.functionType(i.type), i.operands.operands(), i.resultDestinationSlot ?: i.callFrameOffset)
    else -> null
}

fun ControlInstruction.tailCallArguments(): List<Operand>? = when (val i = this) {
    is ControlInstruction.ReturnCall -> i.operands.wasm.operands()
    is ControlInstruction.ReturnCallIndirectI -> i.operands.wasm.operands()
    is ControlInstruction.ReturnCallIndirectS -> i.operands.wasm.operands()
    is ControlInstruction.ReturnCallRefS -> i.operands.wasm.operands()
    else -> null
}
