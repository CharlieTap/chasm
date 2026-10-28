package io.github.charlietap.chasm.compiler.frontend.instruction

import io.github.charlietap.chasm.cir.GlobalIndex
import io.github.charlietap.chasm.cir.VariableInstruction
import io.github.charlietap.chasm.compiler.frontend.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.frontend.operand.OperandSource
import io.github.charlietap.chasm.compiler.frontend.operand.isImmediate
import io.github.charlietap.chasm.compiler.frontend.operand.sourceSlot

internal fun FunctionCompilationContext.emitGlobalGet(
    global: GlobalIndex,
    destinationSlot: Int,
) {
    val instruction = VariableInstruction.GlobalGetS(global, destinationSlot)
    emit(instruction)
}

internal fun FunctionCompilationContext.emitGlobalSet(
    global: GlobalIndex,
    source: OperandSource,
) {
    val instruction = if (source.isImmediate) {
        VariableInstruction.GlobalSetI(source.sourceBits, global)
    } else {
        VariableInstruction.GlobalSetS(source.sourceSlot, global)
    }
    emit(instruction)
}
