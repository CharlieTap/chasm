package io.github.charlietap.chasm.bindgen

import com.squareup.kotlinpoet.ClassName
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.ValueType

internal data class Aggregate(
    val generated: GeneratedType,
) : Type

internal data class StructViewType(
    val className: ClassName,
    val nullable: Boolean,
) : Type

internal data class ArrayReferenceType(
    val nullable: Boolean,
) : Type

internal data class AnyReferenceType(
    val nullable: Boolean,
) : Type

internal data class StructViewField(
    val name: String,
    val index: Int,
    val type: Type,
    val mutable: Boolean,
)

internal data class StructView(
    val className: ClassName,
    val fields: List<StructViewField>,
)

internal sealed interface ParameterStringEncoding

internal data class LinearMemoryString(
    val strategy: StringEncodingStrategy,
) : ParameterStringEncoding

internal data class GcArrayString(
    val typeIndex: Int,
    val elementType: PackedType,
) : ParameterStringEncoding

internal data class FunctionParameter(
    val name: String,
    val type: Type,
    val stringAllocationStrategy: StringAllocationStrategy? = null,
    val stringEncoding: ParameterStringEncoding? = null,
)

internal val FunctionParameter.linearMemoryStrategy: StringEncodingStrategy?
    get() {
        return (stringEncoding as? LinearMemoryString)?.strategy
    }

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
            param.linearMemoryStrategy == StringEncodingStrategy.POINTER_AND_LENGTH
        ) {
            2
        } else {
            1
        }
    }

internal fun functionInputBufferName(inputCount: Int): String {
    return FUNCTION_INPUT_BUFFER_NAME + inputCount
}

internal fun preparedFunctionPropertyName(function: Function): String {
    return function.name + "PreparedFunction"
}

internal fun gcArrayTypePropertyName(typeIndex: Int): String {
    return "arrayType$typeIndex"
}

internal val Function.invocationSlotCount: Int
    get() {
        return maxOf(inputCount, resultTypes.size)
    }

internal val Function.gcArrayTypeIndices: List<Int>
    get() {
        return params.mapNotNull { param -> (param.stringEncoding as? GcArrayString)?.typeIndex }
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
    val source: ClassName,
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
    val linksWasiPreview1: Boolean = false,
    val structViews: List<StructView> = emptyList(),
)
