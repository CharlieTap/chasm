package io.github.charlietap.chasm.validator.validator.instruction.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.ext.arrayType
import io.github.charlietap.chasm.type.matching.StorageTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.pop
import io.github.charlietap.chasm.validator.ext.popI32
import io.github.charlietap.chasm.validator.ext.type

internal fun ArrayCopyInstructionValidator(
    context: ValidationContext,
    instruction: AggregateInstruction.ArrayCopy,
): Result<Unit, ModuleValidatorError> =
    ArrayCopyInstructionValidator(
        context = context,
        instruction = instruction,
        storageTypeMatcher = ::StorageTypeMatcher,
    )

internal inline fun ArrayCopyInstructionValidator(
    context: ValidationContext,
    instruction: AggregateInstruction.ArrayCopy,
    crossinline storageTypeMatcher: TypeMatcher<StorageType>,
): Result<Unit, ModuleValidatorError> = binding {

    val srcDefinedType = context.type(instruction.sourceTypeIndex).bind()
    val srcArrayType = srcDefinedType
        .arrayType()
        .toResultOr {
            TypeValidatorError.TypeMismatch
        }.bind()

    val dstDefinedType = context.type(instruction.destinationTypeIndex).bind()
    val dstArrayType = dstDefinedType
        .arrayType()
        .toResultOr {
            TypeValidatorError.TypeMismatch
        }.bind()

    if (!storageTypeMatcher(srcArrayType.fieldType.storageType, dstArrayType.fieldType.storageType, context)) {
        Err(TypeValidatorError.TypeMismatch).bind<Unit>()
    }

    if (dstArrayType.fieldType.mutability != Mutability.Var) {
        Err(TypeValidatorError.TypeMismatch).bind<Unit>()
    }

    context.popI32().bind()
    context.popI32().bind()
    context.pop(ValueType.Reference(ReferenceType.RefNull(ConcreteHeapType.Defined(srcDefinedType)))).bind()
    context.popI32().bind()
    context.pop(ValueType.Reference(ReferenceType.RefNull(ConcreteHeapType.Defined(dstDefinedType)))).bind()
}
