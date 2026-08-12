package io.github.charlietap.chasm.validator.validator.instruction.atomic

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.validator.ModuleValidator
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.popI32
import io.github.charlietap.chasm.validator.ext.popI64
import io.github.charlietap.chasm.validator.ext.popMemoryAddress
import io.github.charlietap.chasm.validator.ext.pushI32
import io.github.charlietap.chasm.validator.ext.size
import io.github.charlietap.chasm.validator.validator.index.MemoryIndexValidator
import io.github.charlietap.chasm.validator.validator.instruction.MemArgValidator
import io.github.charlietap.chasm.validator.validator.instruction.MemoryArgumentValidator

internal fun AtomicI32WaitInstructionValidator(
    context: ModuleValidationContext,
    instruction: AtomicMemoryInstruction.I32Wait,
): Result<Unit, ModuleValidatorError> =
    AtomicWaitInstructionValidator(
        context = context,
        instruction = instruction,
        memArgValidator = ::MemArgValidator,
        memoryIndexValidator = ::MemoryIndexValidator,
    )

internal fun AtomicI64WaitInstructionValidator(
    context: ModuleValidationContext,
    instruction: AtomicMemoryInstruction.I64Wait,
): Result<Unit, ModuleValidatorError> =
    AtomicWaitInstructionValidator(
        context = context,
        instruction = instruction,
        memArgValidator = ::MemArgValidator,
        memoryIndexValidator = ::MemoryIndexValidator,
    )

internal inline fun AtomicWaitInstructionValidator(
    context: ModuleValidationContext,
    instruction: AtomicMemoryInstruction.I32Wait,
    crossinline memArgValidator: MemoryArgumentValidator,
    crossinline memoryIndexValidator: ModuleValidator<Index.MemoryIndex>,
): Result<Unit, ModuleValidatorError> = binding {

    memoryIndexValidator(context, instruction.memoryIndex).bind()
    memArgValidator(context, instruction.memArg, instruction.memoryIndex, instruction.size(), true).bind()

    context.popI64().bind()
    context.popI32().bind()
    context.popMemoryAddress(instruction.memoryIndex).bind()
    context.pushI32()
}

internal inline fun AtomicWaitInstructionValidator(
    context: ModuleValidationContext,
    instruction: AtomicMemoryInstruction.I64Wait,
    crossinline memArgValidator: MemoryArgumentValidator,
    crossinline memoryIndexValidator: ModuleValidator<Index.MemoryIndex>,
): Result<Unit, ModuleValidatorError> = binding {

    memoryIndexValidator(context, instruction.memoryIndex).bind()
    memArgValidator(context, instruction.memArg, instruction.memoryIndex, instruction.size(), true).bind()

    context.popI64().bind()
    context.popI64().bind()
    context.popMemoryAddress(instruction.memoryIndex).bind()
    context.pushI32()
}
