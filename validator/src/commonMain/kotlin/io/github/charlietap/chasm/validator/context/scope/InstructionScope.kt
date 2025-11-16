package io.github.charlietap.chasm.validator.context.scope

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun InstructionScope(
    context: ValidationContext,
    instruction: Instruction,
    block: (ValidationContext) -> Result<Unit, ModuleValidatorError>,
): Result<Unit, ModuleValidatorError> {
    context.instructionContext.instruction = instruction
    val result = block(context)
    context.instructionContext.instruction = null
    return result
}
