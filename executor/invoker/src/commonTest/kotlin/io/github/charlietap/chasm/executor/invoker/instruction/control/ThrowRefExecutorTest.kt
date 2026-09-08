package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.fixture.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.runtime.exception.compiledCatch
import io.github.charlietap.chasm.fixture.runtime.exception.exceptionRegion
import io.github.charlietap.chasm.fixture.runtime.exception.functionExceptionTable
import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.type.rtt
import io.github.charlietap.chasm.fixture.runtime.value.nullReferenceValue
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.CompiledCatch
import io.github.charlietap.chasm.runtime.exception.ExceptionRegion
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.program.Program
import io.github.charlietap.chasm.runtime.stack.activationHeader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ThrowRefExecutorTest {
    @Test
    fun `writes matched catch payloads and returns the continuation address`() {
        val store = store()
        val tag = store.heap.registerTag(
            rtt(),
            tagType(
                functionType = functionType(
                    params = resultType(listOf(i64ValueType(), i64ValueType())),
                ),
            ),
        )
        val ref = store.heap.allocateException(tag, longArrayOf(11L, 22L))
        store.program.install(
            arrayOf(
                exceptionRegion(
                    0,
                    10,
                    -1,
                    arrayOf(
                        compiledCatch(tag.address, 42, intArrayOf(2, 3, 4), true, 5),
                    ),
                ),
            ),
        )
        val stack = vstack().apply { reserveDepth(12) }
        assertEquals(42, ThrowRefValueExecutor(stack, executionContext(store = store, vstack = stack), ref, 0))
        assertEquals(11L, stack.getFrameSlot(2))
        assertEquals(22L, stack.getFrameSlot(3))
        assertEquals(ref, stack.getFrameSlot(4))
        assertEquals(5, stack.sp)
    }

    @Test
    fun `nonmatching inner region searches parent and ordered catches`() {
        val store = store()
        val tag = store.heap.registerTag(rtt(), tagType())
        val ref = store.heap.allocateException(tag, longArrayOf())
        store.program.install(
            arrayOf(
                exceptionRegion(
                    0,
                    20,
                    -1,
                    arrayOf(
                        compiledCatch(tag.address + 1, 51, intArrayOf(1), true, 8),
                        compiledCatch(CompiledCatch.CATCH_ALL_TAG, 73, intArrayOf(1), true, 2),
                        compiledCatch(tag.address, 60, intArrayOf(1), true, 3),
                    ),
                ),
                exceptionRegion(5, 10, 0, arrayOf(compiledCatch(tag.address + 1, 51, intArrayOf(1), true, 8))),
            ),
        )
        for (ip in listOf(0, 5, 9, 10, 19)) {
            val stack = vstack().apply { reserveDepth(12) }
            assertEquals(73, ThrowRefValueExecutor(stack, executionContext(store = store, vstack = stack), ref, ip))
            assertEquals(ref, stack.getFrameSlot(1))
            assertEquals(2, stack.sp)
        }
    }

    @Test
    fun `region end is exclusive and root exit is recognized before return flag decoding`() {
        val store = store()
        val tag = store.heap.registerTag(rtt(), tagType())
        val ref = store.heap.allocateException(tag, longArrayOf())
        store.program.install(
            arrayOf(
                exceptionRegion(
                    0,
                    10,
                    -1,
                    arrayOf(
                        compiledCatch(tag.address, 42, intArrayOf(), false, 2),
                    ),
                ),
            ),
        )
        val stack = vstack().apply {
            reserveDepth(3)
            writeRootActivationHeader(1)
        }
        val failure = assertFailsWith<InvocationException> {
            ThrowRefValueExecutor(stack, executionContext(store = store, vstack = stack), ref, 10)
        }
        assertEquals(InvocationError.ThrownException, failure.error)
        assertEquals(ref, store.heap.takePendingExceptionReference())
    }

    @Test
    fun `walks EH free callee frames with ordinary and destination encoded returns`() {
        for (results in listOf(0, 1, 2)) {
            val store = store()
            val tag = store.heap.registerTag(rtt(), tagType())
            val ref = store.heap.allocateException(tag, longArrayOf())
            store.program.install(
                arrayOf(
                    exceptionRegion(
                        10,
                        11,
                        -1,
                        arrayOf(
                            compiledCatch(tag.address, 20, intArrayOf(0), true, 2),
                        ),
                    ),
                ),
                count = 40,
            )
            store.program.append(Array(20) { dispatchableInstruction() })
            val callee = wasmFunctionInstance(
                functionType = functionType(
                    params = resultType(List(3) { i64ValueType() }),
                    results = resultType(List(results) { i64ValueType() }),
                ),
                function = runtimeFunction(body = runtimeExpression(40)),
            )
            // Imports may alias the same function; store order is not program order.
            store.functions.add(callee)
            store.functions.add(wasmFunctionInstance(function = runtimeFunction(body = runtimeExpression(0))))
            store.functions.add(callee)
            val stack = vstack().apply {
                reserveDepth(20)
                writeRootActivationHeader(1)
                writeActivationHeader(8, 3, activationHeader(if (results == 1) (1 shl 30) or 10 else 11, 8))
                activateFrameAtDepth(8, 20)
            }
            assertEquals(20, ThrowRefValueExecutor(stack, executionContext(store = store, vstack = stack), ref, 45))
            assertEquals(0, stack.fp)
            assertEquals(2, stack.sp)
            assertEquals(ref, stack.getFrameSlot(0))
        }
    }

    @Test
    fun `tail call site cannot select eliminated caller handler`() {
        val store = store()
        val tag = store.heap.registerTag(rtt(), tagType())
        val ref = store.heap.allocateException(tag, longArrayOf())
        store.program.install(
            arrayOf(
                exceptionRegion(
                    0,
                    10,
                    -1,
                    arrayOf(
                        compiledCatch(tag.address, 42, intArrayOf(), false, 2),
                    ),
                ),
            ),
            tails = intArrayOf(5),
        )
        val stack = vstack().apply {
            reserveDepth(3)
            writeRootActivationHeader(1)
        }
        val failure = assertFailsWith<InvocationException> {
            ThrowRefValueExecutor(stack, executionContext(store = store, vstack = stack), ref, 5)
        }
        assertEquals(InvocationError.ThrownException, failure.error)
        assertEquals(ref, store.heap.takePendingExceptionReference())
    }

    @Test
    fun `escaping throw preserves the exact exception for the host`() {
        val store = store()
        val tag = store.heap.registerTag(rtt(), tagType())
        val ref = store.heap.allocateException(tag, LongArray(0))
        val stack = vstack()
        val failure = assertFailsWith<InvocationException> {
            ThrowRefValueExecutor(stack, executionContext(store = store, vstack = stack), ref, 0)
        }
        assertEquals(InvocationError.ThrownException, failure.error)
        assertTrue(store.heap.hasPending)
        assertEquals(ref, store.heap.takePendingExceptionReference())
    }

    @Test
    fun `null throw ref traps without publishing a pending exception`() {
        val store = store()
        val stack = vstack()
        val failure = assertFailsWith<InvocationException> {
            ThrowRefValueExecutor(stack, executionContext(store = store, vstack = stack), nullReferenceValue().toLongFromBoxed(), 0)
        }
        assertEquals(InvocationError.UnexpectedReferenceValue, failure.error)
        assertFalse(store.heap.hasPending)
    }

    @Test
    fun `invalid activation links fail instead of looping or escaping through a fake root`() {
        for ((returnIp, delta) in listOf(11 to 0, 11 to 9, Int.MAX_VALUE to 0)) {
            val store = store()
            val tag = store.heap.registerTag(rtt(), tagType())
            val ref = store.heap.allocateException(tag, longArrayOf())
            store.program.install(emptyArray())
            val stack = vstack().apply {
                reserveDepth(20)
                writeActivationHeader(8, 1, activationHeader(returnIp, delta))
                activateFrameAtDepth(8, 20)
            }
            val failure = assertFailsWith<InvocationException> {
                ThrowRefValueExecutor(stack, executionContext(store = store, vstack = stack), ref, 15)
            }
            assertEquals(InvocationError.ProgramFinishedInconsistentState, failure.error)
            assertFalse(store.heap.hasPending)
        }
    }

    private fun Program.install(regions: Array<ExceptionRegion>, count: Int = 80, tails: IntArray = intArrayOf()) {
        append(Array(count) { dispatchableInstruction() })
        registerExceptionTable(functionExceptionTable(0, count, 1, 1, regions, tails))
    }
}
