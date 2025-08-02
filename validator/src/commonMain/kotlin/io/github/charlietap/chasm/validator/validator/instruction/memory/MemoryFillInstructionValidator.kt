package io.github.charlietap.chasm.validator.validator.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.popI32
import io.github.charlietap.chasm.validator.ext.popMemoryAddress
import io.github.charlietap.chasm.validator.validator.index.MemoryIndexValidator

internal fun MemoryFillInstructionValidator(
    context: ValidationContext,
    instruction: MemoryInstruction.MemoryFill,
): Result<Unit, ModuleValidatorError> =
    MemoryFillInstructionValidator(
        context = context,
        instruction = instruction,
        memoryIndexValidator = ::MemoryIndexValidator,
    )

internal inline fun MemoryFillInstructionValidator(
    context: ValidationContext,
    instruction: MemoryInstruction.MemoryFill,
    crossinline memoryIndexValidator: Validator<Index.MemoryIndex>,
): Result<Unit, ModuleValidatorError> = binding {

    memoryIndexValidator(context, instruction.memoryIndex).bind()

    context.popMemoryAddress(instruction.memoryIndex).bind()
    context.popI32().bind()
    context.popMemoryAddress(instruction.memoryIndex).bind()
}
