package io.github.charlietap.chasm.validator.validator.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.popI32
import io.github.charlietap.chasm.validator.validator.index.MemoryIndexValidator
import io.github.charlietap.chasm.validator.validator.instruction.MemArgValidator

internal fun I32Store16InstructionValidator(
    context: ValidationContext,
    instruction: MemoryInstruction.I32Store16,
): Result<Unit, ModuleValidatorError> =
    I32Store16InstructionValidator(
        context = context,
        instruction = instruction,
        memArgValidator = ::MemArgValidator,
        memoryIndexValidator = ::MemoryIndexValidator,
    )

internal inline fun I32Store16InstructionValidator(
    context: ValidationContext,
    instruction: MemoryInstruction.I32Store16,
    crossinline memArgValidator: Validator<MemArg>,
    crossinline memoryIndexValidator: Validator<Index.MemoryIndex>,
): Result<Unit, ModuleValidatorError> = binding {

    memArgValidator(context, instruction.memArg).bind()
    memoryIndexValidator(context, instruction.memoryIndex).bind()

    context.popI32().bind()
    context.popI32().bind()
}
