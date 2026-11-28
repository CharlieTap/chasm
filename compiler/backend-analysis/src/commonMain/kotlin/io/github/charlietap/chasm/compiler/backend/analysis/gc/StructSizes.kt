package io.github.charlietap.chasm.compiler.backend.analysis.gc

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.compiled.format.ArtifactTarget

@InternalChasmApi
class StructSizes(
    target: ArtifactTarget,
) {
    private val baseBytes = when (target) {
        ArtifactTarget.HOTSPOT -> HOTSPOT_BASE_BYTES
        ArtifactTarget.ART -> ART_BASE_BYTES
    }

    private val arrayHeaderBytes = when (target) {
        ArtifactTarget.HOTSPOT -> HOTSPOT_ARRAY_HEADER_BYTES
        ArtifactTarget.ART -> ART_ARRAY_HEADER_BYTES
    }

    fun bytes(storage: StructStorage): Int {

        val spillFieldBytes = if (storage.final) SPILL_ARRAY_FIELD_BYTES else 0
        val objectBytes = aligned(baseBytes + SLOT_BYTES * storage.inlineLanes + spillFieldBytes)

        return objectBytes + spillArrayBytes(storage.spilledInts) + spillArrayBytes(storage.spilledReferences)
    }

    private fun spillArrayBytes(length: Int): Int {
        return if (length == 0) 0 else aligned(arrayHeaderBytes + SLOT_BYTES * length)
    }

    private fun aligned(bytes: Int): Int {
        return (bytes + ALIGNMENT - 1) and (ALIGNMENT - 1).inv()
    }

    private companion object {
        const val HOTSPOT_BASE_BYTES = 16
        const val HOTSPOT_ARRAY_HEADER_BYTES = 16
        const val ART_BASE_BYTES = 12
        const val ART_ARRAY_HEADER_BYTES = 12
        const val SLOT_BYTES = 4
        const val SPILL_ARRAY_FIELD_BYTES = 8
        const val ALIGNMENT = 8
    }
}
