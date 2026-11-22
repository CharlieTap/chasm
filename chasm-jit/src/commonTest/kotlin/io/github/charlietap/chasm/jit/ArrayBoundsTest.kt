package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.embedding.dsl.FunctionTypeBuilder
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.host.HostFunction
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals

class ArrayBoundsTest {

    private val store = store()
    private val instance by lazy { instantiate(store) }

    @Test
    fun inBoundsAccessesSucceedForEveryElementKind() {
        for (kind in KINDS) {
            assertEquals(I32(1), call("get_$kind", 3).expect("get_$kind").single())
            assertEquals(I32(1), call("set_$kind", 0).expect("set_$kind").single())
        }
    }

    @Test
    fun outOfBoundsAccessesTrapForEveryElementKind() {
        for (kind in KINDS) {
            for (index in OUT_OF_BOUNDS) {
                assertArrayTrap(call("get_$kind", index))
                assertArrayTrap(call("set_$kind", index))
            }
        }
    }

    @Test
    fun aFailedSetLeavesTheArrayUnchanged() {

        invoke(instance, "set_shared", listOf(I32(1), I32(7))).expect("set")

        assertArrayTrap(invoke(instance, "set_shared", listOf(I32(4), I32(9))))
        assertEquals(I32(7), call("read_shared", 1).expect("read").single())
        assertEquals(I32(0), call("read_shared", 3).expect("read").single())
    }

    @Test
    fun arrayTrapsAreNotCaughtByCatchAll() {
        assertArrayTrap(call("trap_inside_catch_all", 4))
    }

    @Test
    fun hostArrayIndexFailuresAreNotReportedAsArrayTraps() {

        val result = runCatching { invoke(instance, "host_failure", emptyList()) }
        val failure = result.exceptionOrNull()

        if (failure != null) {
            assertIs<ArrayIndexOutOfBoundsException>(failure)
        } else {
            val error = assertIs<ChasmResult.Error<*>>(result.getOrThrow()).error
            assertNotEquals(TrapReason.ARRAY_OUT_OF_BOUNDS, (error as? ExecutionError.Trapped)?.trap?.reason)
        }
        assertEquals(I32(1), call("get_ints", 0).expect("get_ints").single())
    }

    private fun assertArrayTrap(result: ChasmResult<*, *>) {
        val error = assertIs<ChasmResult.Error<*>>(result).error
        assertEquals(TrapReason.ARRAY_OUT_OF_BOUNDS, assertIs<ExecutionError.Trapped>(error).trap.reason, error.toString())
    }

    private fun call(
        name: String,
        index: Int,
    ) = invoke(instance, name, listOf(I32(index)))

    private fun instantiate(store: Store): Instance {

        val explode = function(store, FunctionTypeBuilder().build(), HostFunction { _, _ -> throw ArrayIndexOutOfBoundsException("host") })
        val module = runBlocking { module(resourceBytes("modules/array_bounds.wasm"), validate = true) }.expect("module")

        return runBlocking { instance(store, module, listOf(Import("host", "explode", explode))) }.expect("instance")
    }

    private companion object {
        val KINDS = listOf("bytes", "shorts", "ints", "longs", "floats", "doubles", "refs")
        val OUT_OF_BOUNDS = listOf(-1, 4, Int.MIN_VALUE, Int.MAX_VALUE)
    }
}
