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
import io.github.charlietap.chasm.validator.ext.pop
import io.github.charlietap.chasm.validator.ext.popI32
import io.github.charlietap.chasm.validator.ext.popI64
import io.github.charlietap.chasm.validator.ext.push
import io.github.charlietap.chasm.validator.ext.pushI32
import io.github.charlietap.chasm.validator.validator.index.MemoryIndexValidator
import io.github.charlietap.chasm.validator.validator.instruction.MemArgValidator

internal fun AtomicMemoryInstructionValidator(
    context: ValidationContext,
    instruction: AtomicMemoryInstruction,
): Result<Unit, ModuleValidatorError> =
    AtomicMemoryInstructionValidator(
        context = context,
        instruction = instruction,
        memArgValidator = ::MemArgValidator,
        memoryIndexValidator = ::MemoryIndexValidator,
    )

internal inline fun AtomicMemoryInstructionValidator(
    context: ValidationContext,
    instruction: AtomicMemoryInstruction,
    crossinline memArgValidator: Validator<MemArg>,
    crossinline memoryIndexValidator: Validator<Index.MemoryIndex>,
): Result<Unit, ModuleValidatorError> = binding {
    when (instruction) {
        AtomicMemoryInstruction.Fence -> Unit
        is AtomicMemoryInstruction.Notify -> {

            memoryIndexValidator(context, instruction.memoryIndex).bind()
            memArgValidator(context, instruction.memArg).bind()

            context.popI32().bind()
            context.popI32().bind()

            context.pushI32()
        }
        is AtomicMemoryInstruction.I32Wait -> {

            memoryIndexValidator(context, instruction.memoryIndex).bind()
            memArgValidator(context, instruction.memArg).bind()

            context.popI64().bind()
            context.popI32().bind()
            context.popI32().bind()

            context.pushI32()
        }
        is AtomicMemoryInstruction.I64Wait -> {

            memoryIndexValidator(context, instruction.memoryIndex).bind()
            memArgValidator(context, instruction.memArg).bind()

            context.popI64().bind()
            context.popI64().bind()
            context.popI32().bind()

            context.pushI32()
        }
        is AtomicMemoryInstruction.Load -> {

            memoryIndexValidator(context, instruction.memoryIndex).bind()
            memArgValidator(context, instruction.memArg).bind()

            context.popI32().bind()

            val valueType = when (instruction) {
                is AtomicMemoryInstruction.Load.I32 -> ValueType.Number(NumberType.I32)
                is AtomicMemoryInstruction.Load.I64 -> ValueType.Number(NumberType.I64)
            }
            context.push(valueType)
        }
        is AtomicMemoryInstruction.Store -> {

            memoryIndexValidator(context, instruction.memoryIndex).bind()
            memArgValidator(context, instruction.memArg).bind()

            val valueType = when (instruction) {
                is AtomicMemoryInstruction.Store.I32 -> ValueType.Number(NumberType.I32)
                is AtomicMemoryInstruction.Store.I64 -> ValueType.Number(NumberType.I64)
            }
            context.pop(valueType).bind()
            context.popI32().bind()
        }
        is AtomicMemoryInstruction.ReadModifyWrite -> {

            memoryIndexValidator(context, instruction.memoryIndex).bind()
            memArgValidator(context, instruction.memArg).bind()

            val valueType = when (instruction) {
                is AtomicMemoryInstruction.ReadModifyWrite.I32 -> ValueType.Number(NumberType.I32)
                is AtomicMemoryInstruction.ReadModifyWrite.I64 -> ValueType.Number(NumberType.I64)
            }

            context.pop(valueType).bind()
            context.popI32().bind()

            context.push(valueType)
        }
        is AtomicMemoryInstruction.CompareExchange -> {

            memoryIndexValidator(context, instruction.memoryIndex).bind()
            memArgValidator(context, instruction.memArg).bind()

            val valueType = when (instruction) {
                is AtomicMemoryInstruction.CompareExchange.I32 -> ValueType.Number(NumberType.I32)
                is AtomicMemoryInstruction.CompareExchange.I64 -> ValueType.Number(NumberType.I64)
            }

            context.pop(valueType).bind()
            context.pop(valueType).bind()
            context.popI32().bind()

            context.push(valueType)
        }
    }
}
