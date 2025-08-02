package io.github.charlietap.chasm.validator.validator.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.pushMemoryAddress
import io.github.charlietap.chasm.validator.validator.index.MemoryIndexValidator

internal fun MemorySizeInstructionValidator(
    context: ValidationContext,
    instruction: MemoryInstruction.MemorySize,
): Result<Unit, ModuleValidatorError> =
    MemorySizeInstructionValidator(
        context = context,
        instruction = instruction,
        memoryIndexValidator = ::MemoryIndexValidator,
    )

internal inline fun MemorySizeInstructionValidator(
    context: ValidationContext,
    instruction: MemoryInstruction.MemorySize,
    crossinline memoryIndexValidator: Validator<Index.MemoryIndex>,
): Result<Unit, ModuleValidatorError> = binding {

    memoryIndexValidator(context, instruction.memoryIndex).bind()

    context.pushMemoryAddress(instruction.memoryIndex).bind()
}
