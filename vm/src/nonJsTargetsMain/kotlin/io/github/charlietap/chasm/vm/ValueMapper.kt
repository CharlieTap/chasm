package io.github.charlietap.chasm.vm

import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.NumberValue
import io.github.charlietap.chasm.vm.WasmVirtualMachine.Value
import io.github.charlietap.chasm.vm.WasmVirtualMachine.Value.F32
import io.github.charlietap.chasm.vm.WasmVirtualMachine.Value.F64
import io.github.charlietap.chasm.vm.WasmVirtualMachine.Value.I32
import io.github.charlietap.chasm.vm.WasmVirtualMachine.Value.I64

object ValueMapper {
    fun from(value: Value): ExecutionValue {
        return when (value) {
            is I32 -> NumberValue.I32(value.value)
            is I64 -> NumberValue.I64(value.value)
            is F32 -> NumberValue.F32(value.value)
            is F64 -> NumberValue.F64(value.value)
        }
    }

    fun to(value: ExecutionValue): Value {
        return when (value) {
            is NumberValue.I32 -> I32(value.value)
            is NumberValue.I64 -> I64(value.value)
            is NumberValue.F32 -> F32(value.value)
            is NumberValue.F64 -> F64(value.value)
            else -> throw IllegalArgumentException("Failed to map chasm execution value: $value to wasm value")
        }
    }
}
