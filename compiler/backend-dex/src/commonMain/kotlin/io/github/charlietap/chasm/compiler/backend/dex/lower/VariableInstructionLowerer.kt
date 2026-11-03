package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.cir.GlobalIndex
import io.github.charlietap.chasm.cir.VariableInstruction
import io.github.charlietap.chasm.compiler.backend.abi.GLOBAL_GET
import io.github.charlietap.chasm.compiler.backend.abi.GLOBAL_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.GLOBAL_SET
import io.github.charlietap.chasm.compiler.backend.abi.Resource

internal fun lowerVariableInstruction(
    instruction: VariableInstruction,
    context: LowerContext,
) {
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

private fun LowerContext.loadGlobal(
    global: GlobalIndex,
) = loadObject(Registers.OBJECT, Resource.Global(global.index), GLOBAL_INSTANCE)
