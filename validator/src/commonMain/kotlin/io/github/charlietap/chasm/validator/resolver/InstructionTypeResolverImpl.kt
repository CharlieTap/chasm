package io.github.charlietap.chasm.validator.resolver

import com.github.michaelbull.result.Err
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
import io.github.charlietap.chasm.ast.type.InstructionType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun InstructionTypeResolverImpl(
    context: ValidationContext,
    instruction: Instruction,
): Result<InstructionType, ModuleValidatorError> =
    InstructionTypeResolverImpl(
        context = context,
        instruction = instruction,
        numericInstructionTypeResolver = ::NumericInstructionTypeResolverImpl,
        memoryInstructionTypeResolver = ::MemoryInstructionTypeResolverImpl,
    )

internal fun InstructionTypeResolverImpl(
    context: ValidationContext,
    instruction: Instruction,
    numericInstructionTypeResolver: InstructionTypeResolver<NumericInstruction>,
    memoryInstructionTypeResolver: InstructionTypeResolver<MemoryInstruction>,
): Result<InstructionType, ModuleValidatorError> = when (instruction) {
    is AggregateInstruction -> Err(InstructionValidatorError.UnknownInstruction)
    is ControlInstruction -> Err(InstructionValidatorError.UnknownInstruction)
    is NumericInstruction -> numericInstructionTypeResolver(context, instruction)
    is MemoryInstruction -> memoryInstructionTypeResolver(context, instruction)
    is ParametricInstruction -> Err(InstructionValidatorError.UnknownInstruction)
    is ReferenceInstruction -> Err(InstructionValidatorError.UnknownInstruction)
    is TableInstruction -> Err(InstructionValidatorError.UnknownInstruction)
    is VariableInstruction -> Err(InstructionValidatorError.UnknownInstruction)
    is VectorInstruction -> Err(InstructionValidatorError.UnknownInstruction)
}
