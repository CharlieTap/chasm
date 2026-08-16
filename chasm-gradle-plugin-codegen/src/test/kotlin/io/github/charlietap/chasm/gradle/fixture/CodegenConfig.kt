package io.github.charlietap.chasm.gradle.fixture

import io.github.charlietap.chasm.gradle.CodegenConfig

internal fun codegenConfig(
    generateTypesafeGlobalProperties: Boolean = false,
    generateTypesafeMemoryProperties: Boolean = false,
    generateSuspendingFactories: Boolean = false,
) = CodegenConfig(
    generateTypesafeGlobalProperties = generateTypesafeGlobalProperties,
    generateTypesafeMemoryProperties = generateTypesafeMemoryProperties,
    generateSuspendingFactories = generateSuspendingFactories,
)
