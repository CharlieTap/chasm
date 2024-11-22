package io.github.charlietap.chasm.validator.validator.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.popI32
import io.github.charlietap.chasm.validator.validator.index.MemoryIndexValidator

internal fun MemoryCopyInstructionValidator(
    context: ValidationContext,
    instruction: MemoryInstruction.MemoryCopy,
): Result<Unit, ModuleValidatorError> =
    MemoryCopyInstructionValidator(
        context = context,
        instruction = instruction,
        memoryIndexValidator = ::MemoryIndexValidator,
    )

internal inline fun MemoryCopyInstructionValidator(
    context: ValidationContext,
    instruction: MemoryInstruction.MemoryCopy,
    crossinline memoryIndexValidator: Validator<Index.MemoryIndex>,
): Result<Unit, ModuleValidatorError> = binding {

    memoryIndexValidator(context, instruction.srcIndex).bind()
    memoryIndexValidator(context, instruction.dstIndex).bind()

    repeat(3) {
        context.popI32().bind()
    }
}
