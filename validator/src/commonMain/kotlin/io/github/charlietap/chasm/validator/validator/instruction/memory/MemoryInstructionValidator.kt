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
        i32Load8SValidator = ::I32Load8SInstructionValidator,
        i32Load8UValidator = ::I32Load8UInstructionValidator,
        i32Load16SValidator = ::I32Load16SInstructionValidator,
        i32Load16UValidator = ::I32Load16UInstructionValidator,
        i32LoadValidator = ::I32LoadInstructionValidator,
        i64Load8SValidator = ::I64Load8SInstructionValidator,
        i64Load8UValidator = ::I64Load8UInstructionValidator,
        i64Load16SValidator = ::I64Load16SInstructionValidator,
        i64Load16UValidator = ::I64Load16UInstructionValidator,
        i64Load32SValidator = ::I64Load32SInstructionValidator,
        i64Load32UValidator = ::I64Load32UInstructionValidator,
        i64LoadValidator = ::I64LoadInstructionValidator,
        f32LoadValidator = ::F32LoadInstructionValidator,
        f64LoadValidator = ::F64LoadInstructionValidator,
        i32Store8Validator = ::I32Store8InstructionValidator,
        i32Store16Validator = ::I32Store16InstructionValidator,
        i32StoreValidator = ::I32StoreInstructionValidator,
        i64Store8Validator = ::I64Store8InstructionValidator,
        i64Store16Validator = ::I64Store16InstructionValidator,
        i64Store32Validator = ::I64Store32InstructionValidator,
        i64StoreValidator = ::I64StoreInstructionValidator,
        f32StoreValidator = ::F32StoreInstructionValidator,
        f64StoreValidator = ::F64StoreInstructionValidator,
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
    crossinline i32Load8SValidator: Validator<MemoryInstruction.Load.I32Load8S>,
    crossinline i32Load8UValidator: Validator<MemoryInstruction.Load.I32Load8U>,
    crossinline i32Load16SValidator: Validator<MemoryInstruction.Load.I32Load16S>,
    crossinline i32Load16UValidator: Validator<MemoryInstruction.Load.I32Load16U>,
    crossinline i32LoadValidator: Validator<MemoryInstruction.Load.I32Load>,
    crossinline i64Load8SValidator: Validator<MemoryInstruction.Load.I64Load8S>,
    crossinline i64Load8UValidator: Validator<MemoryInstruction.Load.I64Load8U>,
    crossinline i64Load16SValidator: Validator<MemoryInstruction.Load.I64Load16S>,
    crossinline i64Load16UValidator: Validator<MemoryInstruction.Load.I64Load16U>,
    crossinline i64Load32SValidator: Validator<MemoryInstruction.Load.I64Load32S>,
    crossinline i64Load32UValidator: Validator<MemoryInstruction.Load.I64Load32U>,
    crossinline i64LoadValidator: Validator<MemoryInstruction.Load.I64Load>,
    crossinline f32LoadValidator: Validator<MemoryInstruction.Load.F32Load>,
    crossinline f64LoadValidator: Validator<MemoryInstruction.Load.F64Load>,
    crossinline i32Store8Validator: Validator<MemoryInstruction.Store.I32Store8>,
    crossinline i32Store16Validator: Validator<MemoryInstruction.Store.I32Store16>,
    crossinline i32StoreValidator: Validator<MemoryInstruction.Store.I32Store>,
    crossinline i64Store8Validator: Validator<MemoryInstruction.Store.I64Store8>,
    crossinline i64Store32Validator: Validator<MemoryInstruction.Store.I64Store32>,
    crossinline i64Store16Validator: Validator<MemoryInstruction.Store.I64Store16>,
    crossinline i64StoreValidator: Validator<MemoryInstruction.Store.I64Store>,
    crossinline f32StoreValidator: Validator<MemoryInstruction.Store.F32Store>,
    crossinline f64StoreValidator: Validator<MemoryInstruction.Store.F64Store>,
    crossinline memoryCopyValidator: Validator<MemoryInstruction.MemoryCopy>,
    crossinline memoryFillValidator: Validator<MemoryInstruction.MemoryFill>,
    crossinline memoryGrowValidator: Validator<MemoryInstruction.MemoryGrow>,
    crossinline memoryInitValidator: Validator<MemoryInstruction.MemoryInit>,
    crossinline memorySizeValidator: Validator<MemoryInstruction.MemorySize>,
): Result<Unit, ModuleValidatorError> {
    return when (instruction) {
        is MemoryInstruction.Load.I32Load8S -> i32Load8SValidator(context, instruction)
        is MemoryInstruction.Load.I32Load8U -> i32Load8UValidator(context, instruction)
        is MemoryInstruction.Load.I32Load16S -> i32Load16SValidator(context, instruction)
        is MemoryInstruction.Load.I32Load16U -> i32Load16UValidator(context, instruction)
        is MemoryInstruction.Load.I32Load -> i32LoadValidator(context, instruction)
        is MemoryInstruction.Load.I64Load8S -> i64Load8SValidator(context, instruction)
        is MemoryInstruction.Load.I64Load8U -> i64Load8UValidator(context, instruction)
        is MemoryInstruction.Load.I64Load16S -> i64Load16SValidator(context, instruction)
        is MemoryInstruction.Load.I64Load16U -> i64Load16UValidator(context, instruction)
        is MemoryInstruction.Load.I64Load32S -> i64Load32SValidator(context, instruction)
        is MemoryInstruction.Load.I64Load32U -> i64Load32UValidator(context, instruction)
        is MemoryInstruction.Load.I64Load -> i64LoadValidator(context, instruction)
        is MemoryInstruction.Load.F32Load -> f32LoadValidator(context, instruction)
        is MemoryInstruction.Load.F64Load -> f64LoadValidator(context, instruction)
        is MemoryInstruction.Store.I32Store8 -> i32Store8Validator(context, instruction)
        is MemoryInstruction.Store.I32Store16 -> i32Store16Validator(context, instruction)
        is MemoryInstruction.Store.I32Store -> i32StoreValidator(context, instruction)
        is MemoryInstruction.Store.I64Store8 -> i64Store8Validator(context, instruction)
        is MemoryInstruction.Store.I64Store16 -> i64Store16Validator(context, instruction)
        is MemoryInstruction.Store.I64Store32 -> i64Store32Validator(context, instruction)
        is MemoryInstruction.Store.I64Store -> i64StoreValidator(context, instruction)
        is MemoryInstruction.Store.F32Store -> f32StoreValidator(context, instruction)
        is MemoryInstruction.Store.F64Store -> f64StoreValidator(context, instruction)
        is MemoryInstruction.DataDrop -> dataDropValidator(context, instruction)
        is MemoryInstruction.MemoryInit -> memoryInitValidator(context, instruction)
        is MemoryInstruction.MemoryCopy -> memoryCopyValidator(context, instruction)
        is MemoryInstruction.MemoryFill -> memoryFillValidator(context, instruction)
        is MemoryInstruction.MemoryGrow -> memoryGrowValidator(context, instruction)
        is MemoryInstruction.MemorySize -> memorySizeValidator(context, instruction)
    }
}
