package io.github.charlietap.chasm.validator.context.scope

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.type.InitializationStatus
import io.github.charlietap.chasm.type.LocalType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.context.FunctionContextImpl
import io.github.charlietap.chasm.validator.context.Label
import io.github.charlietap.chasm.validator.context.LabelKind
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.functionType

internal fun FunctionScope(
    context: ModuleValidationContext,
    function: Function,
    block: (ModuleValidationContext) -> Result<Unit, ModuleValidatorError>,
): Result<Unit, ModuleValidatorError> {

    val functionType = context.functionType(function.typeIndex).getOrElse { error ->
        return Err(error)
    }
    val label = Label(
        kind = LabelKind.Function,
        inputs = functionType.params,
        outputs = functionType.results,
        operandsDepth = context.operands.depth(),
        localChangesDepth = context.localChanges.size,
        unreachable = false,
    )

    functionType.params.types.forEach { param ->
        context.locals += LocalType(InitializationStatus.SET, param)
    }
    function.locals.forEach { local ->
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
        context.locals += LocalType(status, local.type)
    }
    val resultType = context.functionContext.result

    context.functionContext.labels.push(label)
    context.functionContext.result = functionType.results

    val result = block(context)

    context.functionContext.labels.clear()
    context.functionContext.locals.clear()
    context.functionContext.localChanges.clear()
    context.functionContext.operands.clear()
    context.functionContext.result = resultType

    return result
}
