package io.github.charlietap.chasm.gradle

import java.io.Serializable

data class ExportedAllocator(
    val allocationFunction: String,
    val deallocationFunction: String,
) : Serializable
