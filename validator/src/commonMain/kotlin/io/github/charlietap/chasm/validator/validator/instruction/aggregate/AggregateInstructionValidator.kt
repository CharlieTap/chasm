package io.github.charlietap.chasm.validator.validator.instruction.aggregate

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun AggregateInstructionValidator(
    context: ValidationContext,
    instruction: AggregateInstruction,
): Result<Unit, ModuleValidatorError> =
    AggregateInstructionValidator(
        context = context,
        instruction = instruction,
        refI31Validator = ::RefI31InstructionValidator,
        anyConvertExternValidator = ::AnyConvertExternInstructionValidator,
        arrayCopyValidator = ::ArrayCopyInstructionValidator,
        arrayFillValidator = ::ArrayFillInstructionValidator,
        arrayGetValidator = ::ArrayGetInstructionValidator,
        arrayGetSignedValidator = ::ArrayGetSignedInstructionValidator,
        arrayGetUnsignedValidator = ::ArrayGetUnsignedInstructionValidator,
        arrayInitDataValidator = ::ArrayInitDataInstructionValidator,
        arrayInitElementValidator = ::ArrayInitElemInstructionValidator,
        arrayLenValidator = ::ArrayLenInstructionValidator,
        arrayNewValidator = ::ArrayNewInstructionValidator,
        arrayNewDataValidator = ::ArrayNewDataInstructionValidator,
        arrayNewDefaultValidator = ::ArrayNewDefaultInstructionValidator,
        arrayNewElementValidator = ::ArrayNewElementInstructionValidator,
        arrayNewFixedValidator = ::ArrayNewFixedInstructionValidator,
        arraySetValidator = ::ArraySetInstructionValidator,
        externConvertAnyValidator = ::ExternConvertAnyInstructionValidator,
        structGetValidator = ::StructGetInstructionValidator,
        structGetSignedValidator = ::StructGetSignedInstructionValidator,
        structGetUnsignedValidator = ::StructGetUnsignedInstructionValidator,
        structNewValidator = ::StructNewInstructionValidator,
        structNewDefaultValidator = ::StructNewDefaultInstructionValidator,
        structSetValidator = ::StructSetInstructionValidator,
        i31GetSignedValidator = ::I31GetSignedInstructionValidator,
        i31GetUnsignedValidator = ::I31GetUnsignedInstructionValidator,
    )

internal fun AggregateInstructionValidator(
    context: ValidationContext,
    instruction: AggregateInstruction,
    refI31Validator: Validator<AggregateInstruction.RefI31>,
    anyConvertExternValidator: Validator<AggregateInstruction.AnyConvertExtern>,
    arrayCopyValidator: Validator<AggregateInstruction.ArrayCopy>,
    arrayFillValidator: Validator<AggregateInstruction.ArrayFill>,
    arrayGetValidator: Validator<AggregateInstruction.ArrayGet>,
    arrayGetSignedValidator: Validator<AggregateInstruction.ArrayGetSigned>,
    arrayGetUnsignedValidator: Validator<AggregateInstruction.ArrayGetUnsigned>,
    arrayInitDataValidator: Validator<AggregateInstruction.ArrayInitData>,
    arrayInitElementValidator: Validator<AggregateInstruction.ArrayInitElement>,
    arrayLenValidator: Validator<AggregateInstruction.ArrayLen>,
    arrayNewValidator: Validator<AggregateInstruction.ArrayNew>,
    arrayNewDataValidator: Validator<AggregateInstruction.ArrayNewData>,
    arrayNewDefaultValidator: Validator<AggregateInstruction.ArrayNewDefault>,
    arrayNewElementValidator: Validator<AggregateInstruction.ArrayNewElement>,
    arrayNewFixedValidator: Validator<AggregateInstruction.ArrayNewFixed>,
    arraySetValidator: Validator<AggregateInstruction.ArraySet>,
    externConvertAnyValidator: Validator<AggregateInstruction.ExternConvertAny>,
    structGetValidator: Validator<AggregateInstruction.StructGet>,
    structGetSignedValidator: Validator<AggregateInstruction.StructGetSigned>,
    structGetUnsignedValidator: Validator<AggregateInstruction.StructGetUnsigned>,
    structNewValidator: Validator<AggregateInstruction.StructNew>,
    structNewDefaultValidator: Validator<AggregateInstruction.StructNewDefault>,
    structSetValidator: Validator<AggregateInstruction.StructSet>,
    i31GetSignedValidator: Validator<AggregateInstruction.I31GetSigned>,
    i31GetUnsignedValidator: Validator<AggregateInstruction.I31GetUnsigned>,
): Result<Unit, ModuleValidatorError> {
    return when (instruction) {
        is AggregateInstruction.AnyConvertExtern -> anyConvertExternValidator(context, instruction)
        is AggregateInstruction.ArrayCopy -> arrayCopyValidator(context, instruction)
        is AggregateInstruction.ArrayFill -> arrayFillValidator(context, instruction)
        is AggregateInstruction.ArrayGet -> arrayGetValidator(context, instruction)
        is AggregateInstruction.ArrayGetSigned -> arrayGetSignedValidator(context, instruction)
        is AggregateInstruction.ArrayGetUnsigned -> arrayGetUnsignedValidator(context, instruction)
        is AggregateInstruction.ArrayInitData -> arrayInitDataValidator(context, instruction)
        is AggregateInstruction.ArrayInitElement -> arrayInitElementValidator(context, instruction)
        is AggregateInstruction.ArrayLen -> arrayLenValidator(context, instruction)
        is AggregateInstruction.ArrayNew -> arrayNewValidator(context, instruction)
        is AggregateInstruction.ArrayNewData -> arrayNewDataValidator(context, instruction)
        is AggregateInstruction.ArrayNewDefault -> arrayNewDefaultValidator(context, instruction)
        is AggregateInstruction.ArrayNewElement -> arrayNewElementValidator(context, instruction)
        is AggregateInstruction.ArrayNewFixed -> arrayNewFixedValidator(context, instruction)
        is AggregateInstruction.ArraySet -> arraySetValidator(context, instruction)
        is AggregateInstruction.ExternConvertAny -> externConvertAnyValidator(context, instruction)
        is AggregateInstruction.I31GetSigned -> i31GetSignedValidator(context, instruction)
        is AggregateInstruction.I31GetUnsigned -> i31GetUnsignedValidator(context, instruction)
        is AggregateInstruction.RefI31 -> refI31Validator(context, instruction)
        is AggregateInstruction.StructGet -> structGetValidator(context, instruction)
        is AggregateInstruction.StructGetSigned -> structGetSignedValidator(context, instruction)
        is AggregateInstruction.StructGetUnsigned -> structGetUnsignedValidator(context, instruction)
        is AggregateInstruction.StructNew -> structNewValidator(context, instruction)
        is AggregateInstruction.StructNewDefault -> structNewDefaultValidator(context, instruction)
        is AggregateInstruction.StructSet -> structSetValidator(context, instruction)
    }
}
