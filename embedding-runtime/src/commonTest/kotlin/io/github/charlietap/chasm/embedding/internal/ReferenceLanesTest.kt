package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.embedding.fixture.REENTRY_MODULE
import io.github.charlietap.chasm.embedding.fixture.instantiate
import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.type.referenceValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.readReference
import io.github.charlietap.chasm.host.writeReference
import io.github.charlietap.chasm.type.AbstractHeapType
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class ReferenceLanesTest {

    @Test
    fun `each host call keeps its own reference lane across guest re-entry`() = runTest {

        val store = interpreterStore()
        val outerArgument = HostValue("outer")
        val innerArgument = HostValue("inner")
        val innerParameters = mutableListOf<Any?>()
        lateinit var instance: Instance

        val outer = HostFunction { parameters, results ->
            val before = parameters.readReference(0)
            val nested = invoke(instance, "reenter", listOf(ReferenceValue.Extern(innerArgument))).expect("reenter runs")
            val after = parameters.readReference(0)
            results.writeReference(0, Visit(before, after, assertIs<ReferenceValue.Extern>(nested.single()).value))
        }
        val inner = HostFunction { parameters, results ->
            val parameter = parameters.readReference(0)
            innerParameters += parameter
            results.writeReference(0, Visit(parameter, parameter, null))
        }
        val type = functionType(params = resultType(listOf(EXTERNREF)), results = resultType(listOf(EXTERNREF)))
        val imports = listOf(
            Import("host", "outer", function(store, type, outer)),
            Import("host", "inner", function(store, type, inner)),
        )
        instance = instantiate(store, REENTRY_MODULE, imports)

        val result = invoke(instance, "start", listOf(ReferenceValue.Extern(outerArgument))).expect("start runs")

        val visit = assertIs<Visit>(assertIs<ReferenceValue.Extern>(result.single()).value)
        assertSame(outerArgument, visit.before)
        assertSame(outerArgument, visit.after)
        assertEquals(Visit(innerArgument, innerArgument, null), visit.nested)
        assertEquals(listOf<Any?>(innerArgument), innerParameters)
    }

    @Test
    fun `a nested acquire gets a separate lane and a released depth reuses its lane`() {

        val lanes = ReferenceLanes()

        val outer = lanes.acquire(2)
        val nested = lanes.acquire(2)
        lanes.release()
        val reacquired = lanes.acquire(2)
        lanes.release()
        lanes.release()

        assertNotSame(outer, nested)
        assertSame(nested, reacquired)
        assertSame(outer, lanes.acquire(2))
    }

    @Test
    fun `a lane grows geometrically when a call needs more slots than it holds`() {

        val lanes = ReferenceLanes()

        val small = lanes.acquire(1)
        lanes.release()
        val doubled = lanes.acquire(small.size + 1)
        lanes.release()
        val large = lanes.acquire(doubled.size * 3)

        assertNotSame(small, doubled)
        assertEquals(small.size * 2, doubled.size)
        assertEquals(doubled.size * 3, large.size)
    }

    @Test
    fun `lanes keep separate depths beyond their initial capacity`() {

        val lanes = ReferenceLanes()

        val acquired = List(10) { lanes.acquire(1) }

        assertEquals(10, acquired.toSet().size)
    }

    private data class HostValue(val name: String)

    private data class Visit(
        val before: Any?,
        val after: Any?,
        val nested: Any?,
    )

    private companion object {
        val EXTERNREF = referenceValueType(refNullReferenceType(AbstractHeapType.Extern))
    }
}
