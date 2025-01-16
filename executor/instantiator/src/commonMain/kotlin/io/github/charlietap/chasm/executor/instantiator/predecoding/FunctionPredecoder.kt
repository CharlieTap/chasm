package io.github.charlietap.chasm.executor.instantiator.predecoding

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.default
import io.github.charlietap.chasm.executor.runtime.function.Local
import io.github.charlietap.chasm.executor.runtime.function.Expression as RuntimeExpression
import io.github.charlietap.chasm.executor.runtime.function.Function as RuntimeFunction

internal fun FunctionPredecoder(
    context: InstantiationContext,
    function: Function,
): Result<RuntimeFunction, ModuleTrapError> =
    FunctionPredecoder(
        context = context,
        function = function,
        expressionPredecoder = ::ExpressionPredecoder,
    )

internal inline fun FunctionPredecoder(
    context: InstantiationContext,
    function: Function,
    crossinline expressionPredecoder: Predecoder<Expression, RuntimeExpression>,
): Result<RuntimeFunction, ModuleTrapError> = binding {
    RuntimeFunction(
        idx = function.idx,
        typeIndex = function.typeIndex,
        locals = function.locals
            .map { local ->
                Local(
                    idx = local.idx,
                    type = local.type,
                    default = local.type.default(),
                )
            }.toTypedArray(),
        body = expressionPredecoder(context, function.body).bind(),
    )
}
