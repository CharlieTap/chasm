package io.github.charlietap.chasm.bindgen

data class FunctionParameterDefinition(
    val name: String,
    val type: Type,
    val stringEncodingStrategy: StringEncodingStrategy? = null,
    val stringAllocationStrategy: StringAllocationStrategy? = null,
)

data class ReturnTypeDefinition(
    val type: Type,
    val stringEncodingStrategy: StringEncodingStrategy? = null,
)

data class WasmFunction(
    val name: String,
    val parameters: List<FunctionParameterDefinition>,
    val returnType: ReturnTypeDefinition,
)
