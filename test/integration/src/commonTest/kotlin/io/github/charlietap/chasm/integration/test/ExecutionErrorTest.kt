package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.embedding.collect
import io.github.charlietap.chasm.embedding.dropInstance
import io.github.charlietap.chasm.embedding.dropStore
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.memory.readInt
import io.github.charlietap.chasm.embedding.prepareFunction
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.HostFunctionException
import io.github.charlietap.chasm.host.hasPending
import io.github.charlietap.chasm.host.writeI32
import io.github.charlietap.chasm.integration.TestRunner
import io.github.charlietap.chasm.integration.TestScope
import io.github.charlietap.chasm.integration.assertTrapped
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import io.github.charlietap.chasm.type.AbstractHeapType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class ExecutionErrorTest {

    @Test
    fun `instantiating without a required import is unlinkable`() = integrationTest(FIXTURE) {

        val result = runner().instance()

        assertIs<ExecutionError.Unlinkable>(errorOf(result), engine.name())
    }

    @Test
    fun `instantiating a memory64 module is unsupported`() = integrationTest(MEMORY64_FIXTURE) {

        val result = runner().instance()

        assertIs<ExecutionError.Unsupported>(errorOf(result), engine.name())
    }

    @Test
    fun `instantiating a simd module is unsupported`() = integrationTest(SIMD_FIXTURE) {

        val result = runner().instance()

        assertIs<ExecutionError.Unsupported>(errorOf(result), engine.name())
    }

    @Test
    fun `instantiating a threads module is unsupported`() = integrationTest(THREADS_FIXTURE) {

        val result = runner().instance()

        assertIs<ExecutionError.Unsupported>(errorOf(result), engine.name())
    }

    @Test
    fun `a start function that throws returns the exception as thrown`() = integrationTest(START_THROW_FIXTURE) {

        val result = runner().instance()

        assertIs<ExecutionError.Thrown>(errorOf(result), engine.name())
    }

    @Test
    fun `a start function that traps returns its reason`() = integrationTest(START_TRAP_FIXTURE) {

        val result = runner().instance()

        assertTrapped(TrapReason.UNREACHABLE, result, engine.name())
    }

    @Test
    fun `instantiating into a dropped store throws`() = integrationTest(FIXTURE) {

        val store = engine.store()
        dropStore(store)

        assertFailsWith<IllegalStateException>(engine.name()) {
            runner(store)
        }
    }

    @Test
    fun `an invocation that traps returns its reason`() = integrationTest(FIXTURE) {

        val (_, runner) = runnerObservingPendingException()

        assertTrapped(TrapReason.UNREACHABLE, runner.invoke("trap"), engine.name())
    }

    @Test
    fun `a failing host function traps with a host function failure`() = integrationTest(FIXTURE) {

        val store = engine.store()
        val failing = function(store, functionType(), HostFunction { _, _ -> throw HostFunctionException("observe failed") })
        val runner = runner(store, listOf(Import("env", "observe", failing)))

        val result = runner.invoke("catch", listOf(ReferenceValue.Null(AbstractHeapType.Exception)))

        assertTrapped(TrapReason.HOST_FUNCTION_FAILURE, result, engine.name())
    }

    @Test
    fun `invoking a missing export is rejected`() = integrationTest(FIXTURE) {

        val (_, runner) = runnerObservingPendingException()

        assertIs<ExecutionError.Rejected>(errorOf(runner.invoke("missing")), engine.name())
    }

    @Test
    fun `invoking a dropped instance is rejected`() = integrationTest(FIXTURE) {

        val (_, runner) = runnerObservingPendingException()
        dropInstance(runner.instance().expect("instance"))

        assertIs<ExecutionError.Rejected>(errorOf(runner.invoke("trap")), engine.name())
    }

    @Test
    fun `invoking with the wrong arguments is rejected`() = integrationTest(FIXTURE) {

        val (_, runner) = runnerObservingPendingException()

        assertIs<ExecutionError.Rejected>(errorOf(runner.invoke("throw")), engine.name())
        assertIs<ExecutionError.Rejected>(errorOf(runner.invoke("throw", listOf(NumberValue.I64(42)))), engine.name())
    }

    @Test
    fun `preparing a missing export is rejected`() = integrationTest(FIXTURE) {

        val (_, runner) = runnerObservingPendingException()

        val result = prepareFunction(runner.instance().expect("instance"), "missing")

        assertIs<ExecutionError.Rejected>(errorOf(result), engine.name())
    }

    @Test
    fun `a prepared function rejects the wrong arguments and returns a thrown exception`() = integrationTest(FIXTURE) {

        val (_, runner) = runnerObservingPendingException()
        val prepared = prepareFunction(runner.instance().expect("instance"), "throw").expect("prepare")

        assertIs<ExecutionError.Rejected>(errorOf(prepared(emptyList())), engine.name())
        assertIs<ExecutionError.Rejected>(errorOf(prepared(listOf(NumberValue.I64(42)))), engine.name())

        val thrown = assertIs<ExecutionError.Thrown>(errorOf(prepared(listOf(NumberValue.I32(42)))), engine.name())
        assertEquals(
            ChasmResult.Success(listOf(NumberValue.I32(42))),
            runner.invoke("catch", listOf(ReferenceValue.Exception(thrown.exception))),
            engine.name(),
        )
    }

    @Test
    fun `a thrown exception survives collection and can be passed back into the next call`() = integrationTest(FIXTURE) {

        val pendingDuringCallback = mutableListOf<Boolean>()
        val (store, runner) = runnerObservingPendingException(pendingDuringCallback)

        val thrown = assertIs<ExecutionError.Thrown>(errorOf(runner.invoke("throw", listOf(NumberValue.I32(42)))), engine.name())

        collect(store)

        assertEquals(
            ChasmResult.Success(listOf(NumberValue.I32(42))),
            runner.invoke("catch", listOf(ReferenceValue.Exception(thrown.exception))),
            engine.name(),
        )
        assertEquals(listOf(false), pendingDuringCallback, engine.name())
    }

    @Test
    fun `a host function keeps an exception thrown by a nested call across another nested call`() = integrationTest(HOST_FIXTURE) {

        val store = engine.store()
        lateinit var instance: Instance
        val nested = function(
            store,
            functionType(results = resultType(listOf(i32ValueType()))),
            HostFunction { _, results ->

                val error = errorOf(invoke(instance, "throw", listOf(NumberValue.I32(42))))
                val exception = assertIs<ExecutionError.Thrown>(error).exception

                assertEquals(ChasmResult.Success(emptyList<ExecutionValue>()), invoke(instance, "noop"))

                val caught = assertIs<ChasmResult.Success<List<ExecutionValue>>>(
                    invoke(instance, "catch", listOf(ReferenceValue.Exception(exception))),
                )
                results.writeI32(0, assertIs<NumberValue.I32>(caught.result.single()).value)
            },
        )
        val escape = function(store, functionType(), HostFunction { _, _ -> })
        val runner = runner(store, listOf(Import("env", "nested", nested), Import("env", "escape", escape)))
        instance = runner.instance().expect("instance")

        val result = runner.invoke("call_nested")

        assertEquals(ChasmResult.Success(listOf(NumberValue.I32(42))), result, engine.name())
    }

    @Test
    fun `an exception a host function throws escapes the invocation`() = integrationTest(HOST_FIXTURE) {

        val store = engine.store()
        lateinit var memory: Memory
        val nested = function(store, functionType(results = resultType(listOf(i32ValueType()))), HostFunction { _, _ -> })
        val escape = function(
            store,
            functionType(),
            HostFunction { _, _ ->
                readInt(memory, PAGE_SIZE * 2)
            },
        )
        val runner = runner(store, listOf(Import("env", "nested", nested), Import("env", "escape", escape)))
        memory = assertNotNull(runner.exports().expect("exports").memory("memory"))

        assertFailsWith<IndexOutOfBoundsException>(engine.name()) {
            runner.invoke("call_escape")
        }
    }

    private suspend fun TestScope.runnerObservingPendingException(
        pendingDuringCallback: MutableList<Boolean> = mutableListOf(),
    ): Pair<Store, TestRunner> {

        val store = engine.store()
        val observe = function(
            store,
            functionType(),
            HostFunction { _, _ ->
                pendingDuringCallback.add(hasPending)
            },
        )

        return store to runner(store, listOf(Import("env", "observe", observe)))
    }

    private fun errorOf(result: ChasmResult<*, *>): Any? {
        return assertIs<ChasmResult.Error<*>>(result).error
    }

    private companion object {
        const val FIXTURE = "results"
        const val HOST_FIXTURE = "execution_error_host"
        const val START_THROW_FIXTURE = "results_start_throw"
        const val START_TRAP_FIXTURE = "trap_diagnostics_start"
        const val MEMORY64_FIXTURE = "memory64"
        const val SIMD_FIXTURE = "simd"
        const val THREADS_FIXTURE = "threads"
        const val PAGE_SIZE = 65_536
    }
}
