package io.github.charlietap.chasm.compiler.backend.dex.gc

import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.compiler.backend.abi.ENGINE_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.EQ_REFERENCE
import io.github.charlietap.chasm.compiler.backend.abi.I31_REFERENCE
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_ARRAY
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_FUNCTION
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_STRUCT
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_ENGINE
import io.github.charlietap.chasm.compiler.backend.abi.SHAPE_OWNER_CLASS
import io.github.charlietap.chasm.compiler.backend.abi.SHAPE_OWNER_ENGINE
import io.github.charlietap.chasm.compiler.backend.abi.WEAK_REFERENCE_TYPE
import io.github.charlietap.chasm.compiler.backend.dex.decodeModule
import io.github.charlietap.chasm.compiler.backend.dex.fixture
import io.github.charlietap.chasm.compiler.backend.dex.lower.Unsupported
import io.github.charlietap.chasm.embedding.engine.Engine
import io.github.charlietap.dexter.Dexter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class GcLayoutsTest {

    private val layouts = GcLayouts(decodeModule(fixture("gc_types")).definedTypes)

    @Test
    fun `canonically equal types share one class`() {
        assertSame(layouts.struct(SHAPE), layouts.struct(SAME_SHAPE))
    }

    @Test
    fun `class names do not depend on the decode that produced them`() {

        val again = GcLayouts(decodeModule(fixture("gc_types")).definedTypes)

        assertEquals(layouts.structs.map { shape -> shape.className }, again.structs.map { shape -> shape.className })
        assertEquals(layouts.arrays.map { shape -> shape.className }, again.arrays.map { shape -> shape.className })
        assertTrue(Regex("Lchasm/gc/S[0-9a-f]{32}_0;").matches(layouts.struct(SHAPE).className))
    }

    @Test
    fun `subtypes extend their supertype and reuse its fields`() {

        val shape = layouts.struct(SHAPE)
        val circle = layouts.struct(CIRCLE)

        assertSame(shape, circle.parent)
        assertSame(shape.fields[0], circle.fields[0])
        assertEquals(circle.className, circle.fields[1].owner)
        assertTrue(circle.final)
        assertFalse(shape.final)
    }

    @Test
    fun `fields carry the most precise descriptor`() {

        val tree = layouts.struct(TREE)
        val leaf = layouts.struct(LEAF)
        val holder = layouts.struct(HOLDER)

        assertEquals(listOf(tree.className, leaf.className), tree.fields.map { field -> field.descriptor })
        assertEquals(listOf("J", "B", "C"), leaf.fields.map { field -> field.descriptor })
        assertEquals(
            listOf(OBJECT_TYPE, EQ_REFERENCE, I31_REFERENCE, MANAGED_FUNCTION, layouts.struct(SHAPE).className),
            holder.fields.map { field -> field.descriptor },
        )
    }

    @Test
    fun `array holders inherit the data field of their root`() {

        val shapes = layouts.array(SHAPES)
        val circles = layouts.array(CIRCLES)

        assertSame(shapes, circles.parent)
        assertSame(shapes.data, circles.data)
        assertEquals("[" + layouts.struct(SHAPE).className, shapes.data.descriptor)
        assertEquals("[B", layouts.array(BYTES).data.descriptor)
    }

    @Test
    fun `written shapes extend the runtime base classes`() {

        val dexter = Dexter()
        val file = dexter.beginFile()
        ShapeWriter().write(file, layouts)
        val classes = DexBackedDexFile(Opcodes.forApi(MIN_API), file.finish()).classes.associateBy { classDef -> classDef.type }

        assertEquals(layouts.structs.size + layouts.arrays.size + 1, classes.size)
        assertEquals(listOf(SHAPE_OWNER_ENGINE), classes.getValue(SHAPE_OWNER_CLASS).staticFields.map { field -> field.name })
        assertEquals(listOf(WEAK_REFERENCE_TYPE), classes.getValue(SHAPE_OWNER_CLASS).staticFields.map { field -> field.type })
        assertEquals(
            listOf(ENGINE_TYPE),
            classes.getValue(layouts.struct(SHAPE).className).virtualMethods
                .filter { method -> method.name == REFERENCE_ENGINE }
                .map { method -> method.returnType },
        )
        assertTrue(classes.getValue(layouts.struct(CIRCLE).className).virtualMethods.none { method -> method.name == REFERENCE_ENGINE })
        assertEquals(MANAGED_STRUCT, classes.getValue(layouts.struct(SHAPE).className).superclass)
        assertEquals(layouts.struct(SHAPE).className, classes.getValue(layouts.struct(CIRCLE).className).superclass)
        assertEquals(listOf("f1"), classes.getValue(layouts.struct(CIRCLE).className).instanceFields.map { field -> field.name })
        assertEquals(MANAGED_ARRAY, classes.getValue(layouts.array(SHAPES).className).superclass)
        assertEquals(listOf("data"), classes.getValue(layouts.array(SHAPES).className).instanceFields.map { field -> field.name })
        assertTrue(classes.getValue(layouts.array(CIRCLES).className).instanceFields.none())
    }

    @Test
    @OptIn(InternalChasmApi::class)
    fun `the engine type names the shared engine interface`() {
        assertEquals("L" + Engine::class.java.name.replace('.', '/') + ";", ENGINE_TYPE)
    }

    @Test
    fun `types that cannot be laid out fail only where they are used`() {

        val vectors = GcLayouts(decodeModule(fixture("gc_vector_types")).definedTypes)

        assertFailsWith<Unsupported> { vectors.struct(VECTOR) }
        assertFailsWith<Unsupported> { vectors.typeDescriptor(VECTOR) }
        assertFailsWith<Unsupported> { vectors.struct(VECTOR_CHILD) }
        assertNull(vectors.className(VECTOR))
        assertNotNull(vectors.className(PLAIN))
        assertTrue(vectors.structs.none { shape -> shape.className == vectors.className(VECTOR) })
    }

    @Test
    fun `references to types that cannot be laid out use the managed base classes`() {

        val vectors = GcLayouts(decodeModule(fixture("gc_vector_types")).definedTypes)

        assertEquals(listOf(MANAGED_STRUCT), vectors.struct(VECTOR_HOLDER).fields.map { field -> field.descriptor })
        assertEquals("[$MANAGED_STRUCT", vectors.array(VECTOR_HOLDERS).data.descriptor)
    }

    private companion object {
        const val MIN_API = 26
        const val SHAPE = 0
        const val CIRCLE = 1
        const val TREE = 2
        const val LEAF = 3
        const val HOLDER = 4
        const val SHAPES = 5
        const val CIRCLES = 6
        const val BYTES = 7
        const val SAME_SHAPE = 8
        const val VECTOR = 0
        const val VECTOR_CHILD = 1
        const val VECTOR_HOLDER = 2
        const val VECTOR_HOLDERS = 3
        const val PLAIN = 4
    }
}
