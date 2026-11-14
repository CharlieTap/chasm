package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.AggregateInstruction
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.cir.NumericInstruction
import io.github.charlietap.chasm.cir.ParametricInstruction
import io.github.charlietap.chasm.cir.ReferenceInstruction
import io.github.charlietap.chasm.cir.TableInstruction
import io.github.charlietap.chasm.cir.VariableInstruction

internal fun lowerInstruction(
    instruction: Instruction,
    context: LowerContext,
) {
    when (instruction) {
        is NumericInstruction -> lowerNumericInstruction(context)
        is MemoryInstruction -> lowerMemoryInstruction(instruction, context)
        is AggregateInstruction -> lowerAggregateInstruction(instruction, context)
        is ReferenceInstruction -> lowerReferenceInstruction(instruction, context)
        is VariableInstruction -> lowerVariableInstruction(instruction, context)
        is ParametricInstruction -> lowerParametricInstruction(instruction, context)
        is ControlInstruction -> lowerControlInstruction(instruction, context)
        is AdminInstruction -> lowerAdminInstruction(instruction, context)
        is TableInstruction -> context.lowerHelperCall(checkNotNull(context.forms.helperCall(context.position)))
    }
}
