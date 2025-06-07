package io.github.charlietap.chasm.gradle

import java.io.Serializable

data class CodegenConfig(
    val transformStrings: Boolean = false,
    val generateTypesafeGlobalProperties: Boolean = false,
) : Serializable
