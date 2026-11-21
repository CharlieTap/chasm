package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.embedding.compiled.Import
import io.github.charlietap.chasm.embedding.compiled.Instance
import io.github.charlietap.chasm.embedding.compiled.Store
import io.github.charlietap.chasm.embedding.compiled.function
import io.github.charlietap.chasm.embedding.compiled.host.HostFunction
import io.github.charlietap.chasm.embedding.compiled.host.writeI64
import io.github.charlietap.chasm.embedding.compiled.instance
import io.github.charlietap.chasm.embedding.compiled.invoke
import io.github.charlietap.chasm.embedding.compiled.value.NumberValue.I32
import io.github.charlietap.chasm.embedding.dsl.FunctionTypeBuilder
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.expect
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AllocationLimitsTest {

    @Test
    fun exhaustingTheHeapTrapsAndLeavesTheStoreUsable() {

        val (store, instance) = instantiate()

        val exhausted = invoke(store, instance, "grow", listOf(I32(Int.MAX_VALUE)))
        invoke(store, instance, "release", emptyList()).expect("release")
        invoke(store, instance, "grow", listOf(I32(4))).expect("grow")

        assertEquals(TrapReason.GUEST_HEAP_EXHAUSTED, trapReason(exhausted))
    }

    @Test
    fun arraysBeyondTheLengthCapTrap() {

        val (store, instance) = instantiate()

        val huge = invoke(store, instance, "huge", emptyList())

        assertEquals(TrapReason.GUEST_HEAP_EXHAUSTED, trapReason(huge))
    }

    private fun trapReason(result: ChasmResult<*, *>): TrapReason {
        return assertIs<ExecutionError.Trapped>(assertIs<ChasmResult.Error<*>>(result).error).trap.reason
    }

    private fun instantiate(): Pair<Store, Instance> {

        val store = store()
        val module = runBlocking { module(resourceBytes("modules/allocation_limits.wasm"), validate = true) }.expect("module")
        val allocated = function(store, FunctionTypeBuilder().apply { results { i64() } }.build(), HostFunction { _, results -> results.writeI64(0, 0L) })
        val instance = runBlocking { instance(store, module, listOf(Import("host", "allocated", allocated))) }.expect("instance")

        return store to instance
    }
}
