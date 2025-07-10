package io.github.charlietap.chasm.gradle.ext

import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.FLOAT
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.UNIT
import io.github.charlietap.chasm.gradle.Aggregate
import io.github.charlietap.chasm.gradle.Scalar
import io.github.charlietap.chasm.gradle.Type
import io.github.charlietap.chasm.runtime.value.NumberValue

internal fun Type.asExecutionValue() = when (this) {
    Scalar.Integer -> NumberValue.I32::class
    Scalar.Long -> NumberValue.I64::class
    Scalar.Float -> NumberValue.F32::class
    Scalar.Double -> NumberValue.F64::class
    Scalar.String,
    Scalar.Unit,
    is Aggregate,
    -> throw UnsupportedOperationException("Can't convert $this to ExecutionValue")
}

internal fun Type.asTypeName() = when (this) {
    Scalar.Integer -> INT
    Scalar.Long -> LONG
    Scalar.Float -> FLOAT
    Scalar.Double -> DOUBLE
    Scalar.String -> STRING
    Scalar.Unit -> UNIT
    is Aggregate -> throw UnsupportedOperationException("Can't convert $this to ExecutionValue")
}
