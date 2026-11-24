package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.cir.GlobalIndex
import io.github.charlietap.chasm.cir.VariableInstruction
import io.github.charlietap.chasm.compiler.backend.abi.GLOBAL_GET
import io.github.charlietap.chasm.compiler.backend.abi.GLOBAL_SET
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_GLOBAL
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_GLOBAL_VALUE
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.immediate
import io.github.charlietap.chasm.compiler.backend.analysis.form.slot
import io.github.charlietap.chasm.type.ValueType

internal fun lowerVariableInstruction(
    instruction: VariableInstruction,
    context: LowerContext,
) {
    when (instruction) {
        is VariableInstruction.GlobalGetS -> context.globalGet(instruction.global, instruction.destinationSlot)
        is VariableInstruction.GlobalSetI -> context.globalSet(instruction.global, immediate(instruction.value))
        is VariableInstruction.GlobalSetS -> context.globalSet(instruction.global, slot(instruction.sourceSlot))
    }
}

private fun LowerContext.globalGet(
    global: GlobalIndex,
    destination: Int,
) {

    if (declarations.globalType(global) is ValueType.Reference) {
        pushResource(Resource.ReferenceGlobal(global.index))
        emit.getField(REFERENCE_GLOBAL, REFERENCE_GLOBAL_VALUE, OBJECT_TYPE)
        storeObject(destination)
        return
    }

    pushResource(Resource.Global(global.index))
    emit.invokeVirtual(GLOBAL_GET)
    storeLong(destination)
}

private fun LowerContext.globalSet(
    global: GlobalIndex,
    value: Operand,
) {

    if (declarations.globalType(global) is ValueType.Reference) {
        pushResource(Resource.ReferenceGlobal(global.index))
        pushObject(value)
        emit.putField(REFERENCE_GLOBAL, REFERENCE_GLOBAL_VALUE, OBJECT_TYPE)
        return
    }

    pushResource(Resource.Global(global.index))
    pushLong(value)
    emit.invokeVirtual(GLOBAL_SET)
}
