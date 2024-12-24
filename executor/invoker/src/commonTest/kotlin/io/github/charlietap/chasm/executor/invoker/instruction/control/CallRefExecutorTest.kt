package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.store
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class CallRefExecutorTest {

    @Test
    fun `can execute a callref instruction to a wasm function and return a result`() {

        val stack = stack()
        val functionInstance = wasmFunctionInstance()
        val functionAddress = functionAddress()
        val store = store(
            functions = mutableListOf(functionInstance),
        )
        val context = executionContext(stack, store)

        val tailRecursion = false
        val wasmFunctionCall: WasmFunctionCall = { _context, _function ->
            assertEquals(context, _context)
            assertEquals(functionInstance, _function)

            Ok(Unit)
        }

        val hostFunctionCall: HostFunctionCall = { _, _ ->
            fail("Host function should not be called in this scenario")
        }

        stack.push(ReferenceValue.Function(functionAddress))

        val actual = CallRefExecutor(context, tailRecursion, hostFunctionCall, wasmFunctionCall)

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `can execute a call to a host function and return a result`() {

        val stack = stack()
        val functionInstance = hostFunctionInstance()
        val store = store(
            functions = mutableListOf(functionInstance),
        )
        val context = executionContext(stack, store)

        val functionAddress = functionAddress()
        val tailRecursion = false

        val wasmFunctionCall: WasmFunctionCall = { _, _ ->
            fail("Wasm function should not be called in this scenario")
        }

        val hostFunctionCall: HostFunctionCall = { _context, _function ->
            assertEquals(context, _context)
            assertEquals(functionInstance, _function)

            Ok(Unit)
        }

        stack.push(ReferenceValue.Function(functionAddress))

        val actual = CallRefExecutor(context, tailRecursion, hostFunctionCall, wasmFunctionCall)

        assertEquals(Ok(Unit), actual)
    }
}
