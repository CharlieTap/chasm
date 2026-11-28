package io.github.charlietap.chasm.compiler.backend.analysis.gc

import io.github.charlietap.chasm.compiled.format.ArtifactTarget
import kotlin.test.Test
import kotlin.test.assertEquals

class StructSizesTest {

    private val art = StructSizes(ArtifactTarget.ART)
    private val hotspot = StructSizes(ArtifactTarget.HOTSPOT)

    @Test
    fun `an object with no spill is its base plus four bytes a slot rounded to eight`() {

        val expected = listOf(1 to (16 to 24), 3 to (24 to 32), 4 to (32 to 32), 5 to (32 to 40), 6 to (40 to 40), 41 to (176 to 184))

        expected.forEach { (inlineLanes, bytes) ->

            val (artBytes, hotspotBytes) = bytes
            val storage = StructStorage(UNUSED_CLASS, emptyList(), inlineLanes, final = false)

            assertEquals(artBytes, art.bytes(storage), "ART $inlineLanes")
            assertEquals(hotspotBytes, hotspot.bytes(storage), "HotSpot $inlineLanes")
        }
    }

    @Test
    fun `a spilling object adds two array fields and each spill array with its header`() {

        assertEquals(200, art.bytes(spilling(ints = 1, references = 0)))
        assertEquals(216, hotspot.bytes(spilling(ints = 1, references = 0)))
        assertEquals(216, art.bytes(spilling(ints = 1, references = 1)))
        assertEquals(240, hotspot.bytes(spilling(ints = 1, references = 1)))
        assertEquals(208, art.bytes(spilling(ints = 2, references = 0)))
        assertEquals(216, hotspot.bytes(spilling(ints = 2, references = 0)))
    }

    private fun spilling(
        ints: Int,
        references: Int,
    ): StructStorage {

        val intFields = List(ints) { index -> FieldStorage(I32, listOf(Lane(LaneKind.INT, index, spilled = true))) }
        val referenceFields = List(references) { index ->
            FieldStorage(ANYREF, listOf(Lane(LaneKind.REFERENCE, index, spilled = true)))
        }

        return StructStorage(UNUSED_CLASS, intFields + referenceFields, inlineLanes = 41, final = true)
    }

    private companion object {
        const val UNUSED_CLASS = "L${STORAGE_PACKAGE}Unused;"
    }
}
