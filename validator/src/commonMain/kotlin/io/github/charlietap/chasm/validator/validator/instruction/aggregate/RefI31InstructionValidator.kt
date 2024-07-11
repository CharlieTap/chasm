package io.github.charlietap.chasm.validator.validator.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.popI32
import io.github.charlietap.chasm.validator.ext.push

internal fun RefI31InstructionValidator(
    context: ValidationContext,
    instruction: AggregateInstruction.RefI31,
): Result<Unit, ModuleValidatorError> = binding {
    context.popI32().bind()
    context.push(ValueType.Reference(ReferenceType.Ref(AbstractHeapType.I31)))
}
