package io.github.charlietap.chasm.gradle

import java.io.Serializable

data class StringAllocationStrategy(
    val freeAfterCall: Boolean,
) : Serializable
