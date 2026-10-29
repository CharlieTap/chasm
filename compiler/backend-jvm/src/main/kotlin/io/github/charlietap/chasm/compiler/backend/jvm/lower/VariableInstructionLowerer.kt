package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.cir.GlobalIndex
import io.github.charlietap.chasm.cir.VariableInstruction
import io.github.charlietap.chasm.compiler.backend.abi.GLOBAL_GET
import io.github.charlietap.chasm.compiler.backend.abi.GLOBAL_SET
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.immediate
import io.github.charlietap.chasm.compiler.backend.analysis.form.slot

internal fun lowerVariableInstruction(
    instruction: VariableInstruction,
    context: LowerContext,
) {
    when (instruction) {
        is VariableInstruction.GlobalGetS -> {
            context.pushResource(Resource.Global(instruction.global.index))
            context.emit.invokeVirtual(GLOBAL_GET)
            context.storeLong(instruction.destinationSlot)
        }
        is VariableInstruction.GlobalSetI -> context.globalSet(instruction.global, immediate(instruction.value))
        is VariableInstruction.GlobalSetS -> context.globalSet(instruction.global, slot(instruction.sourceSlot))
    }
}

private fun LowerContext.globalSet(
    global: GlobalIndex,
    value: Operand,
) {
    pushResource(Resource.Global(global.index))
    pushLong(value)
    emit.invokeVirtual(GLOBAL_SET)
}
