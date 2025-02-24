package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ir.instruction.ParametricInstruction as IRParametricInstruction

internal inline fun ParametricInstructionFactory(
    instruction: ParametricInstruction,
): IRParametricInstruction {
    return when (instruction) {
        ParametricInstruction.Drop -> IRParametricInstruction.Drop

        ParametricInstruction.Select -> IRParametricInstruction.Select

        is ParametricInstruction.SelectWithType -> IRParametricInstruction.SelectWithType(
            types = instruction.types,
        )
    }
}
