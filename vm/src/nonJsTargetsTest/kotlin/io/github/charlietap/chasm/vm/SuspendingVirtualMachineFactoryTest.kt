package io.github.charlietap.chasm.vm

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SuspendingVirtualMachineFactoryTest {

    @Test
    fun `parallel virtual machine decodes and instantiates a module`() = runTest {
        val virtualMachine = suspendingVirtualMachineFactory(StandardTestDispatcher(testScheduler))
        val module = virtualMachine.moduleDecodeSuspending(EMPTY_MODULE).expect("expected module to decode")
        val store = virtualMachine.storeInit()

        virtualMachine.moduleInstantiateSuspending(store, module, emptyList())
            .expect("expected module to instantiate")
    }

    private companion object {
        val EMPTY_MODULE = byteArrayOf(
            0x00,
            0x61,
            0x73,
            0x6D,
            0x01,
            0x00,
            0x00,
            0x00,
        )
    }
}
