package io.github.charlietap.chasm.bindgen.fixture

import com.squareup.kotlinpoet.ClassName
import io.github.charlietap.chasm.bindgen.Aggregate
import io.github.charlietap.chasm.bindgen.ExportedAllocator
import io.github.charlietap.chasm.bindgen.Field
import io.github.charlietap.chasm.bindgen.Function
import io.github.charlietap.chasm.bindgen.FunctionImplementation
import io.github.charlietap.chasm.bindgen.FunctionParameter
import io.github.charlietap.chasm.bindgen.FunctionProxy
import io.github.charlietap.chasm.bindgen.FunctionReturn
import io.github.charlietap.chasm.bindgen.GeneratedType
import io.github.charlietap.chasm.bindgen.GlobalProxy
import io.github.charlietap.chasm.bindgen.MemoryBinding
import io.github.charlietap.chasm.bindgen.Property
import io.github.charlietap.chasm.bindgen.PropertyImplementation
import io.github.charlietap.chasm.bindgen.Scalar
import io.github.charlietap.chasm.bindgen.StringAllocationStrategy
import io.github.charlietap.chasm.bindgen.StringEncodingStrategy
import io.github.charlietap.chasm.bindgen.Type
import io.github.charlietap.chasm.bindgen.VALUE_CLASS_NAME
import io.github.charlietap.chasm.bindgen.WasmInterface

internal fun integerScalarType() = Scalar.Integer

internal fun longScalarType() = Scalar.Long

internal fun floatScalarType() = Scalar.Float

internal fun doubleScalarType() = Scalar.Double

internal fun stringScalarType() = Scalar.String

internal fun unitScalarType() = Scalar.Unit

internal fun scalarType(): Scalar = unitScalarType()

internal fun type(): Type = scalarType()

internal fun field(
    name: String = "",
    type: Type = type(),
) = Field(
    name = name,
    type = type,
)

internal fun generatedType(
    name: String = "",
    fields: List<Field> = [],
) = GeneratedType(
    name = name,
    fields = fields,
)

internal fun aggregateType(
    generated: GeneratedType = generatedType(),
) = Aggregate(
    generated = generated,
)

internal fun functionParameter(
    name: String = "",
    type: Type = type(),
    stringAllocationStrategy: StringAllocationStrategy? = null,
    stringEncodingStrategy: StringEncodingStrategy? = null,
) = FunctionParameter(
    name = name,
    type = type,
    stringAllocationStrategy = stringAllocationStrategy,
    stringEncodingStrategy = stringEncodingStrategy,
)

internal fun functionReturn(
    type: Type = type(),
    stringEncodingStrategy: StringEncodingStrategy? = null,
) = FunctionReturn(
    type = type,
    stringEncodingStrategy = stringEncodingStrategy,
)

internal fun functionImplementation(): FunctionImplementation = functionProxy()

internal fun functionProxy(
    name: String = "",
) = FunctionProxy(
    name = name,
)

internal fun function(
    name: String = "",
    params: List<FunctionParameter> = [],
    returns: FunctionReturn = functionReturn(),
    resultTypes: List<io.github.charlietap.chasm.type.ValueType> = emptyList(),
    implementation: FunctionImplementation = functionImplementation(),
) = Function(
    name = name,
    params = params,
    returns = returns,
    resultTypes = resultTypes,
    implementation = implementation,
)

internal fun globalProxy(
    name: String = "",
    source: ClassName = VALUE_CLASS_NAME,
) = GlobalProxy(
    name = name,
    source = source,
)

internal fun propertyImplementation(): PropertyImplementation = globalProxy()

internal fun property(
    name: String = "",
    type: Type = type(),
    const: Boolean = false,
    implementation: PropertyImplementation = propertyImplementation(),
) = Property(
    name = name,
    type = type,
    const = const,
    implementation = implementation,
)

internal fun memoryBinding(
    name: String = "",
    source: String = "",
    exposed: Boolean = true,
    backingName: String = "_$name",
) = MemoryBinding(
    name = name,
    source = source,
    exposed = exposed,
    backingName = backingName,
)

internal fun wasmInterface(
    interfaceName: String = "",
    packageName: String = "",
    initializers: Set<String> = [],
    types: List<GeneratedType> = [],
    functions: List<Function> = [],
    properties: List<Property> = [],
    memories: List<MemoryBinding> = [],
    allocator: ExportedAllocator? = null,
    linksWasiPreview1: Boolean = false,
) = WasmInterface(
    interfaceName = interfaceName,
    packageName = packageName,
    allocator = allocator,
    initializers = initializers,
    types = types,
    functions = functions,
    properties = properties,
    memories = memories,
    linksWasiPreview1 = linksWasiPreview1,
)
