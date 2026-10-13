package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.instruction.BLOCK
import io.github.charlietap.chasm.validator.validator.instruction.BR
import io.github.charlietap.chasm.validator.validator.instruction.BR_IF
import io.github.charlietap.chasm.validator.validator.instruction.BR_TABLE
import io.github.charlietap.chasm.validator.validator.instruction.CALL
import io.github.charlietap.chasm.validator.validator.instruction.CALL_INDIRECT
import io.github.charlietap.chasm.validator.validator.instruction.ELSE
import io.github.charlietap.chasm.validator.validator.instruction.END
import io.github.charlietap.chasm.validator.validator.instruction.IF
import io.github.charlietap.chasm.validator.validator.instruction.LOOP
import io.github.charlietap.chasm.validator.validator.instruction.NOP
import io.github.charlietap.chasm.validator.validator.instruction.RETURN
import io.github.charlietap.chasm.validator.validator.instruction.UNREACHABLE

internal fun ControlInstructionValidator(
    context: ModuleValidationContext,
    opcode: Int,
    instruction: Instruction,
): Result<Unit, ModuleValidatorError> = when (opcode) {
    UNREACHABLE -> UnreachableInstructionValidator(context, instruction as ControlInstruction.Unreachable)
    NOP -> Ok(Unit)
    BLOCK -> BlockInstructionValidator(context, instruction as ControlInstruction.Block)
    LOOP -> LoopInstructionValidator(context, instruction as ControlInstruction.Loop)
    IF -> IfInstructionValidator(context, instruction as ControlInstruction.If)
    ELSE -> ElseInstructionValidator(context, instruction as ControlInstruction.Else)
    END -> EndInstructionValidator(context, instruction as ControlInstruction.End)
    BR -> BreakInstructionValidator(context, instruction as ControlInstruction.Br)
    BR_IF -> BreakIfInstructionValidator(context, instruction as ControlInstruction.BrIf)
    BR_TABLE -> BreakTableInstructionValidator(context, instruction as ControlInstruction.BrTable)
    RETURN -> ReturnValidator(context, instruction as ControlInstruction.Return)
    CALL -> CallInstructionValidator(context, instruction as ControlInstruction.Call)
    CALL_INDIRECT -> CallIndirectValidator(context, instruction as ControlInstruction.CallIndirect)
    else -> ControlInstructionValidator(context, instruction as ControlInstruction)
}
