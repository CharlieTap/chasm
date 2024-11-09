package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun ControlInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction,
): Result<Unit, ModuleValidatorError> =
    ControlInstructionValidator(
        context = context,
        instruction = instruction,
        blockValidator = ::BlockInstructionValidator,
        breakValidator = ::BreakInstructionValidator,
        breakIfValidator = ::BreakIfInstructionValidator,
        breakOnCastValidator = ::BreakOnCastInstructionValidator,
        breakOnCastFailValidator = ::BreakOnCastFailInstructionValidator,
        breakOnNonNullValidator = ::BreakOnNonNullInstructionValidator,
        breakOnNullValidator = ::BreakOnNullInstructionValidator,
        breakTableValidator = ::BreakTableInstructionValidator,
        callValidator = ::CallInstructionValidator,
        callIndirectValidator = ::CallIndirectValidator,
        callRefValidator = ::CallRefInstructionValidator,
        ifValidator = ::IfInstructionValidator,
        throwValidator = ::ThrowInstructionValidator,
        throwRefValidator = ::ThrowRefInstructionValidator,
        tryTableValidator = ::TryTableInstructionValidator,
        returnValidator = ::ReturnValidator,
        returnCallValidator = ::ReturnCallInstructionValidator,
        returnCallIndirectValidator = ::ReturnCallIndirectInstructionValidator,
        returnCallRefValidator = ::ReturnCallRefInstructionValidator,
        loopValidator = ::LoopInstructionValidator,
        unreachableValidator = ::UnreachableInstructionValidator,
    )

internal inline fun ControlInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction,
    crossinline blockValidator: Validator<ControlInstruction.Block>,
    crossinline breakValidator: Validator<ControlInstruction.Br>,
    crossinline breakIfValidator: Validator<ControlInstruction.BrIf>,
    crossinline breakOnCastValidator: Validator<ControlInstruction.BrOnCast>,
    crossinline breakOnCastFailValidator: Validator<ControlInstruction.BrOnCastFail>,
    crossinline breakOnNullValidator: Validator<ControlInstruction.BrOnNull>,
    crossinline breakOnNonNullValidator: Validator<ControlInstruction.BrOnNonNull>,
    crossinline breakTableValidator: Validator<ControlInstruction.BrTable>,
    crossinline callValidator: Validator<ControlInstruction.Call>,
    crossinline callIndirectValidator: Validator<ControlInstruction.CallIndirect>,
    crossinline callRefValidator: Validator<ControlInstruction.CallRef>,
    crossinline ifValidator: Validator<ControlInstruction.If>,
    crossinline throwValidator: Validator<ControlInstruction.Throw>,
    crossinline throwRefValidator: Validator<ControlInstruction.ThrowRef>,
    crossinline tryTableValidator: Validator<ControlInstruction.TryTable>,
    crossinline returnValidator: Validator<ControlInstruction.Return>,
    crossinline returnCallValidator: Validator<ControlInstruction.ReturnCall>,
    crossinline returnCallIndirectValidator: Validator<ControlInstruction.ReturnCallIndirect>,
    crossinline returnCallRefValidator: Validator<ControlInstruction.ReturnCallRef>,
    crossinline loopValidator: Validator<ControlInstruction.Loop>,
    crossinline unreachableValidator: Validator<ControlInstruction.Unreachable>,
): Result<Unit, ModuleValidatorError> {
    return when (instruction) {
        is ControlInstruction.Block -> blockValidator(context, instruction)
        is ControlInstruction.Br -> breakValidator(context, instruction)
        is ControlInstruction.BrIf -> breakIfValidator(context, instruction)
        is ControlInstruction.BrOnCast -> breakOnCastValidator(context, instruction)
        is ControlInstruction.BrOnCastFail -> breakOnCastFailValidator(context, instruction)
        is ControlInstruction.BrOnNonNull -> breakOnNonNullValidator(context, instruction)
        is ControlInstruction.BrOnNull -> breakOnNullValidator(context, instruction)
        is ControlInstruction.BrTable -> breakTableValidator(context, instruction)
        is ControlInstruction.Call -> callValidator(context, instruction)
        is ControlInstruction.CallIndirect -> callIndirectValidator(context, instruction)
        is ControlInstruction.CallRef -> callRefValidator(context, instruction)
        is ControlInstruction.If -> ifValidator(context, instruction)
        is ControlInstruction.Loop -> loopValidator(context, instruction)
        is ControlInstruction.Nop -> Ok(Unit)
        is ControlInstruction.Return -> returnValidator(context, instruction)
        is ControlInstruction.ReturnCall -> returnCallValidator(context, instruction)
        is ControlInstruction.ReturnCallIndirect -> returnCallIndirectValidator(context, instruction)
        is ControlInstruction.ReturnCallRef -> returnCallRefValidator(context, instruction)
        is ControlInstruction.Unreachable -> unreachableValidator(context, instruction)
        is ControlInstruction.Throw -> throwValidator(context, instruction)
        is ControlInstruction.ThrowRef -> throwRefValidator(context, instruction)
        is ControlInstruction.TryTable -> tryTableValidator(context, instruction)
    }
}
