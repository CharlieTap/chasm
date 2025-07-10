package io.github.charlietap.chasm.gradle

import java.io.Serializable

data class CodegenConfig(
    val generateTypesafeGlobalProperties: Boolean = false,
) : Serializable
