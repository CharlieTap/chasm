package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ir.instruction.ParametricInstruction as IRParametricInstruction
import io.github.charlietap.chasm.ir.type.ValueType as IRValueType

internal fun ParametricInstructionFactory(
    instruction: ParametricInstruction,
): IRParametricInstruction = ParametricInstructionFactory(
    instruction = instruction,
    valueTypeFactory = ::ValueTypeFactory,
)

internal inline fun ParametricInstructionFactory(
    instruction: ParametricInstruction,
    valueTypeFactory: IRFactory<ValueType, IRValueType>,
): IRParametricInstruction {
    return when (instruction) {
        ParametricInstruction.Drop -> IRParametricInstruction.Drop

        ParametricInstruction.Select -> IRParametricInstruction.Select

        is ParametricInstruction.SelectWithType -> IRParametricInstruction.SelectWithType(
            types = instruction.types.map(valueTypeFactory),
        )
    }
}
