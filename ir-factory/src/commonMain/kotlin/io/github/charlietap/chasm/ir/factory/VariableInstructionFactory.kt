package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ir.instruction.VariableInstruction as IRVariableInstruction
import io.github.charlietap.chasm.ir.module.Index.GlobalIndex as IRGlobalIndex
import io.github.charlietap.chasm.ir.module.Index.LocalIndex as IRLocalIndex

internal fun VariableInstructionFactory(
    instruction: VariableInstruction,
): IRVariableInstruction = VariableInstructionFactory(
    instruction = instruction,
    localIndexFactory = ::LocalIndexFactory,
    globalIndexFactory = ::GlobalIndexFactory,
)

internal inline fun VariableInstructionFactory(
    instruction: VariableInstruction,
    localIndexFactory: IRFactory<Index.LocalIndex, IRLocalIndex>,
    globalIndexFactory: IRFactory<Index.GlobalIndex, IRGlobalIndex>,
): IRVariableInstruction {
    return when (instruction) {
        is VariableInstruction.LocalGet -> IRVariableInstruction.LocalGet(
            localIdx = localIndexFactory(instruction.localIdx),
        )

        is VariableInstruction.LocalSet -> IRVariableInstruction.LocalSet(
            localIdx = localIndexFactory(instruction.localIdx),
        )

        is VariableInstruction.LocalTee -> IRVariableInstruction.LocalTee(
            localIdx = localIndexFactory(instruction.localIdx),
        )

        is VariableInstruction.GlobalGet -> IRVariableInstruction.GlobalGet(
            globalIdx = globalIndexFactory(instruction.globalIdx),
        )

        is VariableInstruction.GlobalSet -> IRVariableInstruction.GlobalSet(
            globalIdx = globalIndexFactory(instruction.globalIdx),
        )
    }
}
