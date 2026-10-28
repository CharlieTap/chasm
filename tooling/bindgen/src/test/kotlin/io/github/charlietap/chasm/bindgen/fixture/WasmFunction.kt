package io.github.charlietap.chasm.bindgen.fixture

import io.github.charlietap.chasm.bindgen.FunctionParameterDefinition
import io.github.charlietap.chasm.bindgen.ReturnTypeDefinition
import io.github.charlietap.chasm.bindgen.StringEncodingStrategy
import io.github.charlietap.chasm.bindgen.Type
import io.github.charlietap.chasm.bindgen.WasmFunction

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
    parameters: List<FunctionParameterDefinition> = [],
    returnType: ReturnTypeDefinition = returnTypeDefinition(),
) = WasmFunction(
    name = name,
    parameters = parameters,
    returnType = returnType,
)
