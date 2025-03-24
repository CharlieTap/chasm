package io.github.charlietap.chasm.validator.validator.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.ext.arrayType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.popI32
import io.github.charlietap.chasm.validator.ext.push
import io.github.charlietap.chasm.validator.ext.type
import io.github.charlietap.chasm.validator.ext.unpackDefault

internal fun ArrayNewDefaultInstructionValidator(
    context: ValidationContext,
    instruction: AggregateInstruction.ArrayNewDefault,
): Result<Unit, ModuleValidatorError> = binding {

    val definedType = context.type(instruction.typeIndex).bind()
    val arrayType = definedType
        .arrayType()
        .toResultOr {
            TypeValidatorError.TypeMismatch
        }.bind()

    arrayType.fieldType.unpackDefault().bind()

    context.popI32().bind()
    context.push(ValueType.Reference(ReferenceType.Ref(ConcreteHeapType.Defined(definedType))))
}
