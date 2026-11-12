package io.github.charlietap.chasm.compiler.frontend

import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.type.ValueType

internal class OperandCapture(
    private val parameters: Int,
    private val locals: MutableList<Local>,
) {
    private val original = parameters + locals.size

    fun local(
        type: ValueType,
    ): Index.LocalIndex {
        locals += Local(Index.LocalIndex(locals.size.toUInt()), type)
        return Index.LocalIndex((parameters + locals.size - 1).toUInt())
    }

    fun capture(
        into: MutableList<Instruction>,
        operands: Array<Instruction?>,
        lowest: Int,
    ): Int {
        var operand = operands.size - 1
        while (operand >= lowest) {
            val last = into.lastOrNull()
            if (last == null || !last.isRepeatable()) break
            into.removeAt(into.lastIndex)
            operands[operand--] = last
        }
        return operand
    }

    private fun Instruction.isRepeatable(): Boolean = when (this) {
        is NumericInstruction.I32Const,
        is NumericInstruction.I64Const,
        is NumericInstruction.F32Const,
        is NumericInstruction.F64Const,
        -> true
        is VariableInstruction.LocalGet -> localIdx.toInt() < original
        else -> false
    }
}
