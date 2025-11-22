package io.github.charlietap.chasm.validator.validator.instruction.atomic

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun AtomicMemoryInstructionValidator(
    context: ValidationContext,
    instruction: AtomicMemoryInstruction,
): Result<Unit, ModuleValidatorError> =
    AtomicMemoryInstructionValidator(
        context = context,
        instruction = instruction,
        fenceValidator = ::AtomicFenceInstructionValidator,
        notifyValidator = ::AtomicNotifyInstructionValidator,
        i32WaitValidator = ::AtomicI32WaitInstructionValidator,
        i64WaitValidator = ::AtomicI64WaitInstructionValidator,
        loadValidator = ::AtomicLoadInstructionValidator,
        storeValidator = ::AtomicStoreInstructionValidator,
        readModifyWriteValidator = ::AtomicReadModifyWriteInstructionValidator,
        compareExchangeValidator = ::AtomicCompareExchangeInstructionValidator,
    )

internal inline fun AtomicMemoryInstructionValidator(
    context: ValidationContext,
    instruction: AtomicMemoryInstruction,
    crossinline fenceValidator: Validator<AtomicMemoryInstruction.Fence>,
    crossinline notifyValidator: Validator<AtomicMemoryInstruction.Notify>,
    crossinline i32WaitValidator: Validator<AtomicMemoryInstruction.I32Wait>,
    crossinline i64WaitValidator: Validator<AtomicMemoryInstruction.I64Wait>,
    crossinline loadValidator: Validator<AtomicMemoryInstruction.Load>,
    crossinline storeValidator: Validator<AtomicMemoryInstruction.Store>,
    crossinline readModifyWriteValidator: Validator<AtomicMemoryInstruction.ReadModifyWrite>,
    crossinline compareExchangeValidator: Validator<AtomicMemoryInstruction.CompareExchange>,
): Result<Unit, ModuleValidatorError> = binding {
    when (instruction) {
        AtomicMemoryInstruction.Fence -> fenceValidator(context, AtomicMemoryInstruction.Fence).bind()
        is AtomicMemoryInstruction.Notify -> notifyValidator(context, instruction).bind()
        is AtomicMemoryInstruction.I32Wait -> i32WaitValidator(context, instruction).bind()
        is AtomicMemoryInstruction.I64Wait -> i64WaitValidator(context, instruction).bind()
        is AtomicMemoryInstruction.Load -> loadValidator(context, instruction).bind()
        is AtomicMemoryInstruction.Store -> storeValidator(context, instruction).bind()
        is AtomicMemoryInstruction.ReadModifyWrite -> readModifyWriteValidator(context, instruction).bind()
        is AtomicMemoryInstruction.CompareExchange -> compareExchangeValidator(context, instruction).bind()
    }
}
