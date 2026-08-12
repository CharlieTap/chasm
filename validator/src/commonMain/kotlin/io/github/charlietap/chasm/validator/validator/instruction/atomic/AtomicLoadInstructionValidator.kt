package io.github.charlietap.chasm.validator.validator.instruction.atomic

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.ModuleValidator
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.popMemoryAddress
import io.github.charlietap.chasm.validator.ext.push
import io.github.charlietap.chasm.validator.ext.size
import io.github.charlietap.chasm.validator.validator.index.MemoryIndexValidator
import io.github.charlietap.chasm.validator.validator.instruction.MemArgValidator
import io.github.charlietap.chasm.validator.validator.instruction.MemoryArgumentValidator

internal fun AtomicLoadInstructionValidator(
    context: ModuleValidationContext,
    instruction: AtomicMemoryInstruction.Load,
): Result<Unit, ModuleValidatorError> =
    AtomicLoadInstructionValidator(
        context = context,
        instruction = instruction,
        memArgValidator = ::MemArgValidator,
        memoryIndexValidator = ::MemoryIndexValidator,
    )

internal inline fun AtomicLoadInstructionValidator(
    context: ModuleValidationContext,
    instruction: AtomicMemoryInstruction.Load,
    crossinline memArgValidator: MemoryArgumentValidator,
    crossinline memoryIndexValidator: ModuleValidator<Index.MemoryIndex>,
): Result<Unit, ModuleValidatorError> = binding {

    memoryIndexValidator(context, instruction.memoryIndex).bind()
    memArgValidator(context, instruction.memArg, instruction.memoryIndex, instruction.size(), true).bind()

    context.popMemoryAddress(instruction.memoryIndex).bind()
    val valueType = when (instruction) {
        is AtomicMemoryInstruction.Load.I32 -> ValueType.Number(NumberType.I32)
        is AtomicMemoryInstruction.Load.I64 -> ValueType.Number(NumberType.I64)
    }
    context.push(valueType)
}
