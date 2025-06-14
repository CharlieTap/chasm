package io.github.charlietap.chasm.gradle

import java.io.Serializable

data class CodegenConfig(
    val transformStrings: Boolean = true,
    val generateTypesafeGlobalProperties: Boolean = false,
): Serializable
