package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutor
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Configuration
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.Thread
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.module.function
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.functionType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class FunctionInvokerImplTest {

    @Test
    fun `can invoke a function and return a result`() {

        val locals = mutableListOf<ExecutionValue>(NumberValue.I32(117))
        val address = Address.Function(0)
        val moduleInstance = moduleInstance(
            functionAddresses = mutableListOf(address),
        )
        val functionType = functionType()
        val function = function()
        val functionInstance = FunctionInstance.WasmFunction(
            functionType,
            moduleInstance,
            function,
        )

        val store = store(
            functions = mutableListOf(functionInstance),
        )

        val thread = Thread(
            Stack.Entry.ActivationFrame(
                Arity(functionType.results.types.size),
                Stack.Entry.ActivationFrame.State(
                    locals,
                    moduleInstance,
                ),
            ),
            listOf(ControlInstruction.Call(Index.FunctionIndex(0u))),
        )
        val expectedConfig = Configuration(store, thread)

        val threadExecutor: ThreadExecutor = { config ->
            assertEquals(expectedConfig, config)
            Ok(listOf(NumberValue.I32(117)))
        }

        val actual = FunctionInvokerImpl(
            store = store,
            address = address,
            values = locals,
            threadExecutor = threadExecutor,
        )

        assertEquals(Ok(listOf(NumberValue.I32(117))), actual)
    }

    @Test
    fun `throws error if address does not exist`() {

        val locals = mutableListOf<ExecutionValue>(NumberValue.I32(117))
        val address = Address.Function(0)
        val moduleInstance = moduleInstance()
        val functionType = functionType()
        val function = function()
        val functionInstance = FunctionInstance.WasmFunction(
            functionType,
            moduleInstance,
            function,
        )

        val store = store(
            functions = mutableListOf(functionInstance),
        )

        val threadExecutor: ThreadExecutor = { _ ->
            fail("thread executor should not be called")
        }

        val actual = FunctionInvokerImpl(
            store = store,
            address = address,
            values = locals,
            threadExecutor = threadExecutor,
        )

        assertEquals(Err(InvocationError.InvalidAddress), actual)
    }
}
