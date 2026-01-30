package io.github.charlietap.chasm.vm

import com.goncalossilva.resources.Resource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.text.decodeToString

class VirtualMachineTest {

    @Test
    fun `can invoke a wasm function and return an int`() {

        val vm = virtualMachineFactory()
        val bytes = Resource(FILE_DIR + "test.wasm").readBytes()

        val store = vm.storeInit()
        val module = vm.moduleDecode(bytes).expect("Failed to decode module")
        val instance = vm.moduleInstantiate(store, module, defaultImports(vm, store)).expect("Failed to instantiate module")
        val actual =
            vm.functionInvoke(store, instance, "int_function", emptyList()).expectFirstValue("Failed to invoke function int_function")

        assertEquals(WasmVirtualMachine.Value.I32(1), actual)
    }

    @Test
    fun `can invoke a wasm function and return a long`() {

        val vm = virtualMachineFactory()
        val bytes = Resource(FILE_DIR + "test.wasm").readBytes()

        val store = vm.storeInit()
        val module = vm.moduleDecode(bytes).expect("Failed to decode module")
        val instance = vm.moduleInstantiate(store, module, defaultImports(vm, store)).expect("Failed to instantiate module")
        val actual =
            vm.functionInvoke(store, instance, "long_function", emptyList()).expectFirstValue("Failed to invoke function long_function")

        assertEquals(WasmVirtualMachine.Value.I64(2), actual)
    }

    @Test
    fun `can invoke a wasm function and return a float`() {

        val vm = virtualMachineFactory()
        val bytes = Resource(FILE_DIR + "test.wasm").readBytes()

        val store = vm.storeInit()
        val module = vm.moduleDecode(bytes).expect("Failed to decode module")
        val instance = vm.moduleInstantiate(store, module, defaultImports(vm, store)).expect("Failed to instantiate module")
        val actual =
            vm.functionInvoke(store, instance, "float_function", emptyList()).expectFirstValue("Failed to invoke function float_function")

        assertEquals(WasmVirtualMachine.Value.F32(3.1f), actual)
    }

    @Test
    fun `can invoke a wasm function and return a double`() {

        val vm = virtualMachineFactory()
        val bytes = Resource(FILE_DIR + "test.wasm").readBytes()

        val store = vm.storeInit()
        val module = vm.moduleDecode(bytes).expect("Failed to decode module")
        val instance = vm.moduleInstantiate(store, module, defaultImports(vm, store)).expect("Failed to instantiate module")
        val actual =
            vm.functionInvoke(store, instance, "double_function", emptyList()).expectFirstValue("Failed to invoke function double_function")

        assertEquals(WasmVirtualMachine.Value.F64(4.2), actual)
    }

    @Test
    fun `can invoke a wasm function and return an empty list`() {

        val vm = virtualMachineFactory()
        val bytes = Resource(FILE_DIR + "test.wasm").readBytes()

        val store = vm.storeInit()
        val module = vm.moduleDecode(bytes).expect("Failed to decode module")
        val instance = vm.moduleInstantiate(store, module, defaultImports(vm, store)).expect("Failed to instantiate module")
        val actual = vm.functionInvoke(store, instance, "unit_function", emptyList()).expect("Failed to invoke function unit_function")

        assertEquals(emptyList(), actual)
    }

    @Test
    fun `can read a value from a global`() {

        val vm = virtualMachineFactory()
        val bytes = Resource(FILE_DIR + "test.wasm").readBytes()

        val store = vm.storeInit()
        val module = vm.moduleDecode(bytes).expect("Failed to decode module")
        val instance = vm.moduleInstantiate(store, module, defaultImports(vm, store)).expect("Failed to instantiate module")
        val global = vm.exportGlobal(instance, "immutable_global").expect()
        val actual = vm.globalRead(store, global).expect("Failed to read global")

        assertEquals(WasmVirtualMachine.Value.I32(117), actual)
    }

    @Test
    fun `can write a value from a global`() {

        val vm = virtualMachineFactory()
        val bytes = Resource(FILE_DIR + "test.wasm").readBytes()

        val store = vm.storeInit()
        val module = vm.moduleDecode(bytes).expect("Failed to decode module")
        val instance = vm.moduleInstantiate(store, module, defaultImports(vm, store)).expect("Failed to instantiate module")
        val global = vm.exportGlobal(instance, "mutable_global").expect()

        val current = vm.globalRead(store, global).expect("Failed to read global")
        assertEquals(WasmVirtualMachine.Value.I32(117), current)

        val result = vm.globalWrite(store, global, WasmVirtualMachine.Value.I32(118)).expect("Failed to write global")
        assertEquals(Unit, result)

        val actual = vm.globalRead(store, global).expect("Failed to read global")
        assertEquals(WasmVirtualMachine.Value.I32(118), actual)
    }

    @Test
    fun `can read bytes from a memory`() {

        val vm = virtualMachineFactory()
        val bytes = Resource(FILE_DIR + "test.wasm").readBytes()

        val store = vm.storeInit()
        val module = vm.moduleDecode(bytes).expect("Failed to decode module")
        val instance = vm.moduleInstantiate(store, module, defaultImports(vm, store)).expect("Failed to instantiate module")
        val memory = vm.exportMemory(instance, "memory").expect()

        val size = 18
        val buffer = ByteArray(size)
        val bytearray = vm.memoryReadBytes(store, memory, 1, size, buffer).expect()
        val string = bytearray.decodeToString()

        assertEquals("pointer and length", string)
    }

    @Test
    fun `can write bytes from a memory`() {

        val vm = virtualMachineFactory()
        val bytes = Resource(FILE_DIR + "test.wasm").readBytes()

        val store = vm.storeInit()
        val module = vm.moduleDecode(bytes).expect("Failed to decode module")
        val instance = vm.moduleInstantiate(store, module, defaultImports(vm, store)).expect("Failed to instantiate module")
        val memory = vm.exportMemory(instance, "memory").expect()

        val substring = "painter"
        val result = vm.memoryWriteBytes(store, memory, 1, substring.encodeToByteArray()).expect()
        assertEquals(Unit, result)

        val size = 18
        val buffer = ByteArray(size)
        val bytearray = vm.memoryReadBytes(store, memory, 1, size, buffer).expect()
        val string = bytearray.decodeToString()

        assertEquals("painter and length", string)
    }

    @Test
    fun `can read an int from a memory`() {

        val vm = virtualMachineFactory()
        val bytes = Resource(FILE_DIR + "test.wasm").readBytes()

        val store = vm.storeInit()
        val module = vm.moduleDecode(bytes).expect("Failed to decode module")
        val instance = vm.moduleInstantiate(store, module, defaultImports(vm, store)).expect("Failed to instantiate module")
        val memory = vm.exportMemory(instance, "memory").expect()

        val actual = vm.memoryReadInt(store, memory, 160).expect()

        assertEquals(117, actual)
    }

    @Test
    fun `can read a long from a memory`() {

        val vm = virtualMachineFactory()
        val bytes = Resource(FILE_DIR + "test.wasm").readBytes()

        val store = vm.storeInit()
        val module = vm.moduleDecode(bytes).expect("Failed to decode module")
        val instance = vm.moduleInstantiate(store, module, defaultImports(vm, store)).expect("Failed to instantiate module")
        val memory = vm.exportMemory(instance, "memory").expect()

        val actual = vm.memoryReadLong(store, memory, 164).expect()

        assertEquals(118, actual)
    }

    @Test
    fun `can read a ut8 encoded string from a memory`() {

        val vm = virtualMachineFactory()
        val bytes = Resource(FILE_DIR + "test.wasm").readBytes()

        val store = vm.storeInit()
        val module = vm.moduleDecode(bytes).expect("Failed to decode module")
        val instance = vm.moduleInstantiate(store, module, defaultImports(vm, store)).expect("Failed to instantiate module")
        val memory = vm.exportMemory(instance, "memory").expect()

        val expected = "pointer and length"
        val actual = vm.memoryReadUtf8String(
            store = store,
            memory = memory,
            pointer = 1,
            lengthInBytes = expected.encodeToByteArray().size,
        ).expect()

        assertEquals(expected, actual)
    }

    @Test
    fun `can write a ut8 encoded string to a memory`() {

        val vm = virtualMachineFactory()
        val bytes = Resource(FILE_DIR + "test.wasm").readBytes()

        val store = vm.storeInit()
        val module = vm.moduleDecode(bytes).expect("Failed to decode module")
        val instance = vm.moduleInstantiate(store, module, defaultImports(vm, store)).expect("Failed to instantiate module")
        val memory = vm.exportMemory(instance, "memory").expect()

        val result = vm.memoryWriteUtf8String(
            store = store,
            memory = memory,
            pointer = 1,
            value = "painter",
        ).expect()
        assertEquals(Unit, result)

        val expected = "painter and length"
        val actual = vm.memoryReadUtf8String(
            store = store,
            memory = memory,
            pointer = 1,
            lengthInBytes = expected.encodeToByteArray().size,
        ).expect()

        assertEquals(expected, actual)
    }

    @Test
    fun `can read a null terminated ut8 encoded string a memory`() {

        val vm = virtualMachineFactory()
        val bytes = Resource(FILE_DIR + "test.wasm").readBytes()

        val store = vm.storeInit()
        val module = vm.moduleDecode(bytes).expect("Failed to decode module")
        val instance = vm.moduleInstantiate(store, module, defaultImports(vm, store)).expect("Failed to instantiate module")
        val memory = vm.exportMemory(instance, "memory").expect()

        val expected = "null terminated"
        val actual = vm.memoryReadUtf8NullTerminatedUtf8String(
            store = store,
            memory = memory,
            pointer = 80,
        ).expect()

        assertEquals(expected, actual)
    }

    @Test
    fun `can write a null terminated ut8 encoded string a memory`() {

        val vm = virtualMachineFactory()
        val bytes = Resource(FILE_DIR + "test.wasm").readBytes()

        val store = vm.storeInit()
        val module = vm.moduleDecode(bytes).expect("Failed to decode module")
        val instance = vm.moduleInstantiate(store, module, defaultImports(vm, store)).expect("Failed to instantiate module")
        val memory = vm.exportMemory(instance, "memory").expect()

        val expected = "null terminated extended"
        val result = vm.memoryWriteNullTerminatedUtf8String(
            store = store,
            memory = memory,
            pointer = 80,
            value = expected,
        ).expect()
        assertEquals(Unit, result)

        val actual = vm.memoryReadUtf8NullTerminatedUtf8String(
            store = store,
            memory = memory,
            pointer = 80,
        ).expect()
        assertEquals(expected, actual)
    }

    @Test
    fun `can import a function and use it as an import`() {

        val vm = virtualMachineFactory()
        val bytes = Resource(FILE_DIR + "test.wasm").readBytes()

        val store = vm.storeInit()
        val module = vm.moduleDecode(bytes).expect("Failed to decode module")

        val instance = vm.moduleInstantiate(store, module, defaultImports(vm, store)).expect("Failed to instantiate module")
        val actual = vm.functionInvoke(store, instance, "imported_function", listOf(WasmVirtualMachine.Value.I32(107)))
            .expectFirstValue("Failed to invoke function imported_function")

        assertEquals(WasmVirtualMachine.Value.I32(117), actual)
    }

    @Test
    fun `can import a multi return function and use it as an import`() {

        val vm = virtualMachineFactory()
        val bytes = Resource(FILE_DIR + "test.wasm").readBytes()

        val store = vm.storeInit()
        val module = vm.moduleDecode(bytes).expect("Failed to decode module")

        val instance = vm.moduleInstantiate(store, module, defaultImports(vm, store)).expect("Failed to instantiate module")
        val result = vm.functionInvoke(
            store,
            instance,
            "imported_function_2",
            listOf(WasmVirtualMachine.Value.I32(5), WasmVirtualMachine.Value.I32(7)),
        ).expect("Failed to invoke function imported_function_2")

        assertEquals(listOf(WasmVirtualMachine.Value.I32(10), WasmVirtualMachine.Value.I32(21)), result)
    }

    @Test
    fun `can import a noop function and use it as an import`() {

        val vm = virtualMachineFactory()
        val bytes = Resource(FILE_DIR + "test.wasm").readBytes()

        val store = vm.storeInit()
        val module = vm.moduleDecode(bytes).expect("Failed to decode module")

        val instance = vm.moduleInstantiate(store, module, defaultImports(vm, store)).expect("Failed to instantiate module")
        val result = vm.functionInvoke(
            store,
            instance,
            "imported_noop",
            emptyList(),
        ).expect("Failed to invoke function noop_function")

        assertEquals(emptyList(), result)
    }

    private fun defaultImports(vm: WasmVirtualMachine, store: Store): List<Import> {
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
            params = listOf(
                ValueType.Number(NumberType.I32),
                ValueType.Number(NumberType.I32),
            ),
            results = listOf(
                ValueType.Number(NumberType.I32),
                ValueType.Number(NumberType.I32),
            ),
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
        val noopHost: HostFunction = {
            emptyList()
        }
        val noopFn = vm.allocateFunction(store, noopType, noopHost).expect("Failed to allocate noop function")

        return listOf(
            Import(
                moduleName = "env",
                entityName = "adder",
                address = adderFn,
            ),
            Import(
                moduleName = "env",
                entityName = "multiplier",
                address = multiplierFn,
            ),
            Import(
                moduleName = "env",
                entityName = "noop",
                address = noopFn,
            ),
        )
    }

    companion object {
        private const val FILE_DIR = ""
    }
}
