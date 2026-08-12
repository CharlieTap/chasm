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
import io.github.charlietap.chasm.validator.ext.pop
import io.github.charlietap.chasm.validator.ext.popMemoryAddress
import io.github.charlietap.chasm.validator.ext.push
import io.github.charlietap.chasm.validator.ext.size
import io.github.charlietap.chasm.validator.validator.index.MemoryIndexValidator
import io.github.charlietap.chasm.validator.validator.instruction.MemArgValidator
import io.github.charlietap.chasm.validator.validator.instruction.MemoryArgumentValidator

internal fun AtomicCompareExchangeInstructionValidator(
    context: ModuleValidationContext,
    instruction: AtomicMemoryInstruction.CompareExchange,
): Result<Unit, ModuleValidatorError> =
    AtomicCompareExchangeInstructionValidator(
        context = context,
        instruction = instruction,
        memArgValidator = ::MemArgValidator,
        memoryIndexValidator = ::MemoryIndexValidator,
    )

internal inline fun AtomicCompareExchangeInstructionValidator(
    context: ModuleValidationContext,
    instruction: AtomicMemoryInstruction.CompareExchange,
    crossinline memArgValidator: MemoryArgumentValidator,
    crossinline memoryIndexValidator: ModuleValidator<Index.MemoryIndex>,
): Result<Unit, ModuleValidatorError> = binding {

    memoryIndexValidator(context, instruction.memoryIndex).bind()
    memArgValidator(context, instruction.memArg, instruction.memoryIndex, instruction.size(), true).bind()

    val valueType = when (instruction) {
        is AtomicMemoryInstruction.CompareExchange.I32 -> ValueType.Number(NumberType.I32)
        is AtomicMemoryInstruction.CompareExchange.I64 -> ValueType.Number(NumberType.I64)
    }

    context.pop(valueType).bind()
    context.pop(valueType).bind()
    context.popMemoryAddress(instruction.memoryIndex).bind()
    context.push(valueType)
}
