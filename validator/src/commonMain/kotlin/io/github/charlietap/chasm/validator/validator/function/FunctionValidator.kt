package io.github.charlietap.chasm.validator.validator.function

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.ModuleValidator
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.context.scope.FunctionScope
import io.github.charlietap.chasm.validator.context.scope.NewScope
import io.github.charlietap.chasm.validator.context.scope.Scope
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.pop
import io.github.charlietap.chasm.validator.ext.popValues
import io.github.charlietap.chasm.validator.ext.resultType
import io.github.charlietap.chasm.validator.validator.instruction.InstructionBlockValidator
import io.github.charlietap.chasm.validator.validator.type.ValueTypeValidator

internal fun FunctionValidator(
    context: ModuleValidationContext,
    function: Function,
): Result<Unit, ModuleValidatorError> =
    FunctionValidator(
        context = context,
        function = function,
        scope = ::FunctionScope,
        instructionBlockValidator = ::InstructionBlockValidator,
        valueTypeValidator = ::ValueTypeValidator,
    )

internal inline fun FunctionValidator(
    context: ModuleValidationContext,
    function: Function,
    crossinline scope: NewScope<Function>,
    crossinline instructionBlockValidator: ModuleValidator<List<Instruction>>,
    crossinline valueTypeValidator: ModuleValidator<ValueType>,
): Result<Unit, ModuleValidatorError> = binding {
    scope(context, function) { scopedContext ->
        function.locals.forEach { local ->
            valueTypeValidator(scopedContext, local.type).bind()
        }

        instructionBlockValidator(scopedContext, function.body.instructions).bind()

        FinishFunction(scopedContext).bind()

        Ok(Unit)
    }.bind()
}

internal fun FinishFunction(
    context: ModuleValidationContext,
): Result<Unit, ModuleValidatorError> {
    val resultType = context.resultType().getOrElse { error ->
        return Err(error)
    }
    context.popValues(resultType.types).getOrElse { error ->
        return Err(error)
    }

    if (context.operands.depth() > 0) {
        return Err(TypeValidatorError.TypeMismatch)
    }

    context.labels.pop().getOrElse { error ->
        return Err(error)
    }
    return Ok(Unit)
}
