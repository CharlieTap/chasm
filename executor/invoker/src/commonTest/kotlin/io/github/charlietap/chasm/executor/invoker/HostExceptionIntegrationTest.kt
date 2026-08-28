package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.EndFunctionDispatcher
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.instruction.control.ThrowRefValueExecutor
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutor
import io.github.charlietap.chasm.fixture.ast.instruction.catchAllRefHandler
import io.github.charlietap.chasm.fixture.ast.module.labelIndex
import io.github.charlietap.chasm.fixture.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.withExceptions
import io.github.charlietap.chasm.host.writeI64
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.program.Program
import io.github.charlietap.chasm.runtime.type.RTT
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HostExceptionIntegrationTest {

    @Test
    fun `host can inspect and swallow an exception from a nested guest invocation`() {
        val config = RuntimeConfig()
        val program = Program()
        val runtimeStore = store(program = program)
        val tagAddress = runtimeStore.heap.registerTag(
            RTT(0),
            tagType(
                functionType = functionType(
                    params = resultType(listOf(i64ValueType())),
                ),
            ),
        )
        val module = moduleInstance(tagAddresses = mutableListOf(tagAddress))
        val exceptionReference = runtimeStore.heap.allocateException(tagAddress, longArrayOf(42L))
        val nested = escapingFunction(program, module, exceptionReference)
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
                DispatchableInstruction { vstack, _, _, context, nextIp ->
                    HostFunctionCall(
                        vstack = vstack,
                        context = context,
                        caller = module,
                        function = bridge,
                        parameterSlotBase = 0,
                        resultSlotBase = 0,
                    )
                    nextIp
                },
                EndFunctionDispatcher(AdminInstruction.EndFunction(1, 1)),
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
        val config = RuntimeConfig()
        val program = Program()
        val runtimeStore = store(program = program)
        val tagAddress = runtimeStore.heap.registerTag(RTT(0), tagType())
        val module = moduleInstance(tagAddresses = mutableListOf(tagAddress))
        val exceptionReference = runtimeStore.heap.allocateException(tagAddress, LongArray(0))
        val nested = escapingFunction(program, module, exceptionReference)
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
        val continuationIp = program.size + 1
        val entryIp = program.append(
            arrayOf(
                DispatchableInstruction { vstack, cstack, _, context, _ ->
                    cstack.push(
                        ExceptionHandler(
                            handlers = listOf(catchAllRefHandler(labelIndex(0u))),
                            payloadDestinationSlots = listOf(intArrayOf(0)),
                            continuationIps = intArrayOf(continuationIp),
                            instance = module,
                            fp = vstack.fp,
                            sp = vstack.sp,
                        ),
                    )
                    HostFunctionCall(vstack, context, module, bridge, 0, 0)
                    error("pending exception returned to the host call site")
                },
                EndFunctionDispatcher(AdminInstruction.EndFunction(1, 1)),
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
                    DispatchableInstruction { vstack, cstack, store, _, _ ->
                        ThrowRefValueExecutor(vstack, cstack, store, exceptionReference)
                    },
                ),
            ),
        ),
    )
}
