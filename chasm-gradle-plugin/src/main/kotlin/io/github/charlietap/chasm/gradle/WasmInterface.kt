package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.type.ValueType

internal data class FunctionParameter(
    val name: String,
    val type: ValueType,
)

internal sealed interface FunctionReturn {
    @JvmInline
    value class Primitive(val type: ValueType) : FunctionReturn

    object String: FunctionReturn

    data class Type(val generated: GeneratedType) : FunctionReturn

    object Unit: FunctionReturn
}

internal data class Function(
    val name: String,
    val params: List<FunctionParameter>,
    val returns: FunctionReturn,
)

internal data class Field(
    val name: String,
    val type: ValueType,
)

internal data class GeneratedType(
    val name: String,
    val fields: List<Field>,
)

internal sealed interface Property {

    val name: String

    data class GlobalProperty(
        override val name: String,
    ): Property

    data class MemoryProperty(
        override val name: String,
    ): Property

    data class TableProperty(
        override val name: String,
    ): Property

    data class TagProperty(
        override val name: String,
    ): Property
}

internal data class WasmInterface(
    val types: List<GeneratedType>,
    val functions: List<Function>,
    val properties: List<Property>,
)
