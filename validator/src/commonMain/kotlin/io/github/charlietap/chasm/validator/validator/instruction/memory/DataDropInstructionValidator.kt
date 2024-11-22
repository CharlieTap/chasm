package io.github.charlietap.chasm.validator.validator.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.index.DataIndexValidator

internal fun DataDropInstructionValidator(
    context: ValidationContext,
    instruction: MemoryInstruction.DataDrop,
): Result<Unit, ModuleValidatorError> =
    DataDropInstructionValidator(
        context = context,
        instruction = instruction,
        dataIndexValidator = ::DataIndexValidator,
    )

internal inline fun DataDropInstructionValidator(
    context: ValidationContext,
    instruction: MemoryInstruction.DataDrop,
    crossinline dataIndexValidator: Validator<Index.DataIndex>,
): Result<Unit, ModuleValidatorError> = binding {
    dataIndexValidator(context, instruction.dataIdx).bind()
}
