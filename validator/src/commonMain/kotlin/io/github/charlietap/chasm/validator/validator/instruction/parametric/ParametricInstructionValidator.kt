package io.github.charlietap.chasm.validator.validator.instruction.parametric

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.pop
import io.github.charlietap.chasm.validator.ext.popI32
import io.github.charlietap.chasm.validator.ext.push
import io.github.charlietap.chasm.validator.validator.type.ValueTypeValidator

internal fun ParametricInstructionValidator(
    context: ValidationContext,
    instruction: ParametricInstruction,
): Result<Unit, ModuleValidatorError> =
    ParametricInstructionValidator(
        context = context,
        instruction = instruction,
        valueTypeValidator = ::ValueTypeValidator,
    )

internal inline fun ParametricInstructionValidator(
    context: ValidationContext,
    instruction: ParametricInstruction,
    crossinline valueTypeValidator: Validator<ValueType>,
): Result<Unit, ModuleValidatorError> = binding {
    when (instruction) {
        ParametricInstruction.Drop -> {
            context.pop().bind()
        }
        ParametricInstruction.Select -> {

            context.popI32().bind()

            val t = context.pop().bind()
            if (
                t !is ValueType.Number &&
                t !is ValueType.Vector &&
                t !is ValueType.Bottom
            ) {
                Err(TypeValidatorError.TypeMismatch).bind<Unit>()
            }

            val tt = context.pop(t).bind()

            if (t is ValueType.Bottom) {
                context.push(tt)
            } else {
                context.push(t)
            }
        }
        is ParametricInstruction.SelectWithType -> {

            instruction.types.forEach { type ->
                valueTypeValidator(context, type).bind()
            }

            context.popI32().bind()

            if (instruction.types.size != 1) {
                Err(TypeValidatorError.TypeMismatch).bind<Unit>()
            }

            val expected = instruction.types.first()
            repeat(2) {
                context.pop(expected).bind()
            }

            context.push(expected)
        }
    }
}
