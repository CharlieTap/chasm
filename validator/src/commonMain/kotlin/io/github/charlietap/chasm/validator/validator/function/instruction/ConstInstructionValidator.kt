package io.github.charlietap.chasm.validator.validator.function.instruction

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun ConstInstructionValidator(
    context: ValidationContext,
    instruction: Instruction,
): Result<Unit, ModuleValidatorError> =
    ConstInstructionValidator(
        context = context,
        instruction = instruction,
        instructionValidator = ::InstructionValidator,
    )

internal fun ConstInstructionValidator(
    context: ValidationContext,
    instruction: Instruction,
    instructionValidator: Validator<Instruction>,
): Result<Unit, ModuleValidatorError> = binding {
    when (instruction) {
        is NumericInstruction.I32Const,
        is NumericInstruction.I64Const,
        is NumericInstruction.F32Const,
        is NumericInstruction.F64Const,
        is VariableInstruction.GlobalGet,
        is ReferenceInstruction.RefNull,
        is ReferenceInstruction.RefFunc,
        -> instructionValidator(context, instruction).bind()
        else -> Err(InstructionValidatorError.ConstInstructionExpected).bind<Unit>()
    }
}
