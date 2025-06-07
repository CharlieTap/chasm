package io.github.charlietap.chasm.gradle.fixture

import io.github.charlietap.chasm.gradle.Function
import io.github.charlietap.chasm.gradle.GeneratedType
import io.github.charlietap.chasm.gradle.Property
import io.github.charlietap.chasm.gradle.WasmInterface

internal fun wasmInterface(
    types: List<GeneratedType> = emptyList(),
    functions: List<Function> = emptyList(),
    properties: List<Property> = emptyList(),
) = WasmInterface(
    types = types,
    functions = functions,
    properties = properties,
)
