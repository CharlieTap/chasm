package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutor
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.i32
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.host.HostModuleInstance
import io.github.charlietap.chasm.host.HostResources
import io.github.charlietap.chasm.host.readI32
import io.github.charlietap.chasm.host.writeI32
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import kotlin.contextOf
import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionInvokerTest {

    @Test
    fun `can invoke a function and return a result`() {

        val config = runtimeConfig()
        val params = mutableListOf<ExecutionValue>(i32(117))
        val address = functionAddress(0)
        val moduleInstance = moduleInstance(
            functionAddresses = mutableListOf(address),
        )
        val functionType = functionType(
            results = resultType(
                listOf(i32ValueType()),
            ),
        )
        val functionInstance = wasmFunctionInstance(
            functionType = functionType,
            module = moduleInstance,
        )
        val store = store(
            functions = mutableListOf(functionInstance),
        )

        val threadExecutor: ThreadExecutor = { actualConfig, actualStore, actualFunction, actualParams ->
            assertEquals(config, actualConfig)
            assertEquals(store, actualStore)
            assertEquals(functionInstance, actualFunction)
            assertEquals(params, actualParams)
            Ok(listOf(117L))
        }

        val actual = FunctionInvoker(
            config = config,
            store = store,
            instance = moduleInstance,
            function = functionInstance,
            values = params,
            threadExecutor = threadExecutor,
        )

        assertEquals(Ok(listOf(i32(117))), actual)
    }

    @Test
    fun `can invoke a host function and return a result`() {

        val config = runtimeConfig()
        val params = mutableListOf<ExecutionValue>(i32(117))
        val address = functionAddress(0)
        val moduleInstance = moduleInstance(
            functionAddresses = mutableListOf(address),
        )
        val functionType = functionType(
            params = resultType(listOf(i32ValueType())),
            results = resultType(listOf(i32ValueType())),
        )
        val runtimeStore = store()
        val functionInstance = hostFunctionInstance(
            functionType = functionType,
            function = { parameters, results ->
                val caller = contextOf<HostModuleInstance>()
                val context = contextOf<HostResources>()
                context as ExecutionContext
                assertEquals(config, context.config)
                assertEquals(runtimeStore, context.store)
                assertEquals(moduleInstance, caller)
                assertEquals(117, parameters.readI32(0))
                results.writeI32(0, 118)
            },
        )
        runtimeStore.functions += functionInstance

        val threadExecutor: ThreadExecutor = { _, _, _, _ ->
            error("thread executor should not be called for host functions")
        }

        val actual = FunctionInvoker(
            config = config,
            store = runtimeStore,
            instance = moduleInstance,
            address = address,
            values = params,
            threadExecutor = threadExecutor,
        )

        assertEquals(Ok(listOf(i32(118))), actual)
    }

    @Test
    fun `can directly invoke a resolved host function`() {
        val config = runtimeConfig()
        val params = listOf<ExecutionValue>(i32(117))
        val moduleInstance = moduleInstance()
        val runtimeStore = store()
        val functionInstance = hostFunctionInstance(
            functionType = functionType(
                params = resultType(listOf(i32ValueType())),
                results = resultType(listOf(i32ValueType())),
            ),
        ) { parameters, results ->
            val caller = contextOf<HostModuleInstance>()
            val context = contextOf<HostResources>()
            context as ExecutionContext
            assertEquals(config, context.config)
            assertEquals(runtimeStore, context.store)
            assertEquals(moduleInstance, caller)
            assertEquals(117, parameters.readI32(0))
            results.writeI32(0, 118)
        }
        val threadExecutor: ThreadExecutor = { _, _, _, _ ->
            error("thread executor should not be called for host functions")
        }

        val actual = FunctionInvoker(
            config = config,
            store = runtimeStore,
            instance = moduleInstance,
            function = functionInstance,
            values = params,
            threadExecutor = threadExecutor,
        )

        assertEquals(Ok(listOf(i32(118))), actual)
    }
}
