package io.github.charlietap.chasm.validator.validator.instruction.atomic

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.popI32
import io.github.charlietap.chasm.validator.ext.popI64
import io.github.charlietap.chasm.validator.ext.popMemoryAddress
import io.github.charlietap.chasm.validator.ext.pushI32
import io.github.charlietap.chasm.validator.validator.index.MemoryIndexValidator
import io.github.charlietap.chasm.validator.validator.instruction.MemArgValidator

internal fun AtomicI32WaitInstructionValidator(
    context: ValidationContext,
    instruction: AtomicMemoryInstruction.I32Wait,
): Result<Unit, ModuleValidatorError> =
    AtomicWaitInstructionValidator(
        context = context,
        instruction = instruction,
        memArgValidator = ::MemArgValidator,
        memoryIndexValidator = ::MemoryIndexValidator,
    )

internal fun AtomicI64WaitInstructionValidator(
    context: ValidationContext,
    instruction: AtomicMemoryInstruction.I64Wait,
): Result<Unit, ModuleValidatorError> =
    AtomicWaitInstructionValidator(
        context = context,
        instruction = instruction,
        memArgValidator = ::MemArgValidator,
        memoryIndexValidator = ::MemoryIndexValidator,
    )

internal inline fun AtomicWaitInstructionValidator(
    context: ValidationContext,
    instruction: AtomicMemoryInstruction.I32Wait,
    crossinline memArgValidator: Validator<MemArg>,
    crossinline memoryIndexValidator: Validator<Index.MemoryIndex>,
): Result<Unit, ModuleValidatorError> = binding {

    memoryIndexValidator(context, instruction.memoryIndex).bind()
    memArgValidator(context, instruction.memArg).bind()

    context.popI64().bind()
    context.popI32().bind()
    context.popMemoryAddress(instruction.memoryIndex).bind()
    context.pushI32()
}

internal inline fun AtomicWaitInstructionValidator(
    context: ValidationContext,
    instruction: AtomicMemoryInstruction.I64Wait,
    crossinline memArgValidator: Validator<MemArg>,
    crossinline memoryIndexValidator: Validator<Index.MemoryIndex>,
): Result<Unit, ModuleValidatorError> = binding {

    memoryIndexValidator(context, instruction.memoryIndex).bind()
    memArgValidator(context, instruction.memArg).bind()

    context.popI64().bind()
    context.popI64().bind()
    context.popMemoryAddress(instruction.memoryIndex).bind()
    context.pushI32()
}
