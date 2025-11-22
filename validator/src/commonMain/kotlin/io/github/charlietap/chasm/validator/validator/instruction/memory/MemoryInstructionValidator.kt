package io.github.charlietap.chasm.validator.validator.instruction.memory

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun MemoryInstructionValidator(
    context: ValidationContext,
    instruction: MemoryInstruction,
): Result<Unit, ModuleValidatorError> =
    MemoryInstructionValidator(
        context = context,
        instruction = instruction,
        dataDropValidator = ::DataDropInstructionValidator,
        loadValidator = ::MemoryLoadInstructionValidator,
        storeValidator = ::MemoryStoreInstructionValidator,
        memoryCopyValidator = ::MemoryCopyInstructionValidator,
        memoryFillValidator = ::MemoryFillInstructionValidator,
        memoryGrowValidator = ::MemoryGrowInstructionValidator,
        memoryInitValidator = ::MemoryInitInstructionValidator,
        memorySizeValidator = ::MemorySizeInstructionValidator,
    )

internal inline fun MemoryInstructionValidator(
    context: ValidationContext,
    instruction: MemoryInstruction,
    crossinline dataDropValidator: Validator<MemoryInstruction.DataDrop>,
    crossinline loadValidator: Validator<MemoryInstruction.Load>,
    crossinline storeValidator: Validator<MemoryInstruction.Store>,
    crossinline memoryCopyValidator: Validator<MemoryInstruction.MemoryCopy>,
    crossinline memoryFillValidator: Validator<MemoryInstruction.MemoryFill>,
    crossinline memoryGrowValidator: Validator<MemoryInstruction.MemoryGrow>,
    crossinline memoryInitValidator: Validator<MemoryInstruction.MemoryInit>,
    crossinline memorySizeValidator: Validator<MemoryInstruction.MemorySize>,
): Result<Unit, ModuleValidatorError> {
    return when (instruction) {
        is MemoryInstruction.Load -> loadValidator(context, instruction)
        is MemoryInstruction.Store -> storeValidator(context, instruction)
        is MemoryInstruction.DataDrop -> dataDropValidator(context, instruction)
        is MemoryInstruction.MemoryInit -> memoryInitValidator(context, instruction)
        is MemoryInstruction.MemoryCopy -> memoryCopyValidator(context, instruction)
        is MemoryInstruction.MemoryFill -> memoryFillValidator(context, instruction)
        is MemoryInstruction.MemoryGrow -> memoryGrowValidator(context, instruction)
        is MemoryInstruction.MemorySize -> memorySizeValidator(context, instruction)
    }
}
