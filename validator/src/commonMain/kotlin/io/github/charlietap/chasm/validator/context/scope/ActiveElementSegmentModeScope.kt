package io.github.charlietap.chasm.validator.context.scope

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.context.ExpressionContextImpl
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.tableType

internal fun ActiveElementSegmentModeScope(
    context: ValidationContext,
    mode: ElementSegment.Mode.Active,
): Result<ValidationContext, ModuleValidatorError> = binding {
    val tableType = context.tableType(mode.tableIndex).bind()
    val numberType = when (tableType.addressType) {
        AddressType.I32 -> NumberType.I32
        AddressType.I64 -> NumberType.I64
    }
    context.copy(
        expressionContext = ExpressionContextImpl(
            expressionResultType = ResultType(listOf(ValueType.Number(numberType))),
        ),
    )
}
