package io.github.charlietap.chasm.validator.validator.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.pop
import io.github.charlietap.chasm.validator.ext.pushI32

internal fun I31GetUnsignedInstructionValidator(
    context: ValidationContext,
    instruction: AggregateInstruction.I31GetUnsigned,
): Result<Unit, ModuleValidatorError> = binding {
    context.pop(ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.I31))).bind()
    context.pushI32()
}
