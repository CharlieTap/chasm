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

class StorageTailTest {

    private val family = storageFamily()
    private val tails = family.values.filter { storageClass -> TAIL in storageClass.name }
    private val terminals = family.values.filter { storageClass -> storageClass.name.endsWith(TERMINAL) }
    private val rtt = Rtt(0, "tail", fakeEngine(), null, null)

    @Test
    fun `every tail class exists with its superclass and lanes`() {

        assertEquals(TAIL_CLASSES, tails.size)

        tails.forEach { expected ->

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
    fun `every terminal class is final and holds the spill arrays`() {

        assertEquals(TERMINAL_CLASSES, terminals.size)

        terminals.forEach { expected ->

            val storageClass = Class.forName(PACKAGE + expected.name)
            val fields = storageClass.declaredFields.filterNot { field -> field.isSynthetic }

            assertEquals(expected.superclass, storageClass.superclass.simpleName, expected.name)
            assertTrue(Modifier.isPublic(storageClass.modifiers), expected.name)
            assertTrue(Modifier.isFinal(storageClass.modifiers), expected.name)
            assertEquals(setOf(SPILLED_INTS, SPILLED_REFERENCES), fields.map { field -> field.name }.toSet(), expected.name)
            assertEquals(IntArray::class.java, storageClass.getField(SPILLED_INTS).type, expected.name)
            assertEquals(Array<Any?>::class.java, storageClass.getField(SPILLED_REFERENCES).type, expected.name)
            storageClass.getConstructor(Rtt::class.java)
        }
    }

    @Test
    fun `every lane accessor of a tail class reaches its own field`() {
        (tails + terminals).forEach { expected ->

            val storageClass = Class.forName(PACKAGE + expected.name)
            val struct = storageClass.getConstructor(Rtt::class.java).newInstance(rtt) as CompiledStruct
            val lanes = lanes(expected.name)
            val ints = lanes.filter { lane -> lane.startsWith(INT_LANE) }
            val references = lanes.filter { lane -> lane.startsWith(REFERENCE_LANE) }

            ints.forEach { name ->
                val lane = name.drop(1).toInt()
                struct.writeInt(lane, lane + VALUE_OFFSET)
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
                assertSame(value, struct.readReference(lane), "${expected.name}.$name")
            }

            assertFailsWith<IndexOutOfBoundsException>(expected.name) { struct.readInt(ints.size) }
            assertFailsWith<IndexOutOfBoundsException>(expected.name) { struct.readReference(references.size) }
        }
    }

    @Test
    fun `spill accessors of a terminal class reach its spill arrays`() {
        terminals.forEach { expected ->

            val storageClass = Class.forName(PACKAGE + expected.name)
            val struct = storageClass.getConstructor(Rtt::class.java).newInstance(rtt) as CompiledStruct
            val value = Any()

            assertFailsWith<IndexOutOfBoundsException>(expected.name) { struct.readSpilledInt(0) }
            assertFailsWith<IndexOutOfBoundsException>(expected.name) { struct.readSpilledReference(0) }

            storageClass.getField(SPILLED_INTS).set(struct, IntArray(SPILL_SLOTS))
            storageClass.getField(SPILLED_REFERENCES).set(struct, arrayOfNulls<Any?>(SPILL_SLOTS))
            struct.writeSpilledInt(1, VALUE_OFFSET)
            struct.writeSpilledReference(1, value)

            assertEquals(VALUE_OFFSET, struct.readSpilledInt(1), expected.name)
            assertSame(value, struct.readSpilledReference(1), expected.name)
            assertFailsWith<IndexOutOfBoundsException>(expected.name) { struct.readSpilledInt(SPILL_SLOTS) }
            assertFailsWith<IndexOutOfBoundsException>(expected.name) { struct.readSpilledReference(SPILL_SLOTS) }
        }
    }

    private fun lanes(name: String): List<String> {
        return generateSequence(family[name]) { storageClass -> family[storageClass.superclass] }
            .flatMap { storageClass -> storageClass.lanes }
            .filter { lane -> lane.startsWith(INT_LANE) || lane.startsWith(REFERENCE_LANE) }
            .filterNot { lane -> lane == SPILLED_INTS || lane == SPILLED_REFERENCES }
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
        const val SPILLED_INTS = "spilledInts"
        const val SPILLED_REFERENCES = "spilledReferences"
        const val TAIL_CLASSES = 864
        const val TERMINAL_CLASSES = 54
        const val SPILL_SLOTS = 2
        const val VALUE_OFFSET = 100
    }
}
