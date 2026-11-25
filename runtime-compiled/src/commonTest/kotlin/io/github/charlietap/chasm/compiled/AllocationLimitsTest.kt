package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.compiled.code.CompiledFixture
import io.github.charlietap.chasm.compiled.code.fixture
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOptions
import io.github.charlietap.chasm.config.KB
import io.github.charlietap.chasm.config.Limits
import io.github.charlietap.chasm.config.MB
import io.github.charlietap.chasm.config.MemoryLimits
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.collect
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AllocationLimitsTest {

    private val limited = CompiledFixture(fixture(ALLOCATION_LIMITS), JvmOptions(runtime = gcHeap(1)))

    @Test
    fun `an allocation above gcHeap is refused`() {

        val result = limited.invoke("bytes", I32(2.MB.value.toInt()))

        assertGuestHeapExhausted(result)
    }

    @Test
    fun `allocations below gcHeap are not limited by it`() {

        val length = HALF_A_MEGABYTE.KB.value.toInt()

        repeat(ALLOCATIONS) {
            assertEquals(listOf(I32(length)), limited.call("bytes", I32(length)))
        }
    }

    @Test
    fun `an array length above the cap traps`() {

        val fixture = CompiledFixture(fixture(ALLOCATION_LIMITS))

        assertGuestHeapExhausted(fixture.invoke("longs", I32(LONG_ARRAY_CAP + 1)))
        assertGuestHeapExhausted(fixture.invoke("bytes", I32(-1)))
    }

    @Test
    fun `collect runs without a Chasm heap`() {

        val fixture = CompiledFixture(fixture(ALLOCATION_LIMITS))
        fixture.call("keep", I32(17))

        collect(fixture.store)

        assertEquals(listOf(I32(17)), fixture.call("kept"))
    }

    private fun assertGuestHeapExhausted(result: ChasmResult<*, *>) {

        val error = assertIs<ChasmResult.Error<*>>(result).error
        val trapped = assertIs<ExecutionError.Trapped>(error)

        assertEquals(TrapReason.GUEST_HEAP_EXHAUSTED, trapped.trap.reason)
    }

    private fun gcHeap(megabytes: Int): RuntimeConfig {
        return RuntimeConfig(limits = Limits(memory = MemoryLimits(gcHeap = megabytes.MB)))
    }

    private companion object {
        const val ALLOCATION_LIMITS = "allocation_limits"
        const val HALF_A_MEGABYTE = 512
        const val ALLOCATIONS = 8
        const val LONG_ARRAY_CAP = (Int.MAX_VALUE - 32) ushr 3
    }
}
