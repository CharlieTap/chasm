package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.embedding.fixture.PAIRS_MODULE
import io.github.charlietap.chasm.embedding.fixture.instantiate
import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.table
import io.github.charlietap.chasm.embedding.table.readTable
import io.github.charlietap.chasm.embedding.table.writeTableElements
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.type.referenceValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.writeReference
import io.github.charlietap.chasm.type.AbstractHeapType
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertSame

class ScopedEncodeTest {

    @Test
    fun `two externref arguments keep their distinct objects across slot collections`() = runTest {

        val store = interpreterStore()
        val instance = instantiate(store, PAIRS_MODULE, listOf(pairImport(store) { Pair(Any(), Any()) }))

        repeat(ROUNDS) {

            val first = Any()
            val second = Any()

            val results = invoke(instance, "swap", listOf(ReferenceValue.Extern(first), ReferenceValue.Extern(second)))
                .expect("swap runs")

            assertSame(second, assertIs<ReferenceValue.Extern>(results[0]).value)
            assertSame(first, assertIs<ReferenceValue.Extern>(results[1]).value)
        }
    }

    @Test
    fun `two externrefs returned by a host function keep their distinct objects across slot collections`() = runTest {

        val store = interpreterStore()
        var produced = Pair(Any(), Any())
        val instance = instantiate(store, PAIRS_MODULE, listOf(pairImport(store) { produced }))

        repeat(ROUNDS) {

            produced = Pair(Any(), Any())

            val results = invoke(instance, "host_pair").expect("host_pair runs")

            assertSame(produced.first, assertIs<ReferenceValue.Extern>(results[0]).value)
            assertSame(produced.second, assertIs<ReferenceValue.Extern>(results[1]).value)
        }
    }

    @Test
    fun `externs written as a range keep their distinct objects across slot collections`() {

        val store = interpreterStore()
        val externref = refNullReferenceType(AbstractHeapType.Extern)
        val table = table(store, tableType(referenceType = externref, limits = limits(min = 2u)), null)

        repeat(ROUNDS) {

            val first = Any()
            val second = Any()

            writeTableElements(table, 0, arrayOf(first, second))

            assertSame(first, readTable(table, 0))
            assertSame(second, readTable(table, 1))
        }
    }

    private fun pairImport(
        store: Store,
        pair: () -> Pair<Any, Any>,
    ): Import {

        val externref = referenceValueType(refNullReferenceType(AbstractHeapType.Extern))
        val type = functionType(results = resultType(listOf(externref, externref)))
        val hostFunction = HostFunction { _, results ->
            val (first, second) = pair()
            results.writeReference(0, first)
            results.writeReference(1, second)
        }

        return Import("host", "pair", function(store, type, hostFunction))
    }

    private companion object {
        const val ROUNDS = 200
    }
}
