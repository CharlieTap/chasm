package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.ext.functionType
import io.github.charlietap.chasm.type.matching.ResultTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.popValues
import io.github.charlietap.chasm.validator.ext.pushValues
import io.github.charlietap.chasm.validator.ext.type
import io.github.charlietap.chasm.validator.ext.unreachable

internal fun ReturnCallRefInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.ReturnCallRef,
): Result<Unit, ModuleValidatorError> =
    ReturnCallRefInstructionValidator(
        context = context,
        instruction = instruction,
        resultTypeMatcher = ::ResultTypeMatcher,
    )

internal inline fun ReturnCallRefInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.ReturnCallRef,
    crossinline resultTypeMatcher: TypeMatcher<ResultType>,
): Result<Unit, ModuleValidatorError> = binding {

    val definedType = context.type(instruction.typeIndex).bind()
    val functionType = definedType
        .functionType()
        .toResultOr {
            TypeValidatorError.TypeMismatch
        }.bind()

    val result = context.result
    if (result == null || !resultTypeMatcher(functionType.results, result, context)) {
        Err(TypeValidatorError.TypeMismatch).bind<Unit>()
    }

    context
        .popValues(
            functionType.params.types + listOf(ValueType.Reference(ReferenceType.RefNull(ConcreteHeapType.Defined(definedType)))),
        ).bind()
    context.pushValues(functionType.results.types)

    context.popValues(context.result?.types?.asReversed() ?: emptyList()).bind()
    context.unreachable().bind()
}
