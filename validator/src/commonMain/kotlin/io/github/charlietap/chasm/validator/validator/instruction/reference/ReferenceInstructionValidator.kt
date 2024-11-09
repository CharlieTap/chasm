package io.github.charlietap.chasm.validator.validator.instruction.reference

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
        refIsNullValidator = ::RefIsNullInstructionValidator,
        refAsNonNullValidator = ::RefAsNonNullInstructionValidator,
        refCastValidator = ::RefCastInstructionValidator,
        refEqValidator = ::RefEqInstructionValidator,
        refNullValidator = ::RefNullInstructionValidator,
        refTestValidator = ::RefTestInstructionValidator,
    )

internal inline fun ReferenceInstructionValidator(
    context: ValidationContext,
    instruction: ReferenceInstruction,
    crossinline refFuncValidator: Validator<ReferenceInstruction.RefFunc>,
    crossinline refIsNullValidator: Validator<ReferenceInstruction.RefIsNull>,
    crossinline refAsNonNullValidator: Validator<ReferenceInstruction.RefAsNonNull>,
    crossinline refCastValidator: Validator<ReferenceInstruction.RefCast>,
    crossinline refEqValidator: Validator<ReferenceInstruction.RefEq>,
    crossinline refNullValidator: Validator<ReferenceInstruction.RefNull>,
    crossinline refTestValidator: Validator<ReferenceInstruction.RefTest>,
): Result<Unit, ModuleValidatorError> {
    return when (instruction) {
        is ReferenceInstruction.RefFunc -> refFuncValidator(context, instruction)
        is ReferenceInstruction.RefAsNonNull -> refAsNonNullValidator(context, instruction)
        is ReferenceInstruction.RefCast -> refCastValidator(context, instruction)
        is ReferenceInstruction.RefEq -> refEqValidator(context, instruction)
        is ReferenceInstruction.RefIsNull -> refIsNullValidator(context, instruction)
        is ReferenceInstruction.RefNull -> refNullValidator(context, instruction)
        is ReferenceInstruction.RefTest -> refTestValidator(context, instruction)
    }
}
