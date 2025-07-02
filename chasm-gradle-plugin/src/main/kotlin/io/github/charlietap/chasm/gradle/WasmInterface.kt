package io.github.charlietap.chasm.gradle

import kotlin.reflect.KClass

internal sealed interface Type

internal sealed interface Scalar : Type {
    object Integer : Scalar

    object Long : Scalar

    object Float : Scalar

    object Double : Scalar

    object String : Scalar

    object Unit : Scalar
}

internal data class Aggregate(
    val generated: GeneratedType,
) : Type

internal data class FunctionParameter(
    val name: String,
    val type: Type,
)

@JvmInline
internal value class FunctionReturn(
    val type: Type,
)

internal sealed interface FunctionImplementation

internal data class FunctionProxy(
    val name: String,
) : FunctionImplementation

internal data class Function(
    val name: String,
    val params: List<FunctionParameter>,
    val returns: FunctionReturn,
    val implementation: FunctionImplementation,
)

internal data class Field(
    val name: String,
    val type: Type,
)

internal data class GeneratedType(
    val name: String,
    val fields: List<Field>,
)

internal sealed interface PropertyImplementation

internal data class GlobalProxy(
    val name: String,
    val source: KClass<*>,
) : PropertyImplementation

internal data class Property(
    val name: String,
    val type: Type,
    val const: Boolean,
    val implementation: PropertyImplementation,
)

internal data class WasmInterface(
    val initializers: Set<String>,
    val types: List<GeneratedType>,
    val functions: List<Function>,
    val properties: List<Property>,
)
