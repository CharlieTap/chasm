@file:OptIn(UnsafeHostApi::class)

package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.compiled.code.CompiledFixture
import io.github.charlietap.chasm.compiled.code.fixture
import io.github.charlietap.chasm.embedding.compiled.unsafeAllocateArray
import io.github.charlietap.chasm.embedding.reference.arrayType
import io.github.charlietap.chasm.embedding.reference.copyArray
import io.github.charlietap.chasm.embedding.reference.readArrayReference
import io.github.charlietap.chasm.embedding.reference.writeArrayReference
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.reference.StructReference
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertSame

class HostArraysTest {

    private val fixture = CompiledFixture(fixture("host_arrays"))

    @Test
    fun `reference array copies follow the element types' heap subtyping`() {

        val subtype = reference("point3")
        val points = array("make_points")
        val subtypes = array("make_point3s")
        val anys = array("make_anys")

        writeArrayReference(subtypes, 0, subtype)
        copyArray(subtypes, 0, points, 0, 1)
        copyArray(points, 0, anys, 0, 1)

        assertSame(subtype, readArrayReference(points, 0))
        assertSame(subtype, readArrayReference(anys, 0))
        assertFailsWith<IllegalArgumentException> { copyArray(points, 0, subtypes, 1, 1) }
        assertFailsWith<IllegalArgumentException> { copyArray(anys, 0, points, 1, 1) }
    }

    @Test
    fun `reference array copies check each element against the destination type`() {

        val point = reference("point")
        val points = array("make_points")
        val strict = array("make_strict")

        writeArrayReference(points, 1, point)
        copyArray(points, 1, strict, 0, 1)
        copyArray(strict, 0, points, 0, 2)

        assertSame(point, readArrayReference(strict, 0))
        assertSame(point, readArrayReference(points, 0))
        assertFailsWith<IllegalArgumentException> { copyArray(array("make_points"), 0, strict, 0, 1) }
    }

    @Test
    fun `only nullable anyref arrays adopt host reference storage`() {

        val point = reference("point")
        val storage = arrayOf<Any?>(point, null)

        val adopted = unsafeAllocateArray(fixture.store, arrayType(array("make_anys")), storage)

        assertEquals(listOf(I32(2)), fixture.call("any_length", ReferenceValue.Array(adopted)))
        assertSame(point, readArrayReference(adopted, 0))
        assertFailsWith<IllegalArgumentException> {
            unsafeAllocateArray(fixture.store, arrayType(array("make_points")), arrayOfNulls<Any>(1))
        }
        assertFailsWith<IllegalArgumentException> {
            unsafeAllocateArray(fixture.store, arrayType(array("make_strict_anys")), arrayOf<Any?>(point))
        }
    }

    private fun reference(name: String): StructReference {
        return assertIs<ReferenceValue.Struct>(fixture.call(name, I32(1)).single()).reference
    }

    private fun array(name: String): ArrayReference {
        return assertIs<ReferenceValue.Array>(fixture.call(name, I32(2)).single()).reference
    }
}
