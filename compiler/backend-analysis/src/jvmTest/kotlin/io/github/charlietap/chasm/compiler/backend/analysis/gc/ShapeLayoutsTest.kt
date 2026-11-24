package io.github.charlietap.chasm.compiler.backend.analysis.gc

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.compiler.backend.abi.EQ_REFERENCE
import io.github.charlietap.chasm.compiler.backend.abi.I31_REFERENCE
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_ARRAY
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_FUNCTION
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_STRUCT
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ShapeLayoutsTest {

    private val layouts = ShapeLayouts(module("gc_types").definedTypes, StructuralShapeNames())

    @Test
    fun `canonically equal types share one class`() {
        assertSame(layouts.struct(SHAPE), layouts.struct(SAME_SHAPE))
    }

    @Test
    fun `class names do not depend on the decode that produced them`() {

        val again = ShapeLayouts(module("gc_types").definedTypes, StructuralShapeNames())

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
    fun `types that cannot be laid out fail only where they are used`() {

        val vectors = ShapeLayouts(module("gc_vector_types").definedTypes, StructuralShapeNames())

        assertFailsWith<UnsupportedShape> { vectors.struct(VECTOR) }
        assertFailsWith<UnsupportedShape> { vectors.typeDescriptor(VECTOR) }
        assertFailsWith<UnsupportedShape> { vectors.struct(VECTOR_CHILD) }
        assertNull(vectors.className(VECTOR))
        assertNotNull(vectors.className(PLAIN))
        assertTrue(vectors.structs.none { shape -> shape.className == vectors.className(VECTOR) })
    }

    @Test
    fun `references to types that cannot be laid out use the managed base classes`() {

        val vectors = ShapeLayouts(module("gc_vector_types").definedTypes, StructuralShapeNames())

        assertEquals(listOf(MANAGED_STRUCT), vectors.struct(VECTOR_HOLDER).fields.map { field -> field.descriptor })
        assertEquals("[$MANAGED_STRUCT", vectors.array(VECTOR_HOLDERS).data.descriptor)
    }

    @Test
    fun `a shape carries the lowest type index with its key`() {
        assertEquals(SHAPE, layouts.struct(SAME_SHAPE).index)
        assertEquals(CIRCLES, layouts.array(CIRCLES).index)
    }

    @Test
    fun `names are asked for with the lowest type index with each key`() {

        val indexed = ShapeLayouts(module("gc_types").definedTypes, IndexedShapeNames())

        assertEquals("Lshape/Struct$SHAPE;", indexed.struct(SAME_SHAPE).className)
        assertEquals("Lshape/Struct$LEAF;", indexed.struct(TREE).fields[1].descriptor)
        assertEquals("Lshape/Array$SHAPES;", indexed.array(SHAPES).className)
    }

    private fun module(name: String): Module {

        val bytes = checkNotNull(javaClass.classLoader.getResourceAsStream("fixtures/$name.wasm")) { "missing fixture $name" }
            .use { stream -> stream.readBytes() }
        val config = ModuleConfig()
        val module = WasmModuleDecoder(config, bytes).getOrThrow { error -> IllegalStateException(error.toString()) }

        return WasmModuleValidator(config, module).getOrThrow { error -> IllegalStateException(error.toString()) }
    }

    private class StructuralShapeNames : ShapeNames {

        override fun struct(
            key: String,
            index: Int,
        ): String {
            return "Lchasm/gc/S$key;"
        }

        override fun array(
            key: String,
            index: Int,
        ): String {
            return "Lchasm/gc/A$key;"
        }
    }

    private companion object {
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
