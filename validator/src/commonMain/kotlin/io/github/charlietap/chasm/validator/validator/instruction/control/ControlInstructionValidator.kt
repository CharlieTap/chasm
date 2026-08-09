package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.validator.ModuleValidator
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun ControlInstructionValidator(
    context: ModuleValidationContext,
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
        elseValidator = ::ElseInstructionValidator,
        endValidator = ::EndInstructionValidator,
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
    context: ModuleValidationContext,
    instruction: ControlInstruction,
    crossinline blockValidator: ModuleValidator<ControlInstruction.Block>,
    crossinline breakValidator: ModuleValidator<ControlInstruction.Br>,
    crossinline breakIfValidator: ModuleValidator<ControlInstruction.BrIf>,
    crossinline breakOnCastValidator: ModuleValidator<ControlInstruction.BrOnCast>,
    crossinline breakOnCastFailValidator: ModuleValidator<ControlInstruction.BrOnCastFail>,
    crossinline breakOnNullValidator: ModuleValidator<ControlInstruction.BrOnNull>,
    crossinline breakOnNonNullValidator: ModuleValidator<ControlInstruction.BrOnNonNull>,
    crossinline breakTableValidator: ModuleValidator<ControlInstruction.BrTable>,
    crossinline callValidator: ModuleValidator<ControlInstruction.Call>,
    crossinline callIndirectValidator: ModuleValidator<ControlInstruction.CallIndirect>,
    crossinline callRefValidator: ModuleValidator<ControlInstruction.CallRef>,
    crossinline elseValidator: ModuleValidator<ControlInstruction.Else>,
    crossinline endValidator: ModuleValidator<ControlInstruction.End>,
    crossinline ifValidator: ModuleValidator<ControlInstruction.If>,
    crossinline throwValidator: ModuleValidator<ControlInstruction.Throw>,
    crossinline throwRefValidator: ModuleValidator<ControlInstruction.ThrowRef>,
    crossinline tryTableValidator: ModuleValidator<ControlInstruction.TryTable>,
    crossinline returnValidator: ModuleValidator<ControlInstruction.Return>,
    crossinline returnCallValidator: ModuleValidator<ControlInstruction.ReturnCall>,
    crossinline returnCallIndirectValidator: ModuleValidator<ControlInstruction.ReturnCallIndirect>,
    crossinline returnCallRefValidator: ModuleValidator<ControlInstruction.ReturnCallRef>,
    crossinline loopValidator: ModuleValidator<ControlInstruction.Loop>,
    crossinline unreachableValidator: ModuleValidator<ControlInstruction.Unreachable>,
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
        is ControlInstruction.Else -> elseValidator(context, instruction)
        is ControlInstruction.End -> endValidator(context, instruction)
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
