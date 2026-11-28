package io.github.charlietap.chasm.bindgen

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.storage.layout.ReferenceSlot
import io.github.charlietap.chasm.storage.layout.TypeCheck
import io.github.charlietap.chasm.storage.layout.WideNumberSlots
import io.github.charlietap.chasm.storage.layout.storageLayouts
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.ext.structType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class StorageLayoutsStructViewLayoutsTest {

    private val types = definedTypes("storage_planner")
    private val moduleLayouts = storageLayouts(types)
    private val viewLayouts = StorageLayoutsStructViewLayouts()

    @Test
    fun `a type laid out alone gets the class, slots and check it gets in its module`() {

        val structTypes = types.indices.filter { index -> types[index].structType() != null }

        structTypes.forEach { index ->

            val wholeModule = assertNotNull(moduleLayouts.struct(index), "type $index")
            val alone = viewLayouts.layout(types[index])

            assertEquals(wholeModule.storageClass, alone.storageClass, "type $index")
            assertEquals(wholeModule.check, alone.check, "type $index")
            assertEquals(wholeModule.fields, alone.fields, "type $index")
        }
    }

    @Test
    fun `a view checks its type's depth and finality`() {

        val checks = listOf(BASE_TYPE, MIDDLE_TYPE, LEAF_TYPE, HOLDER_TYPE).map { index -> viewLayouts.layout(types[index]).check }

        assertEquals(
            listOf(
                TypeCheck(rttKey = structKey(BASE_TYPE), supertypes = 0, final = false),
                TypeCheck(rttKey = structKey(MIDDLE_TYPE), supertypes = 1, final = false),
                TypeCheck(rttKey = structKey(LEAF_TYPE), supertypes = 2, final = true),
                TypeCheck(rttKey = structKey(HOLDER_TYPE), supertypes = 0, final = true),
            ),
            checks,
        )
    }

    @Test
    fun `reference fields check the RTT of their declared type`() {

        val middle = viewLayouts.layout(types[MIDDLE_TYPE]).fields
        val holder = viewLayouts.layout(types[HOLDER_TYPE]).fields

        assertEquals(TypeCheck(rttKey = structKey(LEAF_TYPE), supertypes = 2, final = true), assertIs<ReferenceSlot>(middle[1].slots).check)
        assertEquals(TypeCheck(rttKey = structKey(BASE_TYPE), supertypes = 0, final = false), assertIs<ReferenceSlot>(holder[0].slots).check)
        assertEquals(TypeCheck(rttKey = arrayKey(BYTES_TYPE), supertypes = 0, final = true), assertIs<ReferenceSlot>(holder[1].slots).check)
        assertNull(assertIs<ReferenceSlot>(holder[2].slots).check)
        assertIs<WideNumberSlots>(holder[3].slots)
    }

    private fun structKey(index: Int): String {
        return assertNotNull(moduleLayouts.struct(index)).check.rttKey
    }

    private fun arrayKey(index: Int): String {
        return assertNotNull(moduleLayouts.array(index)).check.rttKey
    }

    private fun definedTypes(module: String): List<DefinedType> {

        val decoded = WasmModuleDecoder(ModuleConfig(), moduleBytes(module))
            .getOrThrow { error -> IllegalStateException("$module should decode: $error") }

        return ModuleDeclarationsFactory(decoded).definedTypes
    }

    private companion object {
        const val BYTES_TYPE = 0
        const val BASE_TYPE = 1
        const val MIDDLE_TYPE = 2
        const val LEAF_TYPE = 3
        const val HOLDER_TYPE = 4
    }
}
