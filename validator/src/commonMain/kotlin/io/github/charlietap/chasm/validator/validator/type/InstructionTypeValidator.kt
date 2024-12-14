package io.github.charlietap.chasm.validator.validator.type

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.ast.type.BottomType
import io.github.charlietap.chasm.ast.type.InstructionType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.stack.peek
import io.github.charlietap.chasm.stack.pop
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.type.matching.ValueTypeMatcher
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError

internal fun InstructionTypeValidator(
    context: ValidationContext,
    type: InstructionType,
): Result<Unit, ModuleValidatorError> =
    InstructionTypeValidator(
        context = context,
        type = type,
        valueTypeMatcher = ::ValueTypeMatcher,
    )

internal fun InstructionTypeValidator(
    context: ValidationContext,
    type: InstructionType,
    valueTypeMatcher: TypeMatcher<ValueType>,
): Result<Unit, ModuleValidatorError> = binding {

    val unreachable = if (context.result == null) {
        false
    } else {
        val label = context.labels
            .peek()
            .mapError {
                TypeValidatorError.TypeMismatch
            }.bind()
        label.unreachable
    }

    val blockUnderflow = if (context.result == null) {
        null
    } else {
        val label = context.labels
            .peek()
            .mapError {
                TypeValidatorError.TypeMismatch
            }.bind()
        label.operandsDepth
    }

    val startDepth = if (context.result == null) {
        0
    } else {
        val label = context.labels
            .peek()
            .mapError {
                TypeValidatorError.TypeMismatch
            }.bind()
        label.operandsDepth
    }

    type.inputs.types.asReversed().forEach { expectedType ->

        blockUnderflow?.let {
            if (blockUnderflow == context.operands.depth() && !unreachable) {
                Err(TypeValidatorError.TypeMismatch).bind<Unit>()
            }
        }

        val actualType = if (context.operands.depth() == startDepth && unreachable) {
            ValueType.Bottom(BottomType)
        } else {
            context.operands
                .pop()
                .mapError {
                    TypeValidatorError.TypeMismatch
                }.bind()
        }

        // instruction types are contravariant in input
        if (!valueTypeMatcher(actualType, expectedType, context)) {
            Err(TypeValidatorError.TypeMismatch).bind<Unit>()
        }
    }

    type.outputs.types.forEach { operandType ->
        context.operands.push(operandType)
    }
}
