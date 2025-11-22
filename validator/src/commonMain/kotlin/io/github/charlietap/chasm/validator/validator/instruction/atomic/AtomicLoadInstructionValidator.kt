package io.github.charlietap.chasm.validator.validator.instruction.atomic

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.popMemoryAddress
import io.github.charlietap.chasm.validator.ext.push
import io.github.charlietap.chasm.validator.validator.index.MemoryIndexValidator
import io.github.charlietap.chasm.validator.validator.instruction.MemArgValidator

internal fun AtomicLoadInstructionValidator(
    context: ValidationContext,
    instruction: AtomicMemoryInstruction.Load,
): Result<Unit, ModuleValidatorError> =
    AtomicLoadInstructionValidator(
        context = context,
        instruction = instruction,
        memArgValidator = ::MemArgValidator,
        memoryIndexValidator = ::MemoryIndexValidator,
    )

internal inline fun AtomicLoadInstructionValidator(
    context: ValidationContext,
    instruction: AtomicMemoryInstruction.Load,
    crossinline memArgValidator: Validator<MemArg>,
    crossinline memoryIndexValidator: Validator<Index.MemoryIndex>,
): Result<Unit, ModuleValidatorError> = binding {

    memoryIndexValidator(context, instruction.memoryIndex).bind()
    memArgValidator(context, instruction.memArg).bind()

    context.popMemoryAddress(instruction.memoryIndex).bind()
    val valueType = when (instruction) {
        is AtomicMemoryInstruction.Load.I32 -> ValueType.Number(NumberType.I32)
        is AtomicMemoryInstruction.Load.I64 -> ValueType.Number(NumberType.I64)
    }
    context.push(valueType)
}
