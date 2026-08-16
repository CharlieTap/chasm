package io.github.charlietap.chasm.gradle

import java.io.Serializable
import kotlin.ExperimentalVersionOverloading
import kotlin.IntroducedAt

@OptIn(ExperimentalVersionOverloading::class)
data class CodegenConfig(
    val generateTypesafeGlobalProperties: Boolean = false,
    @IntroducedAt("2.2.0")
    val generateTypesafeMemoryProperties: Boolean = false,
    @IntroducedAt("2.2.0")
    val generateSuspendingFactories: Boolean = false,
) : Serializable
