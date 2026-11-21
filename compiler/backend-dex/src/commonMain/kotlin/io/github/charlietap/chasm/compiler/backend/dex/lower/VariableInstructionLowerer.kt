package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.cir.GlobalIndex
import io.github.charlietap.chasm.cir.VariableInstruction
import io.github.charlietap.chasm.compiler.backend.abi.GLOBAL_GET
import io.github.charlietap.chasm.compiler.backend.abi.GLOBAL_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.GLOBAL_SET
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_GLOBAL
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_GLOBAL_VALUE
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.type.ValueType

internal fun lowerVariableInstruction(
    instruction: VariableInstruction,
    context: LowerContext,
) {
    if (context.holdsReference(instruction)) {
        return context.lowerReferenceGlobal(instruction)
    }
    when (instruction) {
        is VariableInstruction.GlobalGetS -> {
            context.loadGlobal(instruction.global)
            context.emit.invokeVirtual(GLOBAL_GET, Registers.OBJECT)
            context.emit.moveResultWide(Registers.W0)
            context.store(instruction.destinationSlot, Registers.W0)
        }
        is VariableInstruction.GlobalSetI -> {
            context.loadGlobal(instruction.global)
            context.emit.constWide(Registers.W0, instruction.value)
            context.emit.invokeVirtual(GLOBAL_SET, Registers.OBJECT, Registers.W0, Registers.W0 + 1)
        }
        is VariableInstruction.GlobalSetS -> {
            context.loadGlobal(instruction.global)
            context.loadWide(Registers.W0, instruction.sourceSlot)
            context.emit.invokeVirtual(GLOBAL_SET, Registers.OBJECT, Registers.W0, Registers.W0 + 1)
        }
    }
}

private fun LowerContext.holdsReference(instruction: VariableInstruction): Boolean {
    val global = when (instruction) {
        is VariableInstruction.GlobalGetS -> instruction.global
        is VariableInstruction.GlobalSetI -> instruction.global
        is VariableInstruction.GlobalSetS -> instruction.global
    }
    return declarations.globalType(global) is ValueType.Reference
}

private fun LowerContext.lowerReferenceGlobal(instruction: VariableInstruction) {
    when (instruction) {
        is VariableInstruction.GlobalGetS -> {
            loadReferenceGlobal(instruction.global)
            emit.getField(Registers.SECOND_OBJECT, Registers.OBJECT, REFERENCE_GLOBAL, REFERENCE_GLOBAL_VALUE, OBJECT_TYPE)
            storeReference(instruction.destinationSlot, Registers.SECOND_OBJECT)
        }
        is VariableInstruction.GlobalSetI -> {
            loadReferenceGlobal(instruction.global)
            emit.const(Registers.SECOND_OBJECT, 0)
            emit.putField(Registers.SECOND_OBJECT, Registers.OBJECT, REFERENCE_GLOBAL, REFERENCE_GLOBAL_VALUE, OBJECT_TYPE)
        }
        is VariableInstruction.GlobalSetS -> {
            loadReferenceGlobal(instruction.global)
            loadReference(Registers.SECOND_OBJECT, instruction.sourceSlot)
            emit.putField(Registers.SECOND_OBJECT, Registers.OBJECT, REFERENCE_GLOBAL, REFERENCE_GLOBAL_VALUE, OBJECT_TYPE)
        }
    }
}

private fun LowerContext.loadGlobal(
    global: GlobalIndex,
) = loadObject(Registers.OBJECT, Resource.Global(global.index), GLOBAL_INSTANCE)

private fun LowerContext.loadReferenceGlobal(
    global: GlobalIndex,
) {
    loadObject(Registers.OBJECT, Resource.ReferenceGlobal(global.index), REFERENCE_GLOBAL)
}
