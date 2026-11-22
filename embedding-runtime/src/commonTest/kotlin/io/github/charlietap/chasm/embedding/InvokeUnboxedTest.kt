@file:OptIn(ExperimentalChasmApi::class)

package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.fixture.INSTANCE_MODULE
import io.github.charlietap.chasm.embedding.fixture.PAIRS_MODULE
import io.github.charlietap.chasm.embedding.fixture.instantiate
import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.PreparedFunction
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes._engine
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.type.referenceValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.writeReference
import io.github.charlietap.chasm.type.AbstractHeapType
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertSame

class InvokeUnboxedTest {

    @Test
    fun `a numeric function runs on the word lane without a reference lane`() = runTest {

        val identity = prepared(instantiate(interpreterStore(), INSTANCE_MODULE), "identity")
        val words = longArrayOf(117L)

        val result = identity.invokeUnboxed(words, arrayOfNulls(0))

        assertEquals(ChasmResult.Success(Unit), result)
        assertEquals(117L, words[0])
    }

    @Test
    fun `a reference function passes host objects in and out on the reference lane`() = runTest {

        val store = interpreterStore()
        val swap = prepared(pairsInstance(store), "swap")
        val first = Any()
        val second = Any()
        val references = arrayOf<Any?>(first, second)

        val result = swap.invokeUnboxed(LongArray(2), references)

        assertEquals(ChasmResult.Success(Unit), result)
        assertSame(second, references[0])
        assertSame(first, references[1])
    }

    @Test
    fun `a word lane shorter than the function's slots is rejected`() = runTest {

        val identity = prepared(instantiate(interpreterStore(), INSTANCE_MODULE), "identity")

        val result = identity.invokeUnboxed(LongArray(0), arrayOfNulls(0))

        assertIs<ExecutionError.Rejected>(assertIs<ChasmResult.Error<*>>(result).error)
    }

    @Test
    fun `a reference lane shorter than the function's slots is rejected`() = runTest {

        val swap = prepared(pairsInstance(interpreterStore()), "swap")

        val result = swap.invokeUnboxed(LongArray(2), arrayOfNulls(1))

        assertIs<ExecutionError.Rejected>(assertIs<ChasmResult.Error<*>>(result).error)
    }

    @Test
    fun `a function prepared on another store cannot be invoked through this one`() = runTest {

        val identity = prepared(instantiate(interpreterStore(), INSTANCE_MODULE), "identity")
        val foreign = prepared(instantiate(interpreterStore(), INSTANCE_MODULE), "identity")

        assertEquals(ChasmResult.Success(Unit), identity.invokeUnboxed(LongArray(1), arrayOfNulls(0)))
        assertFailsWith<IllegalArgumentException> {
            identity._engine().invokeUnboxed(foreign, LongArray(1), arrayOfNulls(0))
        }
    }

    private suspend fun pairsInstance(store: Store): Instance {

        val externref = referenceValueType(refNullReferenceType(AbstractHeapType.Extern))
        val type = functionType(results = resultType(listOf(externref, externref)))
        val pair = HostFunction { _, results ->
            results.writeReference(0, null)
            results.writeReference(1, null)
        }

        return instantiate(store, PAIRS_MODULE, listOf(Import("host", "pair", function(store, type, pair))))
    }

    private fun prepared(
        instance: Instance,
        name: String,
    ): PreparedFunction {
        return prepareFunction(instance, name).expect("$name prepares")
    }
}
