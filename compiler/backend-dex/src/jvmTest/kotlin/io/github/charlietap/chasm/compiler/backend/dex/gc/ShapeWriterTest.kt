package io.github.charlietap.chasm.compiler.backend.dex.gc

import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.compiler.backend.abi.ENGINE_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_ARRAY
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_STRUCT
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_ENGINE
import io.github.charlietap.chasm.compiler.backend.abi.SHAPE_OWNER_CLASS
import io.github.charlietap.chasm.compiler.backend.abi.SHAPE_OWNER_ENGINE
import io.github.charlietap.chasm.compiler.backend.abi.WEAK_REFERENCE_TYPE
import io.github.charlietap.chasm.compiler.backend.analysis.gc.ShapeLayouts
import io.github.charlietap.chasm.compiler.backend.dex.decodeModule
import io.github.charlietap.chasm.compiler.backend.dex.fixture
import io.github.charlietap.chasm.embedding.engine.Engine
import io.github.charlietap.dexter.Dexter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ShapeWriterTest {

    private val layouts = ShapeLayouts(decodeModule(fixture("gc_types")).definedTypes, StructuralShapeNames())

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

    private companion object {
        const val MIN_API = 26
        const val SHAPE = 0
        const val CIRCLE = 1
        const val SHAPES = 5
        const val CIRCLES = 6
    }
}
