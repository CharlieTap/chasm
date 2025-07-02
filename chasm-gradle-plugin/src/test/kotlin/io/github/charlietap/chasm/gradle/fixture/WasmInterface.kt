package io.github.charlietap.chasm.gradle.fixture

import io.github.charlietap.chasm.gradle.Aggregate
import io.github.charlietap.chasm.gradle.Field
import io.github.charlietap.chasm.gradle.Function
import io.github.charlietap.chasm.gradle.FunctionImplementation
import io.github.charlietap.chasm.gradle.FunctionParameter
import io.github.charlietap.chasm.gradle.FunctionProxy
import io.github.charlietap.chasm.gradle.FunctionReturn
import io.github.charlietap.chasm.gradle.GeneratedType
import io.github.charlietap.chasm.gradle.GlobalProxy
import io.github.charlietap.chasm.gradle.Property
import io.github.charlietap.chasm.gradle.PropertyImplementation
import io.github.charlietap.chasm.gradle.Scalar
import io.github.charlietap.chasm.gradle.Type
import io.github.charlietap.chasm.gradle.WasmInterface
import kotlin.reflect.KClass

internal fun integerScalarType() = Scalar.Integer

internal fun longScalarType() = Scalar.Long

internal fun floatScalarType() = Scalar.Float

internal fun doubleScalarType() = Scalar.Double

internal fun stringScalarType() = Scalar.String

internal fun unitScalarType() = Scalar.Unit

internal fun scalarType(): Scalar = integerScalarType()

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
    fields: List<Field> = emptyList(),
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
) = FunctionParameter(
    name = name,
    type = type,
)

internal fun functionReturn(
    type: Type = type(),
) = FunctionReturn(
    type = type,
)

internal fun functionImplementation(): FunctionImplementation = functionProxy()

internal fun functionProxy(
    name: String = "",
) = FunctionProxy(
    name = name,
)

internal fun function(
    name: String = "",
    params: List<FunctionParameter> = emptyList(),
    returns: FunctionReturn = functionReturn(),
    implementation: FunctionImplementation = functionImplementation(),
) = Function(
    name = name,
    params = params,
    returns = returns,
    implementation = implementation,
)

internal fun globalProxy(
    name: String = "",
    source: KClass<*> = Int::class,
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

internal fun wasmInterface(
    initializers: Set<String> = emptySet(),
    types: List<GeneratedType> = emptyList(),
    functions: List<Function> = emptyList(),
    properties: List<Property> = emptyList(),
) = WasmInterface(
    initializers = initializers,
    types = types,
    functions = functions,
    properties = properties,
)
