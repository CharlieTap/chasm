package io.github.charlietap.chasm.validator.context.scope

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.context.ExpressionContextImpl
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.memoryType

internal fun ActiveDataSegmentScope(
    context: ValidationContext,
    mode: DataSegment.Mode.Active,
): Result<ValidationContext, ModuleValidatorError> = binding {

    val memory = context.memoryType(mode.memoryIndex).bind()
    val valueType = when (memory.addressType) {
        AddressType.I32 -> ValueType.Number(NumberType.I32)
        AddressType.I64 -> ValueType.Number(NumberType.I64)
    }

    context.copy(
        expressionContext = ExpressionContextImpl(
            expressionResultType = ResultType(listOf(valueType)),
        ),
    )
}
