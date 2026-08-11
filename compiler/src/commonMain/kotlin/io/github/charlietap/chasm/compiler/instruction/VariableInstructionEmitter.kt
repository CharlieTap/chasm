package io.github.charlietap.chasm.compiler.instruction

import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.operand.OperandSource
import io.github.charlietap.chasm.compiler.operand.isImmediate
import io.github.charlietap.chasm.compiler.operand.sourceSlot
import io.github.charlietap.chasm.executor.invoker.dispatch.variablefused.VariableSuperInstructionDispatcher
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instruction.VariableSuperInstruction

internal fun FunctionCompilationContext.emitGlobalGet(
    global: GlobalInstance,
    destinationSlot: Int,
) {
    val instruction = VariableSuperInstruction.GlobalGetS(global, destinationSlot)
    emit(instruction, ::VariableSuperInstructionDispatcher)
}

internal fun FunctionCompilationContext.emitGlobalSet(
    global: GlobalInstance,
    source: OperandSource,
) {
    val instruction = if (source.isImmediate) {
        VariableSuperInstruction.GlobalSetI(source.sourceBits, global)
    } else {
        VariableSuperInstruction.GlobalSetS(source.sourceSlot, global)
    }
    emit(instruction, ::VariableSuperInstructionDispatcher)
}
