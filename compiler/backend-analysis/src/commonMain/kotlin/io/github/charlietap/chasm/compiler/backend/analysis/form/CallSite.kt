package io.github.charlietap.chasm.compiler.backend.analysis.form

import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.OperandTransfer
import io.github.charlietap.chasm.cir.TypeIndex
import io.github.charlietap.chasm.compiler.backend.analysis.ModuleIndexSpaces
import io.github.charlietap.chasm.type.FunctionType

class CallSite(
    val typeIndex: TypeIndex,
    val type: FunctionType,
    val operands: OperandTransfer,
    val resultBase: Int,
) {
    private var arguments: List<Operand>? = null

    fun arguments(): List<Operand> {
        return arguments ?: operands.operands().also { built -> arguments = built }
    }
}

fun ControlInstruction.callSite(
    declarations: ModuleIndexSpaces,
): CallSite? {
    return when (val i = this) {
        is ControlInstruction.Call -> {
            val type = declarations.functionTypeIndex(i.function)
            CallSite(type, declarations.functionType(type), i.operands, i.resultDestinationSlot ?: i.callFrameOffset)
        }
        is ControlInstruction.CallIndirectI ->
            CallSite(i.type, declarations.functionType(i.type), i.operands, i.resultDestinationSlot ?: i.callFrameOffset)
        is ControlInstruction.CallIndirectS ->
            CallSite(i.type, declarations.functionType(i.type), i.operands, i.resultDestinationSlot ?: i.callFrameOffset)
        is ControlInstruction.CallRefS ->
            CallSite(i.type, declarations.functionType(i.type), i.operands, i.resultDestinationSlot ?: i.callFrameOffset)
        else -> null
    }
}

fun ControlInstruction.operandTransfer(): OperandTransfer? {
    return when (val instruction = this) {
        is ControlInstruction.Call -> instruction.operands
        is ControlInstruction.CallIndirectI -> instruction.operands
        is ControlInstruction.CallIndirectS -> instruction.operands
        is ControlInstruction.CallRefS -> instruction.operands
        is ControlInstruction.ReturnCall -> instruction.operands.wasm
        is ControlInstruction.ReturnCallIndirectI -> instruction.operands.wasm
        is ControlInstruction.ReturnCallIndirectS -> instruction.operands.wasm
        is ControlInstruction.ReturnCallRefS -> instruction.operands.wasm
        is ControlInstruction.FunctionReturn -> instruction.results
        else -> null
    }
}

fun ControlInstruction.tailCallArguments(): List<Operand>? = when (val i = this) {
    is ControlInstruction.ReturnCall -> i.operands.wasm.operands()
    is ControlInstruction.ReturnCallIndirectI -> i.operands.wasm.operands()
    is ControlInstruction.ReturnCallIndirectS -> i.operands.wasm.operands()
    is ControlInstruction.ReturnCallRefS -> i.operands.wasm.operands()
    else -> null
}
