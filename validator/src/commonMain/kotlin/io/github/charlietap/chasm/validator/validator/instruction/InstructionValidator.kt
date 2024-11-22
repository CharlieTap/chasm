package io.github.charlietap.chasm.validator.validator.instruction

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
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
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.context.scope.InstructionScope
import io.github.charlietap.chasm.validator.context.scope.Scope
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

internal fun InstructionValidator(
    context: ValidationContext,
    instruction: Instruction,
): Result<Unit, ModuleValidatorError> =
    InstructionValidator(
        context = context,
        instruction = instruction,
        scope = ::InstructionScope,
        aggregateInstructionValidator = ::AggregateInstructionValidator,
        atomicMemoryInstructionValidator = ::AtomicMemoryInstructionValidator,
        controlInstructionValidator = ::ControlInstructionValidator,
        memoryInstructionValidator = ::MemoryInstructionValidator,
        numericInstructionValidator = ::NumericInstructionValidator,
        parametricInstructionValidator = ::ParametricInstructionValidator,
        referenceInstructionValidator = ::ReferenceInstructionValidator,
        tableInstructionValidator = ::TableInstructionValidator,
        variableInstructionValidator = ::VariableInstructionValidator,
    )

internal inline fun InstructionValidator(
    context: ValidationContext,
    instruction: Instruction,
    crossinline scope: Scope<Instruction>,
    crossinline aggregateInstructionValidator: Validator<AggregateInstruction>,
    crossinline atomicMemoryInstructionValidator: Validator<AtomicMemoryInstruction>,
    crossinline controlInstructionValidator: Validator<ControlInstruction>,
    crossinline memoryInstructionValidator: Validator<MemoryInstruction>,
    crossinline numericInstructionValidator: Validator<NumericInstruction>,
    crossinline parametricInstructionValidator: Validator<ParametricInstruction>,
    crossinline referenceInstructionValidator: Validator<ReferenceInstruction>,
    crossinline tableInstructionValidator: Validator<TableInstruction>,
    crossinline variableInstructionValidator: Validator<VariableInstruction>,
): Result<Unit, ModuleValidatorError> = binding {
    val scopedContext = scope(context, instruction).bind()
    when (instruction) {
        is AggregateInstruction -> aggregateInstructionValidator(scopedContext, instruction).bind()
        is AtomicMemoryInstruction -> atomicMemoryInstructionValidator(scopedContext, instruction).bind()
        is ControlInstruction -> controlInstructionValidator(scopedContext, instruction).bind()
        is NumericInstruction -> numericInstructionValidator(scopedContext, instruction).bind()
        is MemoryInstruction -> memoryInstructionValidator(scopedContext, instruction).bind()
        is ParametricInstruction -> parametricInstructionValidator(scopedContext, instruction).bind()
        is ReferenceInstruction -> referenceInstructionValidator(scopedContext, instruction).bind()
        is TableInstruction -> tableInstructionValidator(scopedContext, instruction).bind()
        is VariableInstruction -> variableInstructionValidator(scopedContext, instruction).bind()
        is VectorInstruction -> Unit
    }
}
