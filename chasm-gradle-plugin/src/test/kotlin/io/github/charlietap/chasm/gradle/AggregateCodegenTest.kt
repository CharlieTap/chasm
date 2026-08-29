package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.fixture.type.f32ValueType
import io.github.charlietap.chasm.fixture.type.f64ValueType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.gradle.fixture.aggregateType
import io.github.charlietap.chasm.gradle.fixture.doubleScalarType
import io.github.charlietap.chasm.gradle.fixture.field
import io.github.charlietap.chasm.gradle.fixture.floatScalarType
import io.github.charlietap.chasm.gradle.fixture.function
import io.github.charlietap.chasm.gradle.fixture.functionParameter
import io.github.charlietap.chasm.gradle.fixture.functionProxy
import io.github.charlietap.chasm.gradle.fixture.functionReturn
import io.github.charlietap.chasm.gradle.fixture.generatedType
import io.github.charlietap.chasm.gradle.fixture.globalProxy
import io.github.charlietap.chasm.gradle.fixture.integerScalarType
import io.github.charlietap.chasm.gradle.fixture.longScalarType
import io.github.charlietap.chasm.gradle.fixture.memoryBinding
import io.github.charlietap.chasm.gradle.fixture.property
import io.github.charlietap.chasm.gradle.fixture.stringScalarType
import io.github.charlietap.chasm.gradle.fixture.wasmInterface
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.vm.WasmVirtualMachine
import kotlin.test.Test

class AggregateCodegenTest {

    @Test
    fun `generates complete test service`() {
        val multipleReturnResult = generatedType(
            name = "MultipleReturnFunctionResult",
            fields = listOf(
                field("r0", integerScalarType()),
                field("r1", longScalarType()),
            ),
        )
        assertGenerates(
            category = "aggregate",
            wasmInterface = wasmInterface(
                interfaceName = "TestService",
                packageName = "com.test.chasm",
                types = listOf(multipleReturnResult),
                functions = listOf(
                    numericReturnFunction("intFunction", "int_function", integerScalarType(), i32ValueType()),
                    numericReturnFunction("longFunction", "long_function", longScalarType(), i64ValueType()),
                    numericReturnFunction("floatFunction", "float_function", floatScalarType(), f32ValueType()),
                    numericReturnFunction("doubleFunction", "double_function", doubleScalarType(), f64ValueType()),
                    stringReturnFunction(
                        "palStringFunction",
                        "pal_string_function",
                        StringEncodingStrategy.POINTER_AND_LENGTH,
                        listOf(i32ValueType(), i32ValueType()),
                    ),
                    stringReturnFunction(
                        "lengthPrefixedStringFunction",
                        "length_prefixed_string_function",
                        StringEncodingStrategy.LENGTH_PREFIXED,
                        listOf(i32ValueType()),
                    ),
                    stringReturnFunction(
                        "nullTerminatedStringFunction",
                        "null_terminated_string_function",
                        StringEncodingStrategy.NULL_TERMINATED,
                        listOf(i32ValueType()),
                    ),
                    stringReturnFunction(
                        "packedI64StringFunction",
                        "packed_i64_string_function",
                        StringEncodingStrategy.PACKED_POINTER_AND_LENGTH,
                        listOf(i64ValueType()),
                    ),
                    function(name = "unitFunction", implementation = functionProxy("unit_function")),
                    function(
                        name = "multipleParamFunction",
                        params = listOf(
                            functionParameter("p0", integerScalarType()),
                            functionParameter("p1", doubleScalarType()),
                        ),
                        returns = functionReturn(doubleScalarType()),
                        resultTypes = listOf(f64ValueType()),
                        implementation = functionProxy("multiple_param_function"),
                    ),
                    function(
                        name = "multipleReturnFunction",
                        returns = functionReturn(aggregateType(multipleReturnResult)),
                        resultTypes = listOf(i32ValueType(), i64ValueType()),
                        implementation = functionProxy("multiple_return_function"),
                    ),
                ),
                properties = listOf(
                    property(
                        "mutableGlobal",
                        integerScalarType(),
                        false,
                        globalProxy("mutable_global", WasmVirtualMachine.Value.I32::class),
                    ),
                    property(
                        "immutableGlobal",
                        integerScalarType(),
                        true,
                        globalProxy("immutable_global", WasmVirtualMachine.Value.I32::class),
                    ),
                ),
                memories = listOf(memoryBinding("memory", "memory")),
            ),
        )
    }

    private fun numericReturnFunction(
        name: String,
        exportName: String,
        type: Type,
        resultType: ValueType,
    ) = function(
        name = name,
        returns = functionReturn(type),
        resultTypes = listOf(resultType),
        implementation = functionProxy(exportName),
    )

    private fun stringReturnFunction(
        name: String,
        exportName: String,
        encoding: StringEncodingStrategy,
        resultTypes: List<ValueType>,
    ) = function(
        name = name,
        returns = functionReturn(stringScalarType(), encoding),
        resultTypes = resultTypes,
        implementation = functionProxy(exportName),
    )
}
