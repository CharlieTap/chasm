package io.github.charlietap.chasm.vm

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GlobalWriteTest {

    @Test
    fun `writing a value of another type to a global is an error`() {

        val vm = virtualMachineFactory()
        val store = vm.storeInit()
        val module = vm.moduleDecode(MUTABLE_I32_GLOBAL_MODULE).expect("Failed to decode module")
        val instance = vm.moduleInstantiate(store, module, emptyList()).expect("Failed to instantiate module")
        val global = vm.exportGlobal(instance, "global").expect("Failed to find global")

        val actual = vm.globalWrite(store, global, WasmVirtualMachine.Value.F64(1.5))

        assertIs<WasmVirtualMachine.Result.Error>(actual)
        assertEquals(WasmVirtualMachine.Value.I32(1), vm.globalRead(store, global).expect("Failed to read global"))
    }

    private companion object {
        val MUTABLE_I32_GLOBAL_MODULE = byteArrayOf(
            0x00,
            0x61,
            0x73,
            0x6D,
            0x01,
            0x00,
            0x00,
            0x00,
            0x06,
            0x06,
            0x01,
            0x7F,
            0x01,
            0x41,
            0x01,
            0x0B,
            0x07,
            0x0A,
            0x01,
            0x06,
            0x67,
            0x6C,
            0x6F,
            0x62,
            0x61,
            0x6C,
            0x03,
            0x00,
        )
    }
}
