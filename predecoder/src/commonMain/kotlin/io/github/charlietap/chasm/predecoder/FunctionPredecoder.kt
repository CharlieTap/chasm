package io.github.charlietap.chasm.predecoder

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.module.Function
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.default
import io.github.charlietap.chasm.type.ext.functionType
import io.github.charlietap.chasm.runtime.function.Expression as RuntimeExpression
import io.github.charlietap.chasm.runtime.function.Function as RuntimeFunction

fun FunctionPredecoder(
    context: PredecodingContext,
    function: Function,
): Result<RuntimeFunction, ModuleTrapError> =
    FunctionPredecoder(
        context = context,
        function = function,
        expressionPredecoder = ::ExpressionPredecoder,
    )

internal inline fun FunctionPredecoder(
    context: PredecodingContext,
    function: Function,
    crossinline expressionPredecoder: Predecoder<Expression, RuntimeExpression>,
): Result<RuntimeFunction, ModuleTrapError> = binding {
    val type = context.types
        .getOrNull(function.typeIndex.idx)
        ?.functionType()
    val params = type?.params?.types?.size ?: 0
    val results = type?.results?.types?.size ?: 0

    RuntimeFunction(
        idx = function.idx,
        typeIndex = function.typeIndex,
        locals = LongArray(function.locals.size) { index ->
            function.locals[index].type.default()
        },
        body = expressionPredecoder(
            context.copy(
                functionParamCount = params,
                functionResultCount = results,
            ),
            function.body,
        ).bind(),
        frameSlots = maxOf(function.frameSlots, params + function.locals.size),
        frameSlotMode = function.frameSlotMode,
        returnSlots = function.returnSlots,
    )
}
