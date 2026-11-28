package io.github.charlietap.chasm.storage.layout

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.compiled.format.COMPILED_RUNTIME_ABI
import io.github.charlietap.chasm.compiler.backend.abi.binaryName
import io.github.charlietap.chasm.compiler.backend.analysis.gc.Lane
import io.github.charlietap.chasm.compiler.backend.analysis.gc.LaneKind
import io.github.charlietap.chasm.compiler.backend.analysis.gc.STORAGE_FAMILY
import io.github.charlietap.chasm.compiler.backend.analysis.gc.StoragePlans
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.ext.structType
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class StorageLayoutsTest {

    private val fixtureLayouts = storageLayouts(types("storage_layouts"))

    @Test
    fun `a layout names the class and slots the planner chose`() {

        val types = types("storage_plans")
        val plansLayouts = storageLayouts(types)
        val plans = StoragePlans(types, STORAGE_FAMILY)
        val structTypes = types.indices.filter { index -> types[index].structType() != null && index != PLANS_VECTORS_TYPE }

        structTypes.forEach { index ->
            val layout = assertNotNull(plansLayouts.struct(index), "type $index")
            val storage = plans.struct(index)
            val plannedSlots = storage.fields.map { field -> field.lanes.map(::plannedSlot) }

            assertEquals(binaryName(storage.className), layout.storageClass, "type $index")
            assertEquals(plannedSlots, layout.fields.map { field -> slots(field.slots) }, "type $index")
        }
    }

    @Test
    fun `a 64 bit field reports its low and high slot`() {

        val layout = assertNotNull(fixtureLayouts.struct(WIDE_TYPE))

        assertEquals("$STORAGE_PACKAGE.Struct_I3_R0__I_II", layout.storageClass)
        assertEquals(NumberSlot(InlineSlot("i0"), null), layout.fields[0].slots)
        assertEquals(WideNumberSlots(InlineSlot("i1"), InlineSlot("i2")), layout.fields[1].slots)
    }

    @Test
    fun `a 64 bit field that straddles the last inline slot reports an inline low half and a spilled high half`() {

        val layout = assertNotNull(fixtureLayouts.struct(STRADDLE_TYPE))

        assertEquals(WideNumberSlots(InlineSlot("i22"), SpilledSlot("spilledInts", 0)), layout.fields[22].slots)
    }

    @Test
    fun `a packed field reports its packing`() {

        val layout = assertNotNull(fixtureLayouts.struct(PACKED_TYPE))

        assertEquals(NumberSlot(InlineSlot("i0"), PackedType.I8), layout.fields[0].slots)
        assertEquals(NumberSlot(InlineSlot("i1"), PackedType.I16), layout.fields[1].slots)
        assertEquals(NumberSlot(InlineSlot("i2"), null), layout.fields[2].slots)
    }

    @Test
    fun `a spilled field reports its array and index`() {

        val ints = assertNotNull(fixtureLayouts.struct(SPILL_INTS_TYPE))
        val references = assertNotNull(fixtureLayouts.struct(SPILL_REFERENCES_TYPE))

        assertEquals("$STORAGE_PACKAGE.Struct_I23_R18__I_II_II_IRx18_Spill", ints.storageClass)
        assertEquals(NumberSlot(InlineSlot("i22"), null), ints.fields[22].slots)
        assertEquals(NumberSlot(SpilledSlot("spilledInts", 0), null), ints.fields[23].slots)
        assertEquals(ReferenceSlot(SpilledSlot("spilledReferences", 0), null), references.fields[23].slots)
    }

    @Test
    fun `a field of a struct type carries that type's check`() {

        val slots = assertIs<ReferenceSlot>(assertNotNull(fixtureLayouts.struct(HOLDER_TYPE)).fields[0].slots)

        assertEquals(assertNotNull(fixtureLayouts.struct(LEAF_TYPE)).check, slots.check)
    }

    @Test
    fun `a field of an array type carries that type's check`() {

        val slots = assertIs<ReferenceSlot>(assertNotNull(fixtureLayouts.struct(HOLDER_TYPE)).fields[1].slots)

        assertEquals(assertNotNull(fixtureLayouts.array(BYTES_TYPE)).check, slots.check)
    }

    @Test
    fun `a field of an abstract reference type carries no check`() {

        val slots = assertIs<ReferenceSlot>(assertNotNull(fixtureLayouts.struct(HOLDER_TYPE)).fields[2].slots)

        assertNull(slots.check)
    }

    @Test
    fun `a type check counts the supertypes and states finality`() {

        val base = assertNotNull(fixtureLayouts.struct(BASE_TYPE)).check
        val middle = assertNotNull(fixtureLayouts.struct(MIDDLE_TYPE)).check
        val tip = assertNotNull(fixtureLayouts.struct(TIP_TYPE)).check

        assertEquals(listOf(0, 1, 2), listOf(base.supertypes, middle.supertypes, tip.supertypes))
        assertEquals(listOf(false, false, true), listOf(base.final, middle.final, tip.final))
    }

    @Test
    fun `a field that refers into its own recursion group carries the referenced type's check`() {

        val slots = assertIs<ReferenceSlot>(assertNotNull(fixtureLayouts.struct(MIDDLE_TYPE)).fields[2].slots)

        assertEquals(assertNotNull(fixtureLayouts.struct(TIP_TYPE)).check, slots.check)
    }

    @Test
    fun `an array layout names its array class`() {

        val expected = mapOf(
            BYTES_TYPE to "I8Array",
            CHARS_TYPE to "I16Array",
            INTS_TYPE to "I32Array",
            LONGS_TYPE to "I64Array",
            FLOATS_TYPE to "F32Array",
            DOUBLES_TYPE to "F64Array",
            REFS_TYPE to "RefArray",
        )

        expected.forEach { (index, name) ->
            assertEquals("$STORAGE_PACKAGE.$name", assertNotNull(fixtureLayouts.array(index)).storageClass, "type $index")
        }
    }

    @Test
    fun `layouts from bytes equal layouts from the decoded types`() {

        val fromBytes = assertIs<ChasmResult.Success<StorageLayouts>>(storageLayouts(resource("storage_layouts"))).result

        types("storage_layouts").indices.forEach { index ->
            assertEquals(fixtureLayouts.struct(index), fromBytes.struct(index), "type $index")
            assertEquals(fixtureLayouts.array(index), fromBytes.array(index), "type $index")
        }
    }

    @Test
    fun `an invalid module has no layouts`() {

        val result = storageLayouts(resource("invalid_result"))

        assertIs<ModuleError.Invalid>(assertIs<ChasmResult.Error<ModuleError>>(result).error)
    }

    @Test
    fun `bytes that are not a module have no layouts`() {

        val result = storageLayouts(byteArrayOf(0x00, 0x61, 0x73))

        assertIs<ModuleError.Malformed>(assertIs<ChasmResult.Error<ModuleError>>(result).error)
    }

    @Test
    fun `a type that is not a struct has no struct layout`() {
        assertNull(fixtureLayouts.struct(BYTES_TYPE))
        assertNull(fixtureLayouts.struct(FUNCTION_TYPE))
        assertNull(fixtureLayouts.array(LEAF_TYPE))
        assertNull(fixtureLayouts.struct(MISSING_TYPE))
    }

    @Test
    fun `a struct with a v128 field has no layout`() {
        assertNull(fixtureLayouts.struct(VECTORS_TYPE))
    }

    @Test
    fun `layouts report the runtime ABI`() {
        assertEquals(COMPILED_RUNTIME_ABI, fixtureLayouts.runtimeAbi)
    }

    private fun plannedSlot(lane: Lane): Slot {
        return when {
            !lane.spilled -> InlineSlot(lane.fieldName())
            lane.kind == LaneKind.INT -> SpilledSlot("spilledInts", lane.index)
            else -> SpilledSlot("spilledReferences", lane.index)
        }
    }

    private fun slots(fieldSlots: FieldSlots): List<Slot> {
        return when (fieldSlots) {
            is NumberSlot -> listOf(fieldSlots.slot)
            is WideNumberSlots -> listOf(fieldSlots.low, fieldSlots.high)
            is ReferenceSlot -> listOf(fieldSlots.slot)
        }
    }

    private fun types(name: String): List<DefinedType> {

        val config = ModuleConfig()
        val module = WasmModuleDecoder(config, resource(name)).getOrThrow { error -> IllegalStateException(error.toString()) }

        return WasmModuleValidator(config, module).getOrThrow { error -> IllegalStateException(error.toString()) }.definedTypes
    }

    private fun resource(name: String): ByteArray {
        return checkNotNull(javaClass.classLoader.getResourceAsStream("fixtures/$name.wasm")) { "missing fixture $name" }
            .use { stream -> stream.readBytes() }
    }

    private companion object {
        const val STORAGE_PACKAGE = "io.github.charlietap.chasm.runtime.compiled.gc.storage"
        const val WIDE_TYPE = 0
        const val PACKED_TYPE = 1
        const val SPILL_INTS_TYPE = 2
        const val LEAF_TYPE = 3
        const val BYTES_TYPE = 4
        const val HOLDER_TYPE = 5
        const val VECTORS_TYPE = 6
        const val FUNCTION_TYPE = 7
        const val CHARS_TYPE = 8
        const val INTS_TYPE = 9
        const val LONGS_TYPE = 10
        const val FLOATS_TYPE = 11
        const val DOUBLES_TYPE = 12
        const val REFS_TYPE = 13
        const val SPILL_REFERENCES_TYPE = 14
        const val STRADDLE_TYPE = 15
        const val BASE_TYPE = 16
        const val MIDDLE_TYPE = 17
        const val TIP_TYPE = 18
        const val MISSING_TYPE = 99
        const val PLANS_VECTORS_TYPE = 23
    }
}
