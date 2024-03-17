package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.frameState
import io.github.charlietap.chasm.fixture.instance.functionAddress
import io.github.charlietap.chasm.fixture.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.instruction.functionIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class CallExecutorImplTest {

    @Test
    fun `can execute a call to a wasm function and return a result`() {

        val stack = stack()
        val functionInstance = wasmFunctionInstance()
        val functionIndex = functionIndex(0u)
        val functionAddress = functionAddress()
        val store = store(
            functions = mutableListOf(functionInstance),
        )
        val moduleInstance = moduleInstance(
            functionAddresses = mutableListOf(functionAddress),
        )
        val frame = frame(
            state = frameState(moduleInstance = moduleInstance),
        )
        val tailRecursion = true

        val wasmFunctionCall: WasmFunctionCall = { _store, _stack, _function, _tailRecursion ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(functionInstance, _function)
            assertEquals(tailRecursion, _tailRecursion)

            Ok(Unit)
        }

        val hostFunctionCall: HostFunctionCall = { _, _, _ ->
            fail("Host function should not be called in this scenario")
        }

        stack.push(frame)

        val actual = CallExecutorImpl(store, stack, functionIndex, tailRecursion, hostFunctionCall, wasmFunctionCall)

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `can execute a call to a host function and return a result`() {
        val stack = stack()
        val functionInstance = hostFunctionInstance()
        val functionIndex = functionIndex(0u)
        val functionAddress = functionAddress()
        val store = store(
            functions = mutableListOf(functionInstance),
        )
        val moduleInstance = moduleInstance(
            functionAddresses = mutableListOf(functionAddress),
        )
        val frame = frame(
            state = frameState(moduleInstance = moduleInstance),
        )
        val tailRecursion = false

        val wasmFunctionCall: WasmFunctionCall = { _, _, _, _ ->
            fail("Wasm function should not be called in this scenario")
        }

        val hostFunctionCall: HostFunctionCall = { _store, _stack, _function ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(_function, functionInstance)

            Ok(Unit)
        }

        stack.push(frame)

        val actual = CallExecutorImpl(store, stack, functionIndex, tailRecursion, hostFunctionCall, wasmFunctionCall)

        assertEquals(Ok(Unit), actual)
    }
}
