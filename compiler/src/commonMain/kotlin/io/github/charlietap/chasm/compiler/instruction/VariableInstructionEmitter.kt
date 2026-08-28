package io.github.charlietap.chasm.compiler.instruction

import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.operand.OperandSource
import io.github.charlietap.chasm.compiler.operand.isImmediate
import io.github.charlietap.chasm.compiler.operand.sourceSlot
import io.github.charlietap.chasm.executor.invoker.dispatch.variable.VariableInstructionDispatcher
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instruction.VariableInstruction

internal fun FunctionCompilationContext.emitGlobalGet(
    global: GlobalInstance,
    destinationSlot: Int,
) {
    val instruction = VariableInstruction.GlobalGetS(global, destinationSlot)
    emit(instruction, ::VariableInstructionDispatcher)
}

internal fun FunctionCompilationContext.emitGlobalSet(
    global: GlobalInstance,
    source: OperandSource,
) {
    val instruction = if (source.isImmediate) {
        VariableInstruction.GlobalSetI(source.sourceBits, global)
    } else {
        VariableInstruction.GlobalSetS(source.sourceSlot, global)
    }
    emit(instruction, ::VariableInstructionDispatcher)
}
