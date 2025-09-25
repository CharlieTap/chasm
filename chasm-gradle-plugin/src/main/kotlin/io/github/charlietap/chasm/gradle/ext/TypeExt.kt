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
import io.github.charlietap.chasm.vm.WasmVirtualMachine

internal fun Type.asValue() = when (this) {
    Scalar.Integer -> WasmVirtualMachine.Value.I32::class
    Scalar.Long -> WasmVirtualMachine.Value.I64::class
    Scalar.Float -> WasmVirtualMachine.Value.F32::class
    Scalar.Double -> WasmVirtualMachine.Value.F64::class
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
