package io.github.charlietap.chasm.validator.validator.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.pop
import io.github.charlietap.chasm.validator.ext.pushI32

internal fun ArrayLenInstructionValidator(
    context: ValidationContext,
    instruction: AggregateInstruction.ArrayLen,
): Result<Unit, ModuleValidatorError> = binding {
    context.pop(ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Array))).bind()
    context.pushI32()
}
