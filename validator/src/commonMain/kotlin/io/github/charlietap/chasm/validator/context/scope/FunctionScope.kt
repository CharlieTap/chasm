package io.github.charlietap.chasm.validator.context.scope

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.stack.stackOf
import io.github.charlietap.chasm.type.InitializationStatus
import io.github.charlietap.chasm.type.LocalType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.context.FunctionContextImpl
import io.github.charlietap.chasm.validator.context.Label
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.functionType

internal fun FunctionScope(
    context: ValidationContext,
    function: Function,
    block: (ValidationContext) -> Result<Unit, ModuleValidatorError>,
): Result<Unit, ModuleValidatorError> {

    val functionType = context.functionType(function.typeIndex).getOrElse { error ->
        return Err(error)
    }
    val label = Label(
        instruction = null,
        inputs = functionType.params,
        outputs = functionType.results,
        operandsDepth = context.operands.depth(),
        unreachable = false,
    )

    val params = functionType.params.types.map { param ->
        LocalType(InitializationStatus.SET, param)
    }
    val locals = function.locals.map { local ->
        val status = when (val type = local.type) {
            is ValueType.Number,
            is ValueType.Vector,
            -> InitializationStatus.SET
            is ValueType.Reference -> when (type.referenceType) {
                is ReferenceType.Ref -> InitializationStatus.UNSET
                is ReferenceType.RefNull -> InitializationStatus.SET
            }
            is ValueType.Bottom -> InitializationStatus.UNSET
        }
        LocalType(status, local.type)
    }
    val resultType = context.functionContext.result

    context.functionContext.labels.push(label)
    context.functionContext.locals.addAll(params)
    context.functionContext.locals.addAll(locals)
    context.functionContext.result = functionType.results

    val result = block(context)

    context.functionContext.labels.clear()
    context.functionContext.locals.clear()
    context.functionContext.result = resultType

    return result
}
