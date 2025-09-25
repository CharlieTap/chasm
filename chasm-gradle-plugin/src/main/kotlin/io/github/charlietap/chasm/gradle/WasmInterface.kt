package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.vm.WasmVirtualMachine
import java.io.Serializable
import kotlin.reflect.KClass

sealed interface Type : Serializable

enum class Scalar : Type {
    Integer,
    Long,
    Float,
    Double,
    String,
    Unit,
}

internal data class Aggregate(
    val generated: GeneratedType,
) : Type

internal data class FunctionParameter(
    val name: String,
    val type: Type,
    val stringAllocationStrategy: StringAllocationStrategy? = null,
    val stringEncodingStrategy: StringEncodingStrategy? = null,
)

internal data class FunctionReturn(
    val type: Type,
    val stringEncodingStrategy: StringEncodingStrategy? = null,
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
    val source: KClass<out WasmVirtualMachine.Value>,
) : PropertyImplementation

internal data class Property(
    val name: String,
    val type: Type,
    val const: Boolean,
    val implementation: PropertyImplementation,
)

internal data class WasmInterface(
    val interfaceName: String,
    val packageName: String,
    val allocator: ExportedAllocator?,
    val initializers: Set<String>,
    val types: List<GeneratedType>,
    val functions: List<Function>,
    val properties: List<Property>,
)
