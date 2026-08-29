package io.github.charlietap.chasm.gradle.ext

import io.github.charlietap.chasm.gradle.Scalar
import io.github.charlietap.chasm.gradle.Type
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.vm.WasmVirtualMachine
import kotlin.reflect.KClass

internal fun ValueType.asValue(): KClass<out WasmVirtualMachine.Value> {
    return when (this) {
        is ValueType.Number -> when (this.numberType) {
            NumberType.I32 -> WasmVirtualMachine.Value.I32::class
            NumberType.I64 -> WasmVirtualMachine.Value.I64::class
            NumberType.F32 -> WasmVirtualMachine.Value.F32::class
            NumberType.F64 -> WasmVirtualMachine.Value.F64::class
        }
        is ValueType.Bottom,
        is ValueType.Reference,
        is ValueType.Vector,
        -> throw IllegalStateException("Cannot convert $this to ExecutionValue")
    }
}

internal fun ValueType.asType(): Type {
    return when (this) {
        is ValueType.Number -> when (this.numberType) {
            NumberType.I32 -> Scalar.Integer
            NumberType.I64 -> Scalar.Long
            NumberType.F32 -> Scalar.Float
            NumberType.F64 -> Scalar.Double
        }
        is ValueType.Bottom,
        is ValueType.Reference,
        is ValueType.Vector,
        -> throw IllegalStateException("Cannot convert $this to Type")
    }
}
