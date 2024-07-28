package io.github.charlietap.chasm.validator.validator.instruction

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
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
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.resolver.InstructionTypeResolver
import io.github.charlietap.chasm.validator.resolver.InstructionTypeResolverImpl
import io.github.charlietap.chasm.validator.validator.instruction.aggregate.AggregateInstructionValidator
import io.github.charlietap.chasm.validator.validator.instruction.control.ControlInstructionValidator
import io.github.charlietap.chasm.validator.validator.instruction.memory.MemoryInstructionValidator
import io.github.charlietap.chasm.validator.validator.instruction.numeric.NumericInstructionValidator
import io.github.charlietap.chasm.validator.validator.instruction.parametric.ParametricInstructionValidator
import io.github.charlietap.chasm.validator.validator.instruction.reference.ReferenceInstructionValidator
import io.github.charlietap.chasm.validator.validator.instruction.table.TableInstructionValidator
import io.github.charlietap.chasm.validator.validator.instruction.variable.VariableInstructionValidator
import io.github.charlietap.chasm.validator.validator.type.InstructionTypeValidator

internal fun InstructionValidator(
    context: ValidationContext,
    instruction: Instruction,
): Result<Unit, ModuleValidatorError> =
    InstructionValidator(
        context = context,
        instruction = instruction,
        aggregateInstructionValidator = ::AggregateInstructionValidator,
        controlInstructionValidator = ::ControlInstructionValidator,
        memoryInstructionValidator = ::MemoryInstructionValidator,
        numericInstructionValidator = ::NumericInstructionValidator,
        parametricInstructionValidator = ::ParametricInstructionValidator,
        referenceInstructionValidator = ::ReferenceInstructionValidator,
        tableInstructionValidator = ::TableInstructionValidator,
        variableInstructionValidator = ::VariableInstructionValidator,
        instructionTypeResolver = ::InstructionTypeResolverImpl,
        instructionTypeValidator = ::InstructionTypeValidator,
    )

internal fun InstructionValidator(
    context: ValidationContext,
    instruction: Instruction,
    aggregateInstructionValidator: Validator<AggregateInstruction>,
    controlInstructionValidator: Validator<ControlInstruction>,
    memoryInstructionValidator: Validator<MemoryInstruction>,
    numericInstructionValidator: Validator<NumericInstruction>,
    parametricInstructionValidator: Validator<ParametricInstruction>,
    referenceInstructionValidator: Validator<ReferenceInstruction>,
    tableInstructionValidator: Validator<TableInstruction>,
    variableInstructionValidator: Validator<VariableInstruction>,
    instructionTypeResolver: InstructionTypeResolver<Instruction>,
    instructionTypeValidator: Validator<InstructionType>,
): Result<Unit, ModuleValidatorError> = binding {

    if (
        instruction is NumericInstruction
    ) {
        val type = instructionTypeResolver(context, instruction).bind()
        instructionTypeValidator(context, type).bind()
    }

    when (instruction) {
        is AggregateInstruction -> aggregateInstructionValidator(context, instruction).bind()
        is ControlInstruction -> controlInstructionValidator(context, instruction).bind()
        is NumericInstruction -> numericInstructionValidator(context, instruction).bind()
        is MemoryInstruction -> memoryInstructionValidator(context, instruction).bind()
        is ParametricInstruction -> parametricInstructionValidator(context, instruction).bind()
        is ReferenceInstruction -> referenceInstructionValidator(context, instruction).bind()
        is TableInstruction -> tableInstructionValidator(context, instruction).bind()
        is VariableInstruction -> variableInstructionValidator(context, instruction).bind()
        is VectorInstruction -> Unit
        else -> Err(InstructionValidatorError.UnknownInstruction).bind<Unit>()
    }
}
