package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.fixture.INSTANCE_MODULE
import io.github.charlietap.chasm.embedding.fixture.instantiate
import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

class ExportsTest {

    @Test
    fun `exports name each export with a handle to the resource it exports`() = runTest {

        val store = interpreterStore()
        val instance = instantiate(store, INSTANCE_MODULE)

        val exports = exports(instance)

        assertEquals(listOf("memory", "global", "table", "tag", "identity", "trap", "spin"), exports.all.map { export -> export.name })
        assertIs<Memory>(exports.all[0].value)
        assertIs<Global>(exports.all[1].value)
        assertIs<Table>(exports.all[2].value)
        assertIs<Tag>(exports.all[3].value)
        assertIs<Function>(exports.all[4].value)
        assertSame(exports.all[0].value, assertNotNull(exports.memory("memory")))
        assertSame(exports.all[1].value, assertNotNull(exports.global("global")))
        assertSame(exports.all[2].value, assertNotNull(exports.table("table")))
        assertSame(exports.all[3].value, assertNotNull(exports.tag("tag")))
        assertSame(exports.all[4].value, assertNotNull(exports.function("identity")))
    }

    @Test
    fun `export handles carry the type of the resource they export`() = runTest {

        val store = interpreterStore()
        val instance = instantiate(store, INSTANCE_MODULE)

        val exports = exports(instance)

        val identity = functionType(
            params = resultType(listOf(i32ValueType())),
            results = resultType(listOf(i32ValueType())),
        )
        assertEquals(identity, assertNotNull(exports.function("identity")).type)
        assertEquals(1uL, assertNotNull(exports.memory("memory")).type.limits.min)
        assertEquals(2uL, assertNotNull(exports.table("table")).type.limits.min)
        assertEquals(i32ValueType(), assertNotNull(exports.global("global")).type.valueType)
    }

    @Test
    fun `a typed lookup of another kind of export finds nothing`() = runTest {

        val store = interpreterStore()
        val instance = instantiate(store, INSTANCE_MODULE)

        val exports = exports(instance)

        assertNull(exports.function("memory"))
        assertNull(exports.memory("identity"))
        assertNull(exports.global("missing"))
    }

    @Test
    fun `exports are built once per instance`() = runTest {

        val store = interpreterStore()
        val instance = instantiate(store, INSTANCE_MODULE)

        assertSame(exports(instance), exports(instance))
    }

    @Test
    fun `the exports of a dropped instance cannot be read`() = runTest {

        val store = interpreterStore()
        val instance = instantiate(store, INSTANCE_MODULE)

        dropInstance(instance)

        assertFailsWith<IllegalStateException> {
            exports(instance)
        }
    }
}
