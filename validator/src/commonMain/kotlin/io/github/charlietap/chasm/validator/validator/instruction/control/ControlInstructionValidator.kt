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

internal fun ControlInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction,
    blockValidator: Validator<ControlInstruction.Block>,
    breakValidator: Validator<ControlInstruction.Br>,
    breakIfValidator: Validator<ControlInstruction.BrIf>,
    breakOnCastValidator: Validator<ControlInstruction.BrOnCast>,
    breakOnCastFailValidator: Validator<ControlInstruction.BrOnCastFail>,
    breakOnNullValidator: Validator<ControlInstruction.BrOnNull>,
    breakOnNonNullValidator: Validator<ControlInstruction.BrOnNonNull>,
    breakTableValidator: Validator<ControlInstruction.BrTable>,
    callValidator: Validator<ControlInstruction.Call>,
    callIndirectValidator: Validator<ControlInstruction.CallIndirect>,
    callRefValidator: Validator<ControlInstruction.CallRef>,
    ifValidator: Validator<ControlInstruction.If>,
    throwValidator: Validator<ControlInstruction.Throw>,
    throwRefValidator: Validator<ControlInstruction.ThrowRef>,
    tryTableValidator: Validator<ControlInstruction.TryTable>,
    returnValidator: Validator<ControlInstruction.Return>,
    returnCallValidator: Validator<ControlInstruction.ReturnCall>,
    returnCallIndirectValidator: Validator<ControlInstruction.ReturnCallIndirect>,
    returnCallRefValidator: Validator<ControlInstruction.ReturnCallRef>,
    loopValidator: Validator<ControlInstruction.Loop>,
    unreachableValidator: Validator<ControlInstruction.Unreachable>,
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
