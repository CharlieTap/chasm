package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.EndFunctionDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.CallDispatcher
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.instruction.control.ThrowRefValueExecutor
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutor
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.runtime.exception.compiledCatch
import io.github.charlietap.chasm.fixture.runtime.exception.exceptionRegion
import io.github.charlietap.chasm.fixture.runtime.exception.functionExceptionTable
import io.github.charlietap.chasm.fixture.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instruction.endFunctionAdminInstruction
import io.github.charlietap.chasm.fixture.runtime.instruction.hostCallRuntimeInstruction
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.type.rtt
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.withExceptions
import io.github.charlietap.chasm.host.writeI64
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.CompiledCatch
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.program.Program
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HostExceptionIntegrationTest {

    @Test
    fun `host can inspect and swallow an exception from a nested guest invocation`() {
        val config = runtimeConfig()
        val program = Program()
        val runtimeStore = store(program = program)
        val tagAddress = runtimeStore.heap.registerTag(
            rtt(),
            tagType(
                functionType = functionType(
                    params = resultType(listOf(i64ValueType())),
                ),
            ),
        )
        val module = moduleInstance(tagAddresses = mutableListOf(tagAddress))
        val exceptionReference = runtimeStore.heap.allocateException(tagAddress, longArrayOf(42L))
        val nested = escapingFunction(program, module, exceptionReference)
        runtimeStore.functions.add(nested)
        val bridge = hostFunctionInstance(
            functionType = functionType(results = resultType(listOf(i64ValueType()))),
            function = HostFunction { _, results ->
                val invocation = FunctionInvoker(config, runtimeStore, module, nested, emptyList())
                assertEquals(Err(InvocationError.ThrownException), invocation)

                withExceptions {
                    assertTrue(hasPending)
                    val exception = takePending()
                    assertEquals(tagAddress.address, tag(exception).rawAddress)
                    assertEquals(1, payloadSize(exception))
                    results.writeI64(0, readPayload(exception, 0))
                }
            },
        )
        val entryIp = program.append(
            arrayOf(
                dispatchableInstruction { vstack, context ->
                    HostFunctionCall(
                        vstack = vstack,
                        context = context,
                        caller = module,
                        function = bridge,
                        parameterSlotBase = 0,
                        resultSlotBase = 0,
                    )
                },
                EndFunctionDispatcher(endFunctionAdminInstruction(resultCount = 1)),
            ),
        )
        val outer = wasmFunctionInstance(
            module = module,
            functionType = functionType(results = resultType(listOf(i64ValueType()))),
            function = runtimeFunction(
                body = runtimeExpression(entryIp),
                frameSlots = 1,
            ),
        )

        val actual = ThreadExecutor(config, runtimeStore, outer, emptyList())

        assertEquals(Ok(listOf(42L)), actual)
        assertFalse(runtimeStore.heap.hasPending)
    }

    @Test
    fun `host can pass the same pending exception back to an outer guest`() {
        val config = runtimeConfig()
        val program = Program()
        val runtimeStore = store(program = program)
        val tagAddress = runtimeStore.heap.registerTag(rtt(), tagType())
        val module = moduleInstance(tagAddresses = mutableListOf(tagAddress))
        val exceptionReference = runtimeStore.heap.allocateException(tagAddress, LongArray(0))
        val nested = escapingFunction(program, module, exceptionReference)
        runtimeStore.functions.add(nested)
        val bridge = hostFunctionInstance(
            function = HostFunction { _, _ ->
                val invocation = FunctionInvoker(config, runtimeStore, module, nested, emptyList())
                assertEquals(Err(InvocationError.ThrownException), invocation)
                withExceptions {
                    assertTrue(hasPending)
                    raisePending()
                }
            },
        )
        val entryIp = program.append(
            arrayOf(
                CallDispatcher(hostCallRuntimeInstruction(instance = bridge, caller = module)),
                EndFunctionDispatcher(endFunctionAdminInstruction(resultCount = 1)),
            ),
        )
        program.registerExceptionTable(
            functionExceptionTable(
                entryIp,
                2,
                1,
                1,
                arrayOf(
                    exceptionRegion(
                        0,
                        1,
                        -1,
                        arrayOf(
                            compiledCatch(CompiledCatch.CATCH_ALL_TAG, 1, intArrayOf(0), true, 2),
                        ),
                    ),
                ),
                intArrayOf(),
            ),
        )
        val outer = wasmFunctionInstance(
            module = module,
            functionType = functionType(results = resultType(listOf(i64ValueType()))),
            function = runtimeFunction(
                body = runtimeExpression(entryIp),
                frameSlots = 1,
            ),
        )

        val actual = ThreadExecutor(config, runtimeStore, outer, emptyList())

        assertEquals(Ok(listOf(exceptionReference)), actual)
        assertFalse(runtimeStore.heap.hasPending)
    }

    private fun escapingFunction(
        program: Program,
        module: ModuleInstance,
        exceptionReference: Long,
    ) = wasmFunctionInstance(
        module = module,
        function = runtimeFunction(
            body = runtimeExpression(
                program.append(
                    DispatchableInstruction { vstack, context, nextIp ->
                        ThrowRefValueExecutor(vstack, context, exceptionReference, nextIp - 1)
                    },
                ),
            ),
        ),
    )
}
