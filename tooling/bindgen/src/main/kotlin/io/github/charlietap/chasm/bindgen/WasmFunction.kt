package io.github.charlietap.chasm.bindgen

import java.io.Serializable

data class FunctionParameterDefinition(
    val name: String,
    val type: Type,
    val stringEncodingStrategy: StringEncodingStrategy? = null,
    val stringAllocationStrategy: StringAllocationStrategy? = null,
) : Serializable

data class ReturnTypeDefinition(
    val type: Type,
    val stringEncodingStrategy: StringEncodingStrategy? = null,
) : Serializable

data class WasmFunction(
    val name: String,
    val parameters: List<FunctionParameterDefinition>,
    val returnType: ReturnTypeDefinition,
) : Serializable
