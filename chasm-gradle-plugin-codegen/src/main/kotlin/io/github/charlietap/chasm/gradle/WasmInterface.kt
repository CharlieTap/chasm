package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.vm.WasmVirtualMachine
import kotlin.reflect.KClass

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
    val resultTypes: List<ValueType>,
    val implementation: FunctionImplementation,
)

internal val Function.inputCount: Int
    get() = params.sumOf { param ->
        if (
            param.type == Scalar.String &&
            param.stringEncodingStrategy == StringEncodingStrategy.POINTER_AND_LENGTH
        ) {
            2
        } else {
            1
        }
    }

internal fun functionInputBufferName(inputCount: Int): String {
    return FUNCTION_INPUT_BUFFER_NAME + inputCount
}

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

internal data class MemoryBinding(
    val name: String,
    val source: String,
    val exposed: Boolean,
    val backingName: String = "_$name",
)

internal const val DEFAULT_MEMORY_EXPORT_NAME = "memory"
internal const val DEFAULT_MEMORY_BACKING_NAME = "_$DEFAULT_MEMORY_EXPORT_NAME"

internal data class WasmInterface(
    val interfaceName: String,
    val packageName: String,
    val allocator: ExportedAllocator?,
    val initializers: Set<String>,
    val types: List<GeneratedType>,
    val functions: List<Function>,
    val properties: List<Property>,
    val memories: List<MemoryBinding> = emptyList(),
)
