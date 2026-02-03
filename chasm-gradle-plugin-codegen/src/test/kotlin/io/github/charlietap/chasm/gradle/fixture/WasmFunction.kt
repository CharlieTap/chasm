package io.github.charlietap.chasm.gradle.fixture

import io.github.charlietap.chasm.gradle.FunctionParameterDefinition
import io.github.charlietap.chasm.gradle.ReturnTypeDefinition
import io.github.charlietap.chasm.gradle.StringEncodingStrategy
import io.github.charlietap.chasm.gradle.Type
import io.github.charlietap.chasm.gradle.WasmFunction

fun functionParameterDefinition(
    name: String = "",
    type: Type = type(),
    stringEncodingStrategy: StringEncodingStrategy? = null,
) = FunctionParameterDefinition(
    name = name,
    type = type,
    stringEncodingStrategy = stringEncodingStrategy,
)

fun returnTypeDefinition(
    type: Type = type(),
    stringEncodingStrategy: StringEncodingStrategy? = null,
) = ReturnTypeDefinition(
    type = type,
    stringEncodingStrategy = stringEncodingStrategy,
)

fun wasmFunction(
    name: String = "",
    parameters: List<FunctionParameterDefinition> = emptyList(),
    returnType: ReturnTypeDefinition = returnTypeDefinition(),
) = WasmFunction(
    name = name,
    parameters = parameters,
    returnType = returnType,
)
