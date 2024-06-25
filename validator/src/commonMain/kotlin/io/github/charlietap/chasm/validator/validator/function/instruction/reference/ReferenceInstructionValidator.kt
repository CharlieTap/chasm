package io.github.charlietap.chasm.validator.validator.function.instruction.reference

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun ReferenceInstructionValidator(
    context: ValidationContext,
    instruction: ReferenceInstruction,
): Result<Unit, ModuleValidatorError> =
    ReferenceInstructionValidator(
        context = context,
        instruction = instruction,
        refFuncValidator = ::RefFuncInstructionValidator,
    )

internal fun ReferenceInstructionValidator(
    context: ValidationContext,
    instruction: ReferenceInstruction,
    refFuncValidator: Validator<ReferenceInstruction.RefFunc>,
): Result<Unit, ModuleValidatorError> {
    return when (instruction) {
        is ReferenceInstruction.RefFunc -> refFuncValidator(context, instruction)
        is ReferenceInstruction.RefAsNonNull -> Ok(Unit)
        is ReferenceInstruction.RefCast -> Ok(Unit)
        is ReferenceInstruction.RefEq -> Ok(Unit)
        is ReferenceInstruction.RefIsNull -> Ok(Unit)
        is ReferenceInstruction.RefNull -> Ok(Unit)
        is ReferenceInstruction.RefTest -> Ok(Unit)
    }
}
