package io.github.charlietap.chasm.executor.invoker.thread

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.GCThreshold
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.GarbageCollector
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.EndFunctionDispatcher
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.i32
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.finalSubType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.recursiveType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.structCompositeType
import io.github.charlietap.chasm.gc.GuestHeapOutOfMemoryError
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.program.Program
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ThreadExecutorTest {

    @Test
    fun `guest heap exhaustion becomes a deterministic invocation error`() {
        val program = Program()
        val entryIp = program.append(
            arrayOf(
                DispatchableInstruction { _, _, _, _, _ ->
                    throw GuestHeapOutOfMemoryError("injected configured capacity exhaustion")
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
                DispatchableInstruction { vstack, _, _, _, nextIp ->
                    vstack.setFrameSlot(0, 0L)
                    nextIp
                },
                EndFunctionDispatcher(AdminInstruction.EndFunction),
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
                frameSlots = 2,
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
    fun `collects garbage after the invocation frame has been removed`() {
        val program = Program()
        val entryIp = program.append(
            arrayOf(
                DispatchableInstruction { vstack, _, _, _, nextIp ->
                    vstack.setFrameSlot(0, 117L)
                    vstack.setFrameSlot(1, 999L)
                    nextIp
                },
                EndFunctionDispatcher(AdminInstruction.EndFunction),
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
                frameSlots = 2,
            ),
        )
        val store = store(program = program)
        allocateUnreachableGuestObject(store)
        var collected = false
        val collector: GarbageCollector = { _, stack ->
            collected = true
            assertEquals(listOf(117L), stack?.iterator()?.asSequence()?.toList())
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
                EndFunctionDispatcher(AdminInstruction.EndFunction),
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
            arrayOf(EndFunctionDispatcher(AdminInstruction.EndFunction)),
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

    private fun allocateUnreachableGuestObject(store: io.github.charlietap.chasm.runtime.store.Store) {
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
