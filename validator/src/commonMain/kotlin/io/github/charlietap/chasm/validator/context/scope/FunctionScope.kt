package io.github.charlietap.chasm.validator.context.scope

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.type.InitializationStatus
import io.github.charlietap.chasm.ast.type.LocalType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.stack.stackOf
import io.github.charlietap.chasm.validator.context.FunctionContextImpl
import io.github.charlietap.chasm.validator.context.Label
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.functionType

internal fun FunctionScope(
    context: ValidationContext,
    function: Function,
): Result<ValidationContext, ModuleValidatorError> = binding {

    val functionType = context.functionType(function.typeIndex).bind()
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

    context.copy(
        functionContext = FunctionContextImpl(
            labels = stackOf(label),
            locals = (params + locals).toMutableList(),
            result = functionType.results,
        ),
    )
}
