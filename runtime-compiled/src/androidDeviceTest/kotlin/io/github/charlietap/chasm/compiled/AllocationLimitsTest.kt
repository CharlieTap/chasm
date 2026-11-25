package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.embedding.dsl.FunctionTypeBuilder
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.writeI64
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AllocationLimitsTest {

    @Test
    fun exhaustingTheHeapTrapsAndLeavesTheStoreUsable() {

        val instance = instantiate()

        val exhausted = invoke(instance, "grow", listOf(I32(Int.MAX_VALUE)))
        invoke(instance, "release", emptyList()).expect("release")
        invoke(instance, "grow", listOf(I32(4))).expect("grow")

        assertEquals(TrapReason.GUEST_HEAP_EXHAUSTED, trapReason(exhausted))
    }

    @Test
    fun arraysBeyondTheLengthCapTrap() {

        val instance = instantiate()

        val huge = invoke(instance, "huge", emptyList())

        assertEquals(TrapReason.GUEST_HEAP_EXHAUSTED, trapReason(huge))
    }

    private fun trapReason(result: ChasmResult<*, *>): TrapReason {
        return assertIs<ExecutionError.Trapped>(assertIs<ChasmResult.Error<*>>(result).error).trap.reason
    }

    private fun instantiate(): Instance {

        val module = dexModule("allocation_limits")
        val store = store(module)
        val allocated = function(store, FunctionTypeBuilder().apply { results { i64() } }.build(), HostFunction { _, results -> results.writeI64(0, 0L) })

        return runBlocking { instance(store, module, listOf(Import("host", "allocated", allocated))) }.expect("instance")
    }
}
