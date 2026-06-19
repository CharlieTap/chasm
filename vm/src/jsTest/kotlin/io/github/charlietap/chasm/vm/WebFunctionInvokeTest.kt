package io.github.charlietap.chasm.vm

import com.goncalossilva.resources.Resource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WebFunctionInvokeTest {

    @Test
    fun `raw function invoke is unsupported on web`() {
        val vm = virtualMachineFactory()
        val bytes = Resource("test.wasm").readBytes()

        val store = vm.storeInit()
        val module = vm.moduleDecode(bytes).expect("Failed to decode module")
        val instance = vm.moduleInstantiate(store, module, defaultImports(vm, store)).expect("Failed to instantiate module")

        val result = vm.functionInvoke(store, instance, "int_function", emptyList())

        assertTrue(result is WasmVirtualMachine.Result.Error)
        assertEquals("functionInvoke is unsupported on web targets; use functionInvokeTyped with explicit result types", result.message)
    }

    private fun defaultImports(
        vm: WasmVirtualMachine,
        store: Store,
    ): List<Import> {
        val adderType = FunctionType(
            params = listOf(ValueType.Number(NumberType.I32)),
            results = listOf(ValueType.Number(NumberType.I32)),
        )
        val adderHost: HostFunction = { params ->
            val x = (params[0] as WasmVirtualMachine.Value.I32).value
            listOf(WasmVirtualMachine.Value.I32(x + 10))
        }
        val adderFn = vm.allocateFunction(store, adderType, adderHost).expect("Failed to allocate adder function")

        val multiplierType = FunctionType(
            params = listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32)),
            results = listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32)),
        )
        val multiplierHost: HostFunction = { params ->
            val x = (params[0] as WasmVirtualMachine.Value.I32).value
            val y = (params[1] as WasmVirtualMachine.Value.I32).value
            listOf(WasmVirtualMachine.Value.I32(x * 2), WasmVirtualMachine.Value.I32(y * 3))
        }
        val multiplierFn = vm.allocateFunction(store, multiplierType, multiplierHost).expect("Failed to allocate multiplier function")

        val noopType = FunctionType(
            params = emptyList(),
            results = emptyList(),
        )
        val noopHost: HostFunction = { emptyList() }
        val noopFn = vm.allocateFunction(store, noopType, noopHost).expect("Failed to allocate noop function")

        return listOf(
            Import("env", "adder", adderFn),
            Import("env", "multiplier", multiplierFn),
            Import("env", "noop", noopFn),
        )
    }
}
