@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.coroutines

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.internal.MIXED_STORE_ERROR
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.reference.ReferenceStorage
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class InstanceTest {

    @Test
    fun `instantiates a module`() {
        runTest {

            val module = module(TYPE_ONLY_MODULE).expect("decode")

            assertIs<ChasmResult.Success<Instance>>(instance(store(), module, emptyList()))
        }
    }

    @Test
    fun `a missing import is unlinkable`() {
        runTest {

            val module = module(UNLINKABLE_MODULE).expect("decode")

            assertIs<ExecutionError.Unlinkable>(errorOf(instance(store(), module, emptyList())))
        }
    }

    @Test
    fun `a memory64 module is unsupported`() {
        runTest {

            val module = module(MEMORY64_MODULE).expect("decode")

            assertIs<ExecutionError.Unsupported>(errorOf(instance(store(), module, emptyList())))
        }
    }

    @Test
    fun `a start function that traps is trapped`() {
        runTest {

            val module = module(START_TRAPS_MODULE).expect("decode")

            val trapped = assertIs<ExecutionError.Trapped>(errorOf(instance(store(), module, emptyList())))
            assertEquals(TrapReason.UNREACHABLE, trapped.trap.reason)
        }
    }

    @Test
    fun `a start function that throws returns the exception`() {
        runTest {

            val module = module(START_THROWS_MODULE).expect("decode")

            val thrown = assertIs<ExecutionError.Thrown<*>>(errorOf(instance(store(), module, emptyList())))
            assertIs<ReferenceValue.Exception>(thrown.exception)
        }
    }

    @Test
    fun `a store holding compiled references is refused`() {
        runTest {

            val store = store()
            store.store.referenceStorage = FakeReferenceStorage()
            val module = module(TYPE_ONLY_MODULE).expect("decode")

            val failure = assertFailsWith<IllegalArgumentException> {
                instance(store, module, emptyList())
            }
            assertEquals(MIXED_STORE_ERROR, failure.message)
        }
    }

    private fun errorOf(result: ChasmResult<*, *>): Any? {
        return assertIs<ChasmResult.Error<*>>(result).error
    }

    private class FakeReferenceStorage : ReferenceStorage {

        override fun initializeTable(
            table: TableInstance,
            element: ElementInstance,
            offset: Int,
            count: Int,
        ) {
        }

        override fun dropElement(element: ElementInstance) {
        }

        override fun release(global: GlobalInstance) {
        }

        override fun release(table: TableInstance) {
        }

        override fun release(element: ElementInstance) {
        }
    }

    private companion object {
        val HEADER = byteArrayOf(0x00, 0x61, 0x73, 0x6D, 0x01, 0x00, 0x00, 0x00)
        val EMPTY_FUNCTION_TYPE = byteArrayOf(0x01, 0x04, 0x01, 0x60, 0x00, 0x00)
        val TYPE_ONLY_MODULE = HEADER + EMPTY_FUNCTION_TYPE

        val UNLINKABLE_MODULE = HEADER + EMPTY_FUNCTION_TYPE +
            byteArrayOf(0x02, 0x07, 0x01, 0x01, 0x6D, 0x01, 0x66, 0x00, 0x00)

        val MEMORY64_MODULE = HEADER + byteArrayOf(0x05, 0x03, 0x01, 0x04, 0x01)

        val START_TRAPS_MODULE = HEADER + EMPTY_FUNCTION_TYPE +
            byteArrayOf(0x03, 0x02, 0x01, 0x00) +
            byteArrayOf(0x08, 0x01, 0x00) +
            byteArrayOf(0x0A, 0x05, 0x01, 0x03, 0x00, 0x00, 0x0B)

        val START_THROWS_MODULE = HEADER + EMPTY_FUNCTION_TYPE +
            byteArrayOf(0x03, 0x02, 0x01, 0x00) +
            byteArrayOf(0x0D, 0x03, 0x01, 0x00, 0x00) +
            byteArrayOf(0x08, 0x01, 0x00) +
            byteArrayOf(0x0A, 0x06, 0x01, 0x04, 0x00, 0x08, 0x00, 0x0B)
    }
}
