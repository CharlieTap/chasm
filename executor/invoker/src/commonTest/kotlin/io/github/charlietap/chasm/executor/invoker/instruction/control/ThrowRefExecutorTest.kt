package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.fixture.ast.instruction.catchAllRefHandler
import io.github.charlietap.chasm.fixture.ast.instruction.catchRefHandler
import io.github.charlietap.chasm.fixture.ast.module.labelIndex
import io.github.charlietap.chasm.fixture.ast.module.tagIndex
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tagAddress
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.type.rtt
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.runtime.exception.InvocationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ThrowRefExecutorTest {

    @Test
    fun `writes matched catch payloads and returns the continuation address`() {
        val store = store()
        val exceptionTagAddress = store.heap.registerTag(
            rtt(),
            tagType(
                functionType = functionType(
                    params = resultType(listOf(i64ValueType(), i64ValueType())),
                ),
            ),
        )
        val exceptionRef = store.heap.allocateException(exceptionTagAddress, longArrayOf(11L, 22L))
        val module = moduleInstance(tagAddresses = mutableListOf(tagAddress(0)))
        val cstack = cstack(
            handlers = listOf(
                ExceptionHandler(
                    handlers = listOf(catchRefHandler(tagIndex(0u), labelIndex(0u))),
                    payloadDestinationSlots = listOf(intArrayOf(2, 3, 4)),
                    continuationIps = intArrayOf(42),
                    instance = module,
                    fp = 0,
                    sp = 5,
                ),
            ),
        )
        val vstack = vstack().apply { reserveDepth(5) }

        val continuationIp = ThrowRefValueExecutor(vstack, cstack, store, exceptionRef)

        assertEquals(42, continuationIp)
        assertEquals(11L, vstack.getFrameSlot(2))
        assertEquals(22L, vstack.getFrameSlot(3))
        assertEquals(exceptionRef, vstack.getFrameSlot(4))
        assertEquals(0, cstack.handlersDepth())
    }

    @Test
    fun `continues through non-matching handlers without scheduling instructions`() {
        val store = store()
        val exceptionTagAddress = store.heap.registerTag(rtt(), tagType())
        val exceptionRef = store.heap.allocateException(exceptionTagAddress, longArrayOf())
        val module = moduleInstance(tagAddresses = mutableListOf(tagAddress(1)))
        val cstack = cstack(
            handlers = listOf(
                ExceptionHandler(
                    handlers = listOf(catchAllRefHandler(labelIndex(0u))),
                    payloadDestinationSlots = listOf(intArrayOf(1)),
                    continuationIps = intArrayOf(73),
                    instance = module,
                    fp = 0,
                    sp = 2,
                ),
                ExceptionHandler(
                    handlers = listOf(catchRefHandler(tagIndex(0u), labelIndex(0u))),
                    payloadDestinationSlots = listOf(intArrayOf(1)),
                    continuationIps = intArrayOf(51),
                    instance = module,
                    fp = 0,
                    sp = 8,
                ),
            ),
        )
        val vstack = vstack().apply { reserveDepth(12) }

        val continuationIp = ThrowRefValueExecutor(vstack, cstack, store, exceptionRef)

        assertEquals(73, continuationIp)
        assertEquals(exceptionRef, vstack.getFrameSlot(1))
        assertEquals(2, vstack.sp)
        assertEquals(0, cstack.handlersDepth())
    }

    @Test
    fun `escaping throw preserves the exact exception for the host`() {
        val store = store()
        val tagAddress = store.heap.registerTag(rtt(), tagType())
        val exceptionRef = store.heap.allocateException(tagAddress, LongArray(0))
        val cstack = cstack()

        val failure = assertFailsWith<InvocationException> {
            ThrowRefValueExecutor(vstack(), cstack, store, exceptionRef)
        }

        assertEquals(InvocationError.ThrownException, failure.error)
        assertTrue(store.heap.hasPending)
        assertEquals(exceptionRef, store.heap.takePendingExceptionReference())
    }
}
