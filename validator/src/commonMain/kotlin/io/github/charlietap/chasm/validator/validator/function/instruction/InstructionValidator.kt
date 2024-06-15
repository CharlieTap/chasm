package io.github.charlietap.chasm.validator.validator.function.instruction

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.instruction.VectorInstruction
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.function.instruction.memory.MemoryInstructionValidator

internal fun InstructionValidator(
    context: ValidationContext,
    instruction: Instruction,
): Result<Unit, ModuleValidatorError> =
    InstructionValidator(
        context = context,
        instruction = instruction,
        memoryInstructionValidator = ::MemoryInstructionValidator,
    )

internal fun InstructionValidator(
    context: ValidationContext,
    instruction: Instruction,
    memoryInstructionValidator: Validator<MemoryInstruction>,
): Result<Unit, ModuleValidatorError> {
    return when (instruction) {
        is AggregateInstruction -> Ok(Unit)
        is ControlInstruction -> Ok(Unit)
        is NumericInstruction -> Ok(Unit)
        is MemoryInstruction -> memoryInstructionValidator(context, instruction)
        is ParametricInstruction -> Ok(Unit)
        is ReferenceInstruction -> Ok(Unit)
        is TableInstruction -> Ok(Unit)
        is VariableInstruction -> Ok(Unit)
        is VectorInstruction -> Ok(Unit)
        else -> Err(InstructionValidatorError.UnknownInstruction)
    }
}
