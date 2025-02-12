package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.VariableInstruction

internal typealias FusedDestinationFactory = (Instruction?) -> FusedDestination

internal fun FusedDestinationFactory(
    instruction: Instruction?,
): FusedDestination = when (instruction) {
    is VariableInstruction.LocalSet -> FusedDestination.LocalSet(instruction.localIdx)
    is VariableInstruction.GlobalSet -> FusedDestination.GlobalSet(instruction.globalIdx)
    else -> FusedDestination.ValueStack
}
