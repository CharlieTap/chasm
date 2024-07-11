package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.frameState
import io.github.charlietap.chasm.fixture.instance.functionAddress
import io.github.charlietap.chasm.fixture.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.instance.tableAddress
import io.github.charlietap.chasm.fixture.instance.tableInstance
import io.github.charlietap.chasm.fixture.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.module.tableIndex
import io.github.charlietap.chasm.fixture.module.typeIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.value.i32
import io.github.charlietap.chasm.type.ext.definedType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class CallIndirectExecutorImplTest {

    @Test
    fun `can execute a call indirect through a table to a wasm function and return a result`() {

        val stack = stack()

        val typeIndex = typeIndex(0u)
        val functionType = functionType()
        val definedType = functionType.definedType()
        val functionInstance = wasmFunctionInstance(
            type = definedType,
        )

        val functionAddress = functionAddress()

        val tableIndex = tableIndex(0u)
        val tableAddress = tableAddress()
        val tableInstance = tableInstance(
            elements = arrayOf(ReferenceValue.Function(functionAddress)),
        )
        val elementIndex = 0

        stack.push(Stack.Entry.Value(i32(elementIndex)))

        val store = store(
            functions = mutableListOf(functionInstance),
            tables = mutableListOf(tableInstance),
        )
        val moduleInstance = moduleInstance(
            functionAddresses = mutableListOf(functionAddress),
            tableAddresses = mutableListOf(tableAddress),
            types = listOf(definedType),
        )

        val frame = frame(
            state = frameState(moduleInstance = moduleInstance),
        )

        stack.push(frame)

        val tailRecursion = true

        val wasmFunctionCall: WasmFunctionCall = { _store, _stack, _function, _tailRecursion ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(_function, functionInstance)
            assertEquals(_tailRecursion, tailRecursion)

            Ok(Unit)
        }

        val hostFunctionCall: HostFunctionCall = { _, _, _ ->
            fail("Host function should not be called in this scenario")
        }

        val actual = CallIndirectExecutorImpl(store, stack, tableIndex, typeIndex, tailRecursion, hostFunctionCall, wasmFunctionCall)

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `can execute a call to a host function and return a result`() {

        val stack = stack()

        val typeIndex = typeIndex(0u)
        val functionType = functionType()
        val definedType = functionType.definedType()
        val functionInstance = hostFunctionInstance(
            type = definedType,
        )
        val functionAddress = functionAddress()

        val tableIndex = tableIndex(0u)
        val tableAddress = tableAddress()
        val tableInstance = tableInstance(
            elements = arrayOf(ReferenceValue.Function(functionAddress)),
        )
        val elementIndex = 0

        stack.push(Stack.Entry.Value(i32(elementIndex)))

        val store = store(
            functions = mutableListOf(functionInstance),
            tables = mutableListOf(tableInstance),
        )
        val moduleInstance = moduleInstance(
            functionAddresses = mutableListOf(functionAddress),
            tableAddresses = mutableListOf(tableAddress),
            types = listOf(definedType),
        )

        val frame = frame(
            state = frameState(moduleInstance = moduleInstance),
        )

        stack.push(frame)

        val tailRecursion = false

        val wasmFunctionCall: WasmFunctionCall = { _, _, _, _ ->
            fail("Host function should not be called in this scenario")
        }

        val hostFunctionCall: HostFunctionCall = { _store, _stack, _function ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(_function, functionInstance)

            Ok(Unit)
        }

        val actual = CallIndirectExecutorImpl(store, stack, tableIndex, typeIndex, tailRecursion, hostFunctionCall, wasmFunctionCall)

        assertEquals(Ok(Unit), actual)
    }
}
