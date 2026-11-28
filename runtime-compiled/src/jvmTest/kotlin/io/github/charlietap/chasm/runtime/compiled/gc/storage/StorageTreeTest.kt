package io.github.charlietap.chasm.runtime.compiled.gc.storage

import io.github.charlietap.chasm.runtime.compiled.gc.CompiledStruct
import io.github.charlietap.chasm.runtime.compiled.gc.MAX_INLINE_INT_LANES
import io.github.charlietap.chasm.runtime.compiled.gc.Rtt
import io.github.charlietap.chasm.runtime.compiled.gc.fakeEngine
import java.lang.reflect.Modifier
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class StorageTreeTest {

    private val family = storageFamily()
    private val tree = family.values.filterNot { storageClass -> TAIL in storageClass.name || storageClass.name.endsWith(TERMINAL) }
    private val rtt = Rtt(0, "tree", fakeEngine(), null, null)

    @Test
    fun `every tree class exists with its superclass and lanes`() {

        assertEquals(TREE_CLASSES, tree.size)

        tree.forEach { expected ->

            val storageClass = Class.forName(PACKAGE + expected.name)
            val fields = storageClass.declaredFields.filterNot { field -> field.isSynthetic }

            assertEquals(expected.superclass, storageClass.superclass.simpleName, expected.name)
            assertTrue(Modifier.isPublic(storageClass.modifiers), expected.name)
            assertFalse(Modifier.isFinal(storageClass.modifiers), expected.name)
            assertEquals(expected.lanes.toSet(), fields.map { field -> field.name }.toSet(), expected.name)
            fields.forEach { field ->
                assertTrue(Modifier.isPublic(field.modifiers), "${expected.name}.${field.name}")
                assertEquals(laneType(field.name), field.type, "${expected.name}.${field.name}")
            }
            storageClass.getConstructor(Rtt::class.java)
        }
    }

    @Test
    fun `every lane accessor reaches its own field`() {
        tree.forEach { expected ->

            val storageClass = Class.forName(PACKAGE + expected.name)
            val struct = storageClass.getConstructor(Rtt::class.java).newInstance(rtt) as CompiledStruct
            val lanes = lanes(expected.name)
            val ints = lanes.filter { lane -> lane.startsWith(INT_LANE) }
            val references = lanes.filter { lane -> lane.startsWith(REFERENCE_LANE) }

            ints.forEach { name ->
                val lane = name.drop(1).toInt()
                struct.writeInt(lane, lane + VALUE_OFFSET)
                assertEquals(lane + VALUE_OFFSET, storageClass.getField(name).getInt(struct), "${expected.name}.$name")
                assertEquals(lane + VALUE_OFFSET, struct.readInt(lane), "${expected.name}.$name")
            }
            assertTrue(ints.size <= MAX_INLINE_INT_LANES, expected.name)
            val laneValues = IntArray(ints.size)
            struct.readInts(laneValues)
            assertContentEquals(IntArray(ints.size) { lane -> lane + VALUE_OFFSET }, laneValues, expected.name)
            references.forEach { name ->
                val lane = name.drop(1).toInt()
                val value = Any()
                struct.writeReference(lane, value)
                assertSame(value, storageClass.getField(name).get(struct), "${expected.name}.$name")
                assertSame(value, struct.readReference(lane), "${expected.name}.$name")
            }

            assertFailsWith<IndexOutOfBoundsException>(expected.name) { struct.readInt(ints.size) }
            assertFailsWith<IndexOutOfBoundsException>(expected.name) { struct.writeInt(ints.size, 0) }
            assertFailsWith<IndexOutOfBoundsException>(expected.name) { struct.readReference(references.size) }
            assertFailsWith<IndexOutOfBoundsException>(expected.name) { struct.writeReference(references.size, null) }
            assertFailsWith<IndexOutOfBoundsException>(expected.name) { struct.readSpilledInt(0) }
            assertFailsWith<IndexOutOfBoundsException>(expected.name) { struct.readSpilledReference(0) }
        }
    }

    private fun lanes(name: String): List<String> {
        return generateSequence(family[name]) { storageClass -> family[storageClass.superclass] }
            .flatMap { storageClass -> storageClass.lanes }
            .toList()
    }

    private fun laneType(name: String): Class<*> {
        return if (name.startsWith(INT_LANE)) Int::class.java else Any::class.java
    }

    private fun storageFamily(): Map<String, FamilyClass> {

        val text = checkNotNull(javaClass.classLoader.getResourceAsStream(FAMILY)) { "missing $FAMILY" }
            .use { stream -> stream.readBytes().decodeToString() }

        return text.lines()
            .filter { line -> line.isNotBlank() && !line.startsWith("#") }
            .map { line -> line.split(" ") }
            .associate { columns ->
                columns[0] to FamilyClass(columns[0], columns[1], columns.drop(2).map { lane -> lane.substringBefore(":") })
            }
    }

    private class FamilyClass(
        val name: String,
        val superclass: String,
        val lanes: List<String>,
    )

    private companion object {
        const val FAMILY = "storage_family.txt"
        const val PACKAGE = "io.github.charlietap.chasm.runtime.compiled.gc.storage."
        const val TAIL = "_T"
        const val TERMINAL = "_X"
        const val INT_LANE = "i"
        const val REFERENCE_LANE = "r"
        const val TREE_CLASSES = 80
        const val VALUE_OFFSET = 100
    }
}
