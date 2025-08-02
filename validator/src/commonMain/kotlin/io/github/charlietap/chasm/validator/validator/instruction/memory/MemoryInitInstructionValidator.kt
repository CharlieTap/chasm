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
import io.github.charlietap.chasm.validator.validator.index.DataIndexValidator
import io.github.charlietap.chasm.validator.validator.index.MemoryIndexValidator

internal fun MemoryInitInstructionValidator(
    context: ValidationContext,
    instruction: MemoryInstruction.MemoryInit,
): Result<Unit, ModuleValidatorError> =
    MemoryInitInstructionValidator(
        context = context,
        instruction = instruction,
        dataIndexValidator = ::DataIndexValidator,
        memoryIndexValidator = ::MemoryIndexValidator,
    )

internal inline fun MemoryInitInstructionValidator(
    context: ValidationContext,
    instruction: MemoryInstruction.MemoryInit,
    crossinline dataIndexValidator: Validator<Index.DataIndex>,
    crossinline memoryIndexValidator: Validator<Index.MemoryIndex>,
): Result<Unit, ModuleValidatorError> = binding {

    dataIndexValidator(context, instruction.dataIndex).bind()
    memoryIndexValidator(context, instruction.memoryIndex).bind()

    context.popI32().bind()
    context.popI32().bind()
    context.popMemoryAddress(instruction.memoryIndex).bind()
}
