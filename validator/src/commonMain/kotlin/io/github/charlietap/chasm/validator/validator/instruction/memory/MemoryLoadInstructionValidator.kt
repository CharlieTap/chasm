package io.github.charlietap.chasm.validator.validator.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
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

internal fun MemoryLoadInstructionValidator(
    context: ValidationContext,
    instruction: MemoryInstruction.Load,
): Result<Unit, ModuleValidatorError> =
    MemoryLoadInstructionValidator(
        context = context,
        instruction = instruction,
        memArgValidator = ::MemArgValidator,
        memoryIndexValidator = ::MemoryIndexValidator,
    )

internal inline fun MemoryLoadInstructionValidator(
    context: ValidationContext,
    instruction: MemoryInstruction.Load,
    crossinline memArgValidator: Validator<MemArg>,
    crossinline memoryIndexValidator: Validator<Index.MemoryIndex>,
): Result<Unit, ModuleValidatorError> = binding {

    memArgValidator(context, instruction.memArg).bind()
    memoryIndexValidator(context, instruction.memoryIndex).bind()

    context.popMemoryAddress(instruction.memoryIndex).bind()

    val valueType = when (instruction) {
        is MemoryInstruction.Load.I32 -> ValueType.Number(NumberType.I32)
        is MemoryInstruction.Load.I64 -> ValueType.Number(NumberType.I64)
        is MemoryInstruction.Load.F32 -> ValueType.Number(NumberType.F32)
        is MemoryInstruction.Load.F64 -> ValueType.Number(NumberType.F64)
    }

    context.push(valueType)
}
