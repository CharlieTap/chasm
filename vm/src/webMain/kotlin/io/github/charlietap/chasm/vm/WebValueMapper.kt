package io.github.charlietap.chasm.vm

internal object WebValueMapper {

    fun from(value: WasmVirtualMachine.Value): WebValue = when (value) {
        is WasmVirtualMachine.Value.I32 -> webI32(value.value)
        is WasmVirtualMachine.Value.I64 -> webI64(value.value)
        is WasmVirtualMachine.Value.F32 -> webF32(value.value)
        is WasmVirtualMachine.Value.F64 -> webF64(value.value)
    }

    fun to(value: WebValue): WasmVirtualMachine.Value = when (webTypeOf(value)) {
        "bigint" -> WasmVirtualMachine.Value.I64(webToI64(value))
        "number" -> asNumberValue(value)
        else -> throw IllegalArgumentException("Unsupported Wasm value type: $value")
    }

    fun toWithType(
        value: WebValue,
        resultType: String,
    ): WasmVirtualMachine.Value = when (resultType) {
        "i32" -> WasmVirtualMachine.Value.I32(webToI32(value))
        "i64" -> WasmVirtualMachine.Value.I64(webToI64(value))
        "f32" -> WasmVirtualMachine.Value.F32(webToF32(value))
        "f64" -> WasmVirtualMachine.Value.F64(webToF64(value))
        else -> throw IllegalArgumentException("Unsupported Wasm value type: $resultType")
    }

    fun toWithType(
        value: WebValue,
        resultType: ValueType,
    ): WasmVirtualMachine.Value = when (resultType) {
        is ValueType.Number -> when (resultType.numberType) {
            NumberType.I32 -> WasmVirtualMachine.Value.I32(webToI32(value))
            NumberType.I64 -> WasmVirtualMachine.Value.I64(webToI64(value))
            NumberType.F32 -> WasmVirtualMachine.Value.F32(webToF32(value))
            NumberType.F64 -> WasmVirtualMachine.Value.F64(webToF64(value))
        }
        is ValueType.Vector,
        is ValueType.Reference,
        -> throw IllegalArgumentException("Unsupported Web VM result type: $resultType")
    }

    fun mapWasmValuesUsingTypes(
        results: WebValue,
        types: List<ValueType>,
    ): List<WasmVirtualMachine.Value> {
        if (types.isEmpty()) {
            throw IllegalArgumentException("Function returned a value but no result types were provided")
        }

        return if (types.size == 1) {
            listOf(toWithType(results, types.first()))
        } else {
            if (!webIsArray(results)) {
                throw IllegalArgumentException("Function returned a single value but ${types.size} result types were provided")
            }
            val array = webValueAsArray(results)
            val resultCount = webArrayLength(array)
            if (resultCount != types.size) {
                throw IllegalArgumentException("Function returned $resultCount values but ${types.size} result types were provided")
            }
            List(resultCount) { index ->
                toWithType(webArrayGet(array, index), types[index])
            }
        }
    }

    private fun asNumberValue(value: WebValue): WasmVirtualMachine.Value {
        val number = webToDouble(value)
        if (number.isFinite() && number % 1.0 == 0.0) {
            if (number >= Int.MIN_VALUE && number <= Int.MAX_VALUE) {
                return WasmVirtualMachine.Value.I32(number.toInt())
            }
            return WasmVirtualMachine.Value.F64(number)
        }

        val f32 = webFround(value)
        if (f32 == number) {
            return WasmVirtualMachine.Value.F32(webToF32(value))
        }
        return WasmVirtualMachine.Value.F64(number)
    }
}

internal fun ValueType.toWebValType(): String = when (this) {
    is ValueType.Number -> when (this.numberType) {
        NumberType.I32 -> "i32"
        NumberType.I64 -> "i64"
        NumberType.F32 -> "f32"
        NumberType.F64 -> "f64"
    }
    is ValueType.Vector -> when (this.vectorType) {
        VectorType.V128 -> "v128"
    }
    is ValueType.Reference -> throw IllegalArgumentException("JS host function types do not support reference params/results yet: $this")
}
