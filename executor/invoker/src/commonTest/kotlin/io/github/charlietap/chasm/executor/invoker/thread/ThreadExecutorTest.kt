package io.github.charlietap.chasm.executor.invoker.thread

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.GCThreshold
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.GarbageCollector
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.EndFunctionDispatcher
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.fixture.ast.instruction.catchCatchHandler
import io.github.charlietap.chasm.fixture.ast.module.labelIndex
import io.github.charlietap.chasm.fixture.ast.module.tagIndex
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.i32
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.finalSubType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.recursiveType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.structCompositeType
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.gc.GuestHeapOutOfMemoryException
import io.github.charlietap.chasm.host.HostException
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.program.Program
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RTT
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ThreadExecutorTest {

    @Test
    fun `host raised exception resumes at a matching guest catch`() {
        val program = Program()
        val store = store(program = program)
        val tagAddress = store.heap.registerTag(
            rtt = RTT(0),
            type = tagType(
                functionType = functionType(
                    params = resultType(listOf(i64ValueType())),
                ),
            ),
        )
        val exceptionReference = store.heap.allocateException(
            tagAddress = tagAddress,
            fields = longArrayOf(0x123456789ABCDEFL),
        )
        val module = moduleInstance(tagAddresses = mutableListOf(tagAddress))
        val hostFunction = hostFunctionInstance(
            function = HostFunction { _, _ ->
                store.heap.raise(HostException(exceptionReference))
            },
        )
        val entryIp = program.append(
            arrayOf(
                DispatchableInstruction { vstack, context, _ ->
                    context.cstack.push(
                        ExceptionHandler(
                            handlers = listOf(catchCatchHandler(tagIndex(0u), labelIndex(0u))),
                            payloadDestinationSlots = listOf(intArrayOf(0)),
                            continuationIps = intArrayOf(1),
                            instance = module,
                            fp = vstack.fp,
                            sp = vstack.sp,
                        ),
                    )
                    HostFunctionCall(vstack, context, module, hostFunction, 0, 0)
                    error("raised exception returned to the host call site")
                },
                EndFunctionDispatcher(AdminInstruction.EndFunction(1, 1)),
            ),
        )
        val function = wasmFunctionInstance(
            module = module,
            functionType = functionType(results = resultType(listOf(i64ValueType()))),
            function = runtimeFunction(
                body = runtimeExpression(entryIp),
                frameSlots = 2,
            ),
        )

        val actual = ThreadExecutor(runtimeConfig(), store, function, emptyList())

        assertEquals(Ok(listOf(0x123456789ABCDEFL)), actual)
        assertFalse(store.heap.hasPending)
    }

    @Test
    fun `uncaught host raised exception returns to the host`() {
        val program = Program()
        val store = store(program = program)
        val exceptionReference = store.heap.allocateException(
            tagAddress = store.heap.registerTag(
                rtt = RTT(0),
                type = tagType(),
            ),
            fields = LongArray(0),
        )
        val module = moduleInstance()
        val hostFunction = hostFunctionInstance(
            function = HostFunction { _, _ ->
                store.heap.raise(HostException(exceptionReference))
            },
        )
        val entryIp = program.append(
            DispatchableInstruction { vstack, context, _ ->
                HostFunctionCall(vstack, context, module, hostFunction, 0, 0)
                error("raised exception returned to the host call site")
            },
        )
        val function = wasmFunctionInstance(
            module = module,
            function = runtimeFunction(body = runtimeExpression(entryIp)),
        )

        val actual = ThreadExecutor(runtimeConfig(), store, function, emptyList())

        assertEquals(Err(InvocationError.ThrownException), actual)
        assertEquals(exceptionReference, store.heap.takePendingExceptionReference())
    }

    @Test
    fun `guest heap exhaustion becomes a deterministic invocation error`() {
        val program = Program()
        val entryIp = program.append(
            arrayOf(
                DispatchableInstruction { _, _, _ ->
                    throw GuestHeapOutOfMemoryException("injected configured capacity exhaustion")
                },
            ),
        )
        val function = wasmFunctionInstance(
            module = moduleInstance(),
            function = runtimeFunction(body = runtimeExpression(entryIp)),
        )
        val store = store(program = program)

        val actual = ThreadExecutor(runtimeConfig(), store, function, emptyList())

        assertEquals(Err(InvocationError.GuestHeapOutOfMemory), actual)
    }

    @Test
    fun `executes a program and returns its results`() {
        val program = Program()
        val entryIp = program.append(
            arrayOf(
                DispatchableInstruction { vstack, _, nextIp ->
                    vstack.setFrameSlot(0, 0L)
                    nextIp
                },
                EndFunctionDispatcher(AdminInstruction.EndFunction(1, 2)),
            ),
        )
        val module = moduleInstance()
        val function = wasmFunctionInstance(
            module = module,
            functionType = functionType(
                params = resultType(listOf(i32ValueType(), i32ValueType())),
                results = resultType(listOf(i32ValueType())),
            ),
            function = runtimeFunction(
                body = runtimeExpression(entryIp),
                frameSlots = 3,
            ),
        )

        val actual = ThreadExecutor(
            config = runtimeConfig(),
            store = store(program = program),
            instance = function,
            values = listOf(i32(2), i32(3)),
        )

        assertEquals(Ok(listOf(0L)), actual)
    }

    @Test
    fun `root results remain in the overlapping interface`() {
        val program = Program()
        val entryIp = program.append(
            arrayOf(
                DispatchableInstruction { vstack, _, nextIp ->
                    vstack.setFrameSlot(0, 41L)
                    vstack.setFrameSlot(1, 42L)
                    nextIp
                },
                EndFunctionDispatcher(AdminInstruction.EndFunction(resultCount = 2, activationHeaderSlot = 2)),
            ),
        )
        val function = wasmFunctionInstance(
            module = moduleInstance(),
            functionType = functionType(
                results = resultType(listOf(i32ValueType(), i32ValueType())),
            ),
            function = runtimeFunction(
                body = runtimeExpression(entryIp),
                frameSlots = 3,
            ),
        )

        val actual = ThreadExecutor(
            config = runtimeConfig(),
            store = store(program = program),
            instance = function,
            values = emptyList(),
        )

        assertEquals(Ok(listOf(41L, 42L)), actual)
    }

    @Test
    fun `collects garbage after the invocation frame has been removed`() {
        val program = Program()
        val entryIp = program.append(
            arrayOf(
                DispatchableInstruction { vstack, _, nextIp ->
                    vstack.setFrameSlot(0, 117L)
                    vstack.setFrameSlot(2, 999L)
                    nextIp
                },
                EndFunctionDispatcher(AdminInstruction.EndFunction(1, 1)),
            ),
        )
        val module = moduleInstance()
        val function = wasmFunctionInstance(
            module = module,
            functionType = functionType(
                results = resultType(listOf(i32ValueType())),
            ),
            function = runtimeFunction(
                body = runtimeExpression(entryIp),
                frameSlots = 3,
            ),
        )
        val store = store(program = program)
        allocateUnreachableGuestObject(store)
        var collected = false
        val collector: GarbageCollector = { _, stack ->
            collected = true
            assertEquals(1, stack?.sp)
            assertEquals(117L, stack?.getFrameSlot(0))
            Ok(Unit)
        }

        val actual = ThreadExecutor(
            config = RuntimeConfig(
                gcStrategy = GCStrategy.ARENA,
                gcThreshold = GCThreshold.KB(0),
            ),
            store = store,
            instance = function,
            values = emptyList(),
            garbageCollector = collector,
        )

        assertEquals(Ok(listOf(117L)), actual)
        assertTrue(collected)
    }

    @Test
    fun `manual and traditional strategies do not collect after invocation`() {
        val program = Program()
        val entryIp = program.append(
            arrayOf(
                EndFunctionDispatcher(AdminInstruction.EndFunction(0, 0)),
            ),
        )
        val module = moduleInstance()
        val function = wasmFunctionInstance(
            module = module,
            function = runtimeFunction(body = runtimeExpression(entryIp)),
        )
        val store = store(program = program)
        allocateUnreachableGuestObject(store)
        var collected = false
        val collector: GarbageCollector = { _, _ ->
            collected = true
            Ok(Unit)
        }

        for (strategy in listOf(GCStrategy.MANUAL, GCStrategy.TRADITIONAL)) {
            val actual = ThreadExecutor(
                config = RuntimeConfig(
                    gcStrategy = strategy,
                    gcThreshold = GCThreshold.KB(0),
                ),
                store = store,
                instance = function,
                values = emptyList(),
                garbageCollector = collector,
            )

            assertEquals(Ok(emptyList()), actual)
        }
        assertFalse(collected)
    }

    @Test
    fun `collector failure is returned`() {
        val program = Program()
        val entryIp = program.append(
            arrayOf(EndFunctionDispatcher(AdminInstruction.EndFunction(0, 0))),
        )
        val function = wasmFunctionInstance(
            module = moduleInstance(),
            function = runtimeFunction(body = runtimeExpression(entryIp)),
        )
        val store = store(program = program)
        allocateUnreachableGuestObject(store)
        val failure = InvocationError.GarbageCollectionFailed("injected")

        val actual = ThreadExecutor(
            config = RuntimeConfig(
                gcStrategy = GCStrategy.ARENA,
                gcThreshold = GCThreshold.KB(0),
            ),
            store = store,
            instance = function,
            values = emptyList(),
            garbageCollector = { _, _ -> Err(failure) },
        )

        assertEquals(Err(failure), actual)
    }

    private fun allocateUnreachableGuestObject(store: Store) {
        val types = listOf(
            definedType(
                recursiveType = recursiveType(
                    subTypes = listOf(finalSubType(compositeType = structCompositeType())),
                ),
            ),
        )
        val runtimeType = store.heap.registerRuntimeTypes(types)[0]
        store.heap.allocateStruct(runtimeType, LongArray(0))
    }
}
