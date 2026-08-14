package io.github.charlietap.chasm.vm

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.test.Test

class SuspendingVirtualMachineFactoryTest {

    @Test
    fun `serial virtual machine decodes and instantiates a module`() = runImmediately {
        val virtualMachine = suspendingVirtualMachineFactory()
        val module = virtualMachine.moduleDecodeSuspending(EMPTY_MODULE).expect("expected module to decode")
        val store = virtualMachine.storeInit()

        virtualMachine.moduleInstantiateSuspending(store, module, emptyList())
            .expect("expected module to instantiate")
    }

    private fun runImmediately(block: suspend () -> Unit) {
        var result: Result<Unit>? = null
        block.startCoroutine(
            Continuation(EmptyCoroutineContext) { outcome ->
                result = outcome
            },
        )
        checkNotNull(result).getOrThrow()
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
