package io.github.charlietap.chasm.gradle.fixture

import io.github.charlietap.chasm.gradle.CodegenConfig

internal fun codegenConfig(
    generateTypesafeGlobalProperties: Boolean = false,
) = CodegenConfig(
    generateTypesafeGlobalProperties = generateTypesafeGlobalProperties,
)
