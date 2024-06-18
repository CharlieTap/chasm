package io.github.charlietap.chasm.validator.validator.function.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun BreakInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.Br,
): Result<Unit, ModuleValidatorError> = binding {
    if (instruction.labelIndex.idx.toInt() !in context.functions.indices) {
        Err(InstructionValidatorError.UnknownFunction).bind<Unit>()
    }
}
