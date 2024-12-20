package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.ast.module.tableIndex
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import io.github.charlietap.chasm.fixture.ast.type.functionType
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.tableAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.tableInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import io.github.charlietap.chasm.type.ext.definedType
import io.github.charlietap.chasm.type.matching.TypeMatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class CallIndirectExecutorTest {

    @Test
    fun `can execute a call indirect through a table to a wasm function and return a result`() {

        val functionAddress = functionAddress()
        val typeIndex = typeIndex(0u)
        val functionType = functionType()
        val definedType = functionType.definedType()
        val functionInstance = wasmFunctionInstance(
            type = definedType,
        )

        val tableIndex = tableIndex(0u)
        val tableAddress = tableAddress()
        val tableInstance = tableInstance(
            elements = arrayOf(ReferenceValue.Function(functionAddress)),
        )
        val elementIndex = 0

        val stack = stack()
        val store = store(
            functions = mutableListOf(functionInstance),
            tables = mutableListOf(tableInstance),
        )
        val context = executionContext(stack, store)

        stack.push(Stack.Entry.Value(i32(elementIndex)))

        val moduleInstance = moduleInstance(
            functionAddresses = mutableListOf(functionAddress),
            tableAddresses = mutableListOf(tableAddress),
            types = listOf(definedType),
        )

        val frame = frame(
            instance = moduleInstance,
        )

        stack.push(frame)

        val tailRecursion = true

        val wasmFunctionCall: WasmFunctionCall = { _context, _function ->
            assertEquals(context, _context)
            assertEquals(_function, functionInstance)

            Ok(Unit)
        }

        val hostFunctionCall: HostFunctionCall = { _, _ ->
            fail("Host function should not be called in this scenario")
        }

        val definedTypeMatcher: TypeMatcher<DefinedType> = { _, _, _ ->
            true
        }

        val actual = CallIndirectExecutor(
            context = context,
            tableIndex = tableIndex,
            typeIndex = typeIndex,
            tailRecursion = tailRecursion,
            hostFunctionCall = hostFunctionCall,
            wasmFunctionCall = wasmFunctionCall,
            definedTypeMatcher = definedTypeMatcher,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `can execute a call to a host function and return a result`() {

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

        val stack = stack()
        val store = store(
            functions = mutableListOf(functionInstance),
            tables = mutableListOf(tableInstance),
        )
        val context = executionContext(stack, store)

        stack.push(Stack.Entry.Value(i32(elementIndex)))

        val moduleInstance = moduleInstance(
            functionAddresses = mutableListOf(functionAddress),
            tableAddresses = mutableListOf(tableAddress),
            types = listOf(definedType),
        )

        val frame = frame(
            instance = moduleInstance,
        )

        stack.push(frame)

        val tailRecursion = false

        val wasmFunctionCall: WasmFunctionCall = { _, _ ->
            fail("Host function should not be called in this scenario")
        }

        val hostFunctionCall: HostFunctionCall = { _context, _function ->
            assertEquals(context, _context)
            assertEquals(functionInstance, _function)

            Ok(Unit)
        }

        val definedTypeMatcher: TypeMatcher<DefinedType> = { _, _, _ ->
            true
        }

        val actual = CallIndirectExecutor(
            context = context,
            tableIndex = tableIndex,
            typeIndex = typeIndex,
            tailRecursion = tailRecursion,
            hostFunctionCall = hostFunctionCall,
            wasmFunctionCall = wasmFunctionCall,
            definedTypeMatcher = definedTypeMatcher,
        )

        assertEquals(Ok(Unit), actual)
    }
}
