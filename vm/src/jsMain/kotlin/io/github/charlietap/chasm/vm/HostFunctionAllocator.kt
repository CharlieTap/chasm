package io.github.charlietap.chasm.vm

import kotlin.collections.map

internal class HostFunctionAllocator {
    fun allocate(
        type: FunctionType,
        function: HostFunction,
    ): JsFunction {
        val paramTypes = type.params.map { it.toJsValType() }
        val resultTypes = type.results.map { it.toJsValType() }

        val implementation = when (paramTypes.size) {
            0 -> (
                {
                    allocateHostFunction(function, emptyList(), paramTypes, resultTypes)
                } as Any
            ).unsafeCast<dynamic>()
            1 -> { a: dynamic ->
                allocateHostFunction(function, listOf(a), paramTypes, resultTypes)
            }
            2 -> { a: dynamic, b: dynamic ->
                allocateHostFunction(function, listOf(a, b), paramTypes, resultTypes)
            }
            3 -> { a: dynamic, b: dynamic, c: dynamic ->
                allocateHostFunction(function, listOf(a, b, c), paramTypes, resultTypes)
            }
            4 -> { a: dynamic, b: dynamic, c: dynamic, d: dynamic ->
                allocateHostFunction(function, listOf(a, b, c, d), paramTypes, resultTypes)
            }
            5 -> { a: dynamic, b: dynamic, c: dynamic, d: dynamic, e: dynamic ->
                allocateHostFunction(function, listOf(a, b, c, d, e), paramTypes, resultTypes)
            }
            6 -> { a: dynamic, b: dynamic, c: dynamic, d: dynamic, e: dynamic, f: dynamic ->
                allocateHostFunction(function, listOf(a, b, c, d, e, f), paramTypes, resultTypes)
            }
            7 -> { a: dynamic, b: dynamic, c: dynamic, d: dynamic, e: dynamic, f: dynamic, g: dynamic ->
                allocateHostFunction(function, listOf(a, b, c, d, e, f, g), paramTypes, resultTypes)
            }
            8 -> { a: dynamic, b: dynamic, c: dynamic, d: dynamic, e: dynamic, f: dynamic, g: dynamic, h: dynamic ->
                allocateHostFunction(function, listOf(a, b, c, d, e, f, g, h), paramTypes, resultTypes)
            }
            9 -> { a: dynamic, b: dynamic, c: dynamic, d: dynamic, e: dynamic, f: dynamic, g: dynamic, h: dynamic, i: dynamic ->
                allocateHostFunction(function, listOf(a, b, c, d, e, f, g, h, i), paramTypes, resultTypes)
            }
            10 -> { a: dynamic, b: dynamic, c: dynamic, d: dynamic, e: dynamic, f: dynamic, g: dynamic, h: dynamic, i: dynamic, j: dynamic ->
                allocateHostFunction(function, listOf(a, b, c, d, e, f, g, h, i, j), paramTypes, resultTypes)
            }
            11 -> { a: dynamic, b: dynamic, c: dynamic, d: dynamic, e: dynamic, f: dynamic, g: dynamic, h: dynamic, i: dynamic, j: dynamic, k: dynamic ->
                allocateHostFunction(function, listOf(a, b, c, d, e, f, g, h, i, j, k), paramTypes, resultTypes)
            }
            12 -> { a: dynamic, b: dynamic, c: dynamic, d: dynamic, e: dynamic, f: dynamic, g: dynamic, h: dynamic, i: dynamic, j: dynamic, k: dynamic, l: dynamic ->
                allocateHostFunction(function, listOf(a, b, c, d, e, f, g, h, i, j, k, l), paramTypes, resultTypes)
            }
            13 -> { a: dynamic, b: dynamic, c: dynamic, d: dynamic, e: dynamic, f: dynamic, g: dynamic, h: dynamic, i: dynamic, j: dynamic, k: dynamic, l: dynamic, m: dynamic ->
                allocateHostFunction(function, listOf(a, b, c, d, e, f, g, h, i, j, k, l, m), paramTypes, resultTypes)
            }
            14 -> { a: dynamic, b: dynamic, c: dynamic, d: dynamic, e: dynamic, f: dynamic, g: dynamic, h: dynamic, i: dynamic, j: dynamic, k: dynamic, l: dynamic, m: dynamic, n: dynamic ->
                allocateHostFunction(function, listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n), paramTypes, resultTypes)
            }
            15 -> { a: dynamic, b: dynamic, c: dynamic, d: dynamic, e: dynamic, f: dynamic, g: dynamic, h: dynamic, i: dynamic, j: dynamic, k: dynamic, l: dynamic, m: dynamic, n: dynamic, o: dynamic ->
                allocateHostFunction(function, listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o), paramTypes, resultTypes)
            }
            else -> throw IllegalArgumentException("Failed to allocate host function with more than 15 parameters")
        }

        return JsFunction(implementation)
    }

    companion object {
        fun allocateHostFunction(
            function: HostFunction,
            args: List<dynamic>,
            paramTypes: List<String>,
            resultTypes: List<String>,
        ): dynamic {

            val params = args.mapIndexed { idx, param ->
                val type = paramTypes[idx]
                ValueMapper.toWithType(param, type)
            }

            val results = function(params)
            return when (resultTypes.size) {
                0 -> UNDEFINED
                1 -> when (val result = results.first()) {
                    is WasmVirtualMachine.Value.I32 -> result.value
                    is WasmVirtualMachine.Value.I64 -> result.value
                    is WasmVirtualMachine.Value.F32 -> result.value
                    is WasmVirtualMachine.Value.F64 -> result.value
                }
                else -> results.map { result ->
                    when (result) {
                        is WasmVirtualMachine.Value.I32 -> result.value
                        is WasmVirtualMachine.Value.I64 -> result.value
                        is WasmVirtualMachine.Value.F32 -> result.value
                        is WasmVirtualMachine.Value.F64 -> result.value
                    }
                }.toTypedArray()
            }
        }
    }
}
