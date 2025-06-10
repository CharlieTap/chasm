package io.github.charlietap.chasm.gradle.fixture

import io.github.charlietap.chasm.gradle.Aggregate
import io.github.charlietap.chasm.gradle.Field
import io.github.charlietap.chasm.gradle.Function
import io.github.charlietap.chasm.gradle.FunctionParameter
import io.github.charlietap.chasm.gradle.FunctionReturn
import io.github.charlietap.chasm.gradle.GeneratedType
import io.github.charlietap.chasm.gradle.Property
import io.github.charlietap.chasm.gradle.Scalar
import io.github.charlietap.chasm.gradle.Type
import io.github.charlietap.chasm.gradle.WasmInterface


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

internal fun function(
    name: String = "",
    params: List<FunctionParameter> = emptyList(),
    returns: FunctionReturn = functionReturn(),
) = Function(
    name = name,
    params = params,
    returns = returns,
)

internal fun property(
    name: String = "",
    type: Type = type(),
    const: Boolean = false,
) = Property(
    name = name,
    type = type,
    const = const,
)

internal fun wasmInterface(
    types: List<GeneratedType> = emptyList(),
    functions: List<Function> = emptyList(),
    properties: List<Property> = emptyList(),
) = WasmInterface(
    types = types,
    functions = functions,
    properties = properties,
)
