package io.github.charlietap.chasm.gradle.fixture

import io.github.charlietap.chasm.gradle.CodegenConfig

internal fun codegenConfig(
    transformStrings: Boolean = true,
    generateTypesafeGlobalFunctions: Boolean = false,
) = CodegenConfig(
    transformStrings = transformStrings,
    generateTypesafeGlobalFunctions = generateTypesafeGlobalFunctions,
)
