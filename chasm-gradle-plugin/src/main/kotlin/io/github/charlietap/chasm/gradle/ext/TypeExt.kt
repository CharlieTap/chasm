package io.github.charlietap.chasm.gradle.ext

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.FLOAT
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.UNIT
import io.github.charlietap.chasm.gradle.Aggregate
import io.github.charlietap.chasm.gradle.Scalar
import io.github.charlietap.chasm.gradle.Type
import io.github.charlietap.chasm.gradle.VALUE_F32_CLASS_NAME
import io.github.charlietap.chasm.gradle.VALUE_F64_CLASS_NAME
import io.github.charlietap.chasm.gradle.VALUE_I32_CLASS_NAME
import io.github.charlietap.chasm.gradle.VALUE_I64_CLASS_NAME

internal fun Type.asValue(): ClassName = when (this) {
    Scalar.Integer -> VALUE_I32_CLASS_NAME
    Scalar.Long -> VALUE_I64_CLASS_NAME
    Scalar.Float -> VALUE_F32_CLASS_NAME
    Scalar.Double -> VALUE_F64_CLASS_NAME
    Scalar.String,
    Scalar.Unit,
    is Aggregate,
    -> throw UnsupportedOperationException("Can't convert $this to ExecutionValue")
    else -> throw UnsupportedOperationException("Unknown type: $this")
}

internal fun Type.asTypeName() = when (this) {
    Scalar.Integer -> INT
    Scalar.Long -> LONG
    Scalar.Float -> FLOAT
    Scalar.Double -> DOUBLE
    Scalar.String -> STRING
    Scalar.Unit -> UNIT
    is Aggregate -> throw UnsupportedOperationException("Can't convert $this to ExecutionValue")
    else -> throw UnsupportedOperationException("Unknown type: $this")
}
