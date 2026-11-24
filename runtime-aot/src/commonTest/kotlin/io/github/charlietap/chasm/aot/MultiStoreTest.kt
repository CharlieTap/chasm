package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.aot.compiled.fixture
import io.github.charlietap.chasm.aot.compiled.generate
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.reference.runtimeType
import io.github.charlietap.chasm.embedding.reference.structType
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotSame

class MultiStoreTest {

    private val module = generate(fixture("managed_objects")).compiledModule()
    private val first = instance()
    private val second = instance()

    @Test
    fun `one compiled module runs in two live stores`() {

        val firstPoint = point(first, 1, 2)
        val secondPoint = point(second, 3, 4)

        assertNotSame(firstPoint.reference, secondPoint.reference)
        assertEquals(ChasmResult.Success(listOf(I32(1))), invoke(first, "x", listOf(firstPoint)))
        assertEquals(ChasmResult.Success(listOf(I32(3))), invoke(second, "x", listOf(secondPoint)))
    }

    @Test
    fun `a struct of one store is rejected by another store's export`() {

        val point = point(first, 1, 2)

        assertIs<ChasmResult.Error<*>>(invoke(second, "x", listOf(point)))
    }

    @Test
    fun `structType reports each store's own type`() {

        val firstPoint = point(first, 1, 2)
        val secondPoint = point(second, 3, 4)

        assertEquals(runtimeType(first, POINT_TYPE), structType(firstPoint.reference))
        assertEquals(runtimeType(second, POINT_TYPE), structType(secondPoint.reference))
    }

    private fun instance(): Instance {
        return runBlocking { instance(store(module), module) }.expect("instantiates")
    }

    private fun point(
        instance: Instance,
        x: Int,
        y: Int,
    ): ReferenceValue.Struct {
        return assertIs<ReferenceValue.Struct>(invoke(instance, "make_point", listOf(I32(x), I32(y))).expect("make_point").single())
    }

    private companion object {
        const val POINT_TYPE = 0
    }
}
