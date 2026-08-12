package io.github.charlietap.chasm.validator.validator.instruction

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.instruction.VectorInstruction
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.instruction.aggregate.AggregateInstructionValidator
import io.github.charlietap.chasm.validator.validator.instruction.atomic.AtomicMemoryInstructionValidator
import io.github.charlietap.chasm.validator.validator.instruction.control.ControlInstructionValidator
import io.github.charlietap.chasm.validator.validator.instruction.memory.MemoryInstructionValidator
import io.github.charlietap.chasm.validator.validator.instruction.numeric.NumericInstructionValidator
import io.github.charlietap.chasm.validator.validator.instruction.parametric.ParametricInstructionValidator
import io.github.charlietap.chasm.validator.validator.instruction.reference.ReferenceInstructionValidator
import io.github.charlietap.chasm.validator.validator.instruction.table.TableInstructionValidator
import io.github.charlietap.chasm.validator.validator.instruction.variable.VariableInstructionValidator
import io.github.charlietap.chasm.validator.validator.instruction.vector.VectorInstructionValidator

internal fun InstructionValidator(
    context: ModuleValidationContext,
    instruction: Instruction,
): Result<Unit, ModuleValidatorError> {
    return when (instruction) {
        is AggregateInstruction -> AggregateInstructionValidator(context, instruction)
        is AtomicMemoryInstruction -> AtomicMemoryInstructionValidator(context, instruction)
        is ControlInstruction -> ControlInstructionValidator(context, instruction)
        is NumericInstruction -> NumericInstructionValidator(context, instruction)
        is MemoryInstruction -> MemoryInstructionValidator(context, instruction)
        is ParametricInstruction -> ParametricInstructionValidator(context, instruction)
        is ReferenceInstruction -> ReferenceInstructionValidator(context, instruction)
        is TableInstruction -> TableInstructionValidator(context, instruction)
        is VariableInstruction -> VariableInstructionValidator(context, instruction)
        is VectorInstruction -> VectorInstructionValidator(context, instruction)
    }
}
