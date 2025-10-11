package io.github.charlietap.chasm.vm

internal object ValueMapper {

    fun from(value: WasmVirtualMachine.Value): dynamic = when (value) {
        is WasmVirtualMachine.Value.I32 -> value.value
        is WasmVirtualMachine.Value.I64 -> longToBigInt(value.value)
        is WasmVirtualMachine.Value.F32 -> value.value
        is WasmVirtualMachine.Value.F64 -> value.value
    }

    fun to(value: dynamic): WasmVirtualMachine.Value = when (typeOf(value)) {
        "bigint" -> WasmVirtualMachine.Value.I64(bigIntToLong(value))
        "number" -> asNumberValue(value)
        else -> throw IllegalArgumentException("Unsupported Wasm value type: ${'$'}value")
    }

    fun toWithType(value: dynamic, resultType: String): WasmVirtualMachine.Value = when (resultType) {
        "i32" -> WasmVirtualMachine.Value.I32(value.unsafeCast<Int>())
        "i64" -> WasmVirtualMachine.Value.I64(bigIntToLong(value))
        "f32" -> WasmVirtualMachine.Value.F32(fround(value).toFloat())
        "f64" -> WasmVirtualMachine.Value.F64(value.unsafeCast<Double>())
        else -> throw IllegalArgumentException("Unsupported Wasm value type: $resultType")
    }

    private fun asNumberValue(number: dynamic): WasmVirtualMachine.Value {
        val n = number.unsafeCast<Double>()
        if (n.isFinite() && isInteger(n)) {
            if (n >= Int.MIN_VALUE && n <= Int.MAX_VALUE) {
                return WasmVirtualMachine.Value.I32(n.toInt())
            }
            return WasmVirtualMachine.Value.F64(n)
        }

        val f32 = fround(number)
        if (f32 == n) {
            val pretty = js("Number(number.toPrecision(6))").unsafeCast<Double>()
            return WasmVirtualMachine.Value.F32(pretty.toFloat())
        }
        return WasmVirtualMachine.Value.F64(n)
    }

    fun mapWasmValuesUsingInference(
        results: dynamic,
    ): List<WasmVirtualMachine.Value> {
        return if (isArray(results)) {
            results.unsafeCast<Array<dynamic>>().map(ValueMapper::to)
        } else {
            listOf(to(results))
        }
    }

    fun mapWasmValuesUsingTypes(
        results: dynamic,
        types: Array<String>,
    ): List<WasmVirtualMachine.Value> {
        if (types.isNotEmpty()) {
            if (isArray(results)) {
                val arr = results.unsafeCast<Array<dynamic>>()
                return arr.mapIndexed { index, value ->
                    val type = (if (index < types.size) types[index] else null)
                    if (type != null) toWithType(value, type) else to(value)
                }
            } else {
                val type = types[0]
                return listOf(toWithType(results, type))
            }
        }

        return emptyList()
    }
}
