package io.github.charlietap.chasm.validator.validator.function

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.context.scope.FunctionScope
import io.github.charlietap.chasm.validator.context.scope.Scope
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.pop
import io.github.charlietap.chasm.validator.ext.popValues
import io.github.charlietap.chasm.validator.ext.resultType
import io.github.charlietap.chasm.validator.validator.instruction.InstructionBlockValidator
import io.github.charlietap.chasm.validator.validator.type.ValueTypeValidator

internal fun FunctionValidator(
    context: ValidationContext,
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
    context: ValidationContext,
    function: Function,
    crossinline scope: Scope<Function>,
    crossinline instructionBlockValidator: Validator<List<Instruction>>,
    crossinline valueTypeValidator: Validator<ValueType>,
): Result<Unit, ModuleValidatorError> = binding {

    val scopedContext = scope(context, function).bind()

    function.locals.forEach { local ->
        valueTypeValidator(scopedContext, local.type).bind()
    }

    instructionBlockValidator(scopedContext, function.body.instructions).bind()

    val resultType = scopedContext.resultType().bind()
    scopedContext.popValues(resultType.types).bind()

    if (scopedContext.operands.depth() > 0) {
        Err(TypeValidatorError.TypeMismatch).bind<Unit>()
    }

    scopedContext.labels.pop().bind()
}
