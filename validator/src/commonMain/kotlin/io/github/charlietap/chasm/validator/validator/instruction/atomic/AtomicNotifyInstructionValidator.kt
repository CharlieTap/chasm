package io.github.charlietap.chasm.validator.validator.instruction.atomic

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.validator.ModuleValidator
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.popI32
import io.github.charlietap.chasm.validator.ext.popMemoryAddress
import io.github.charlietap.chasm.validator.ext.pushI32
import io.github.charlietap.chasm.validator.ext.size
import io.github.charlietap.chasm.validator.validator.index.MemoryIndexValidator
import io.github.charlietap.chasm.validator.validator.instruction.MemArgValidator
import io.github.charlietap.chasm.validator.validator.instruction.MemoryArgumentValidator

internal fun AtomicNotifyInstructionValidator(
    context: ModuleValidationContext,
    instruction: AtomicMemoryInstruction.Notify,
): Result<Unit, ModuleValidatorError> =
    AtomicNotifyInstructionValidator(
        context = context,
        instruction = instruction,
        memArgValidator = ::MemArgValidator,
        memoryIndexValidator = ::MemoryIndexValidator,
    )

internal inline fun AtomicNotifyInstructionValidator(
    context: ModuleValidationContext,
    instruction: AtomicMemoryInstruction.Notify,
    crossinline memArgValidator: MemoryArgumentValidator,
    crossinline memoryIndexValidator: ModuleValidator<Index.MemoryIndex>,
): Result<Unit, ModuleValidatorError> = binding {

    memoryIndexValidator(context, instruction.memoryIndex).bind()
    memArgValidator(context, instruction.memArg, instruction.memoryIndex, instruction.size(), true).bind()

    context.popI32().bind()
    context.popMemoryAddress(instruction.memoryIndex).bind()
    context.pushI32()
}
