package io.github.charlietap.chasm.gradle.fixture

import io.github.charlietap.chasm.gradle.CodegenConfig

internal fun codegenConfig(
    transformStrings: Boolean = true,
    generateTypesafeGlobalProperties: Boolean = false,
) = CodegenConfig(
    transformStrings = transformStrings,
    generateTypesafeGlobalProperties = generateTypesafeGlobalProperties,
)
