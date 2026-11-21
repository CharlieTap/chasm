package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.embedding.dsl.FunctionTypeBuilder
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.withGc
import io.github.charlietap.chasm.host.writeI64
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.value.NumberValue.I32
import io.github.charlietap.chasm.runtime.value.NumberValue.I64
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AllocationLimitsTest {

    @Test
    fun exhaustingTheHeapTrapsAndLeavesTheStoreUsable() {

        val (store, instance) = instantiate()

        val exhausted = invoke(store, instance, "grow", listOf(I32(Int.MAX_VALUE)))
        invoke(store, instance, "release", emptyList()).expect("release")
        invoke(store, instance, "grow", listOf(I32(4))).expect("grow")

        assertIs<ChasmResult.Error<ChasmError.ExecutionError>>(exhausted)
        assertTrue(exhausted.error.error.contains(InvocationError.GuestHeapOutOfMemory.toString()))
    }

    @Test
    fun arraysBeyondTheLengthCapTrap() {

        val (store, instance) = instantiate()

        val huge = invoke(store, instance, "huge", emptyList())

        assertIs<ChasmResult.Error<ChasmError.ExecutionError>>(huge)
        assertTrue(huge.error.error.contains(InvocationError.GuestHeapOutOfMemory.toString()))
    }

    @Test
    fun allocatedBytesCountGuestAllocations() {

        val (store, instance) = instantiate()
        invoke(store, instance, "grow", listOf(I32(4))).expect("grow")

        val allocated = invoke(store, instance, "allocated", emptyList()).expect("allocated").single() as I64

        assertTrue(allocated.value >= 4L * MEBIBYTE, "allocated ${allocated.value}")
    }

    private fun instantiate(): Pair<Store, Instance> {

        val store = store()
        val module = runBlocking { module(resourceBytes("modules/allocation_limits.wasm"), validate = true) }.expect("module")
        val allocated = function(store, FunctionTypeBuilder().apply { results { i64() } }.build(), HostFunction { _, results -> results.writeI64(0, withGc { allocatedBytes }) })
        val instance = runBlocking { instance(store, module, listOf(Import("host", "allocated", allocated))) }.expect("instance")

        return store to instance
    }

    private companion object {
        const val MEBIBYTE = 1L shl 20
    }
}
