package io.github.charlietap.chasm.validator.validator.function.instruction.control

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
        breakTableValidator = ::BreakTableInstructionValidator,
        callValidator = ::CallInstructionValidator,
        callIndirectValidator = ::CallIndirectValidator,
        loopValidator = ::LoopInstructionValidator,
    )

internal fun ControlInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction,
    blockValidator: Validator<ControlInstruction.Block>,
    breakValidator: Validator<ControlInstruction.Br>,
    breakTableValidator: Validator<ControlInstruction.BrTable>,
    callValidator: Validator<ControlInstruction.Call>,
    callIndirectValidator: Validator<ControlInstruction.CallIndirect>,
    loopValidator: Validator<ControlInstruction.Loop>,
): Result<Unit, ModuleValidatorError> {
    return when (instruction) {
        is ControlInstruction.Block -> blockValidator(context, instruction)
        is ControlInstruction.Br -> breakValidator(context, instruction)
        is ControlInstruction.BrIf -> Ok(Unit)
        is ControlInstruction.BrOnCast -> Ok(Unit)
        is ControlInstruction.BrOnCastFail -> Ok(Unit)
        is ControlInstruction.BrOnNonNull -> Ok(Unit)
        is ControlInstruction.BrOnNull -> Ok(Unit)
        is ControlInstruction.BrTable -> breakTableValidator(context, instruction)
        is ControlInstruction.Call -> callValidator(context, instruction)
        is ControlInstruction.CallIndirect -> callIndirectValidator(context, instruction)
        is ControlInstruction.CallRef -> Ok(Unit) // label
        is ControlInstruction.If -> Ok(Unit)
        is ControlInstruction.Loop -> loopValidator(context, instruction)
        ControlInstruction.Nop -> Ok(Unit)
        ControlInstruction.Return -> Ok(Unit)
        is ControlInstruction.ReturnCall -> Ok(Unit) // label
        is ControlInstruction.ReturnCallIndirect -> Ok(Unit) // label
        is ControlInstruction.ReturnCallRef -> Ok(Unit) // label
        ControlInstruction.Unreachable -> Ok(Unit)
    }
}
