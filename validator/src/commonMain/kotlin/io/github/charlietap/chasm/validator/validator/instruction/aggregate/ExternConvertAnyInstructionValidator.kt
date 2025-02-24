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
import io.github.charlietap.chasm.validator.ext.push

internal fun ExternConvertAnyInstructionValidator(
    context: ValidationContext,
    instruction: AggregateInstruction.ExternConvertAny,
): Result<Unit, ModuleValidatorError> = binding {
    val actual = context.pop(ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Any))).bind()

    val extern = when ((actual as ValueType.Reference).referenceType) {
        is ReferenceType.Ref -> ReferenceType.Ref(AbstractHeapType.Extern)
        is ReferenceType.RefNull -> ReferenceType.RefNull(AbstractHeapType.Extern)
    }

    context.push(ValueType.Reference(extern))
}
