package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.compiler.backend.abi.binaryName
import io.github.charlietap.chasm.compiler.backend.analysis.gc.Lane
import io.github.charlietap.chasm.compiler.backend.analysis.gc.LaneKind
import io.github.charlietap.chasm.compiler.backend.analysis.gc.STORAGE_FAMILY
import io.github.charlietap.chasm.compiler.backend.analysis.gc.StorageClass
import io.github.charlietap.chasm.runtime.compiled.gc.CompiledStruct
import io.github.charlietap.chasm.runtime.compiled.gc.Rtt
import java.lang.reflect.Modifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class StorageTailTest {

    private val loader = CompiledStruct::class.java.classLoader
    private val tails = STORAGE_FAMILY.classes.filter { storageClass -> "x" in storageClass.name.substringAfterLast('/') && !storageClass.final }
    private val terminals = STORAGE_FAMILY.classes.filter { storageClass -> storageClass.final }
    private val rtt = Rtt(0, "tail", fakeEngine(), null, null)

    @Test
    fun `every tail class exists with its superclass and lanes`() {

        assertEquals(TAIL_CLASSES, tails.size)

        tails.forEach { expected ->

            val storageClass = Class.forName(binaryName(expected.name), false, loader)
            val fields = storageClass.declaredFields.filterNot { field -> field.isSynthetic }

            assertEquals(superclassName(expected), storageClass.superclass.name, expected.name)
            assertTrue(Modifier.isPublic(storageClass.modifiers), expected.name)
            assertFalse(Modifier.isFinal(storageClass.modifiers), expected.name)
            assertEquals(expected.lanes.map { lane -> lane.fieldName() }.toSet(), fields.map { field -> field.name }.toSet(), expected.name)
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

            val storageClass = Class.forName(binaryName(expected.name), false, loader)
            val fields = storageClass.declaredFields.filterNot { field -> field.isSynthetic }

            assertEquals(superclassName(expected), storageClass.superclass.name, expected.name)
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

            val storageClass = Class.forName(binaryName(expected.name), false, loader)
            val struct = storageClass.getConstructor(Rtt::class.java).newInstance(rtt) as CompiledStruct
            val lanes = lanes(expected)
            val ints = lanes.filter { lane -> lane.kind == LaneKind.INT }.map { lane -> lane.fieldName() }
            val references = lanes.filter { lane -> lane.kind == LaneKind.REFERENCE }.map { lane -> lane.fieldName() }

            ints.forEach { name ->
                val lane = name.drop(1).toInt()
                struct.writeInt(lane, lane + VALUE_OFFSET)
                assertEquals(lane + VALUE_OFFSET, struct.readInt(lane), "${expected.name}.$name")
            }
            assertNumberLaneReads(struct, ints.size, VALUE_OFFSET, expected.name)
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

            val storageClass = Class.forName(binaryName(expected.name), false, loader)
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

    private fun superclassName(storageClass: StorageClass): String {
        return storageClass.superclass?.let { parent -> binaryName(parent.name) } ?: CompiledStruct::class.java.name
    }

    private fun lanes(storageClass: StorageClass): List<Lane> {
        return generateSequence(storageClass) { current -> current.superclass }.flatMap { current -> current.lanes }.toList()
    }

    private fun laneType(name: String): Class<*> {
        return if (name.startsWith(INT_LANE)) Int::class.java else Any::class.java
    }

    private companion object {
        const val INT_LANE = "i"
        const val SPILLED_INTS = "spilledInts"
        const val SPILLED_REFERENCES = "spilledReferences"
        const val TAIL_CLASSES = 564
        const val TERMINAL_CLASSES = 33
        const val SPILL_SLOTS = 2
        const val VALUE_OFFSET = 100
    }
}
