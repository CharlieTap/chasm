package io.github.charlietap.chasm.executor.instantiator.ext

import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

internal fun NumberType.default(): ExecutionValue = when (this) {
    NumberType.I32 -> NumberValue.I32(0)
    NumberType.I64 -> NumberValue.I64(0)
    NumberType.F32 -> NumberValue.F32(0f)
    NumberType.F64 -> NumberValue.F64(0.0)
}
