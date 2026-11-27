package io.github.charlietap.chasm.compiled.format

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
const val TARGET_HOTSPOT: Int = 0

@InternalChasmApi
const val TARGET_ART: Int = 1

@InternalChasmApi
const val UNPINNED_OFFSET: Long = -1L

enum class ArtifactTarget(
    val code: Int,
) {
    HOTSPOT(TARGET_HOTSPOT),
    ART(TARGET_ART),
}
