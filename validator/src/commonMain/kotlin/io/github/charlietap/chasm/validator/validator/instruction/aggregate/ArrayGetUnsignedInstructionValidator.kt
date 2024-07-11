package io.github.charlietap.chasm.validator.validator.instruction.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.StorageType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.type.ext.arrayType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.pop
import io.github.charlietap.chasm.validator.ext.popI32
import io.github.charlietap.chasm.validator.ext.push
import io.github.charlietap.chasm.validator.ext.type
import io.github.charlietap.chasm.validator.ext.unpack

internal fun ArrayGetUnsignedInstructionValidator(
    context: ValidationContext,
    instruction: AggregateInstruction.ArrayGetUnsigned,
): Result<Unit, ModuleValidatorError> = binding {

    val definedType = context.type(instruction.typeIndex).bind()
    val arrayType = definedType.arrayType().toResultOr {
        TypeValidatorError.TypeMismatch
    }.bind()

    if (arrayType.fieldType.storageType !is StorageType.Packed) {
        Err(TypeValidatorError.TypeMismatch).bind<Unit>()
    }

    val t = arrayType.fieldType.unpack()

    context.popI32().bind()
    context.pop(ValueType.Reference(ReferenceType.RefNull(ConcreteHeapType.Defined(definedType)))).bind()

    context.push(t)
}
