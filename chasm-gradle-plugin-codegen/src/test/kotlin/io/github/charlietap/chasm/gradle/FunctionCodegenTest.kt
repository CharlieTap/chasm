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
import io.github.charlietap.chasm.gradle.fixture.integerScalarType
import io.github.charlietap.chasm.gradle.fixture.longScalarType
import io.github.charlietap.chasm.gradle.fixture.memoryBinding
import io.github.charlietap.chasm.gradle.fixture.stringScalarType
import io.github.charlietap.chasm.gradle.fixture.wasmInterface
import io.github.charlietap.chasm.type.ValueType
import kotlin.test.Test

class FunctionCodegenTest {

    @Test
    fun `generates unit return`() = assertFunction(
        scenario = "UnitReturn",
        function = function(name = "invoke", implementation = functionProxy("invoke")),
    )

    @Test
    fun `generates int return`() = assertNumericReturn(
        scenario = "IntReturn",
        type = integerScalarType(),
        resultType = i32ValueType(),
    )

    @Test
    fun `generates long return`() = assertNumericReturn(
        scenario = "LongReturn",
        type = longScalarType(),
        resultType = i64ValueType(),
    )

    @Test
    fun `generates float return`() = assertNumericReturn(
        scenario = "FloatReturn",
        type = floatScalarType(),
        resultType = f32ValueType(),
    )

    @Test
    fun `generates double return`() = assertNumericReturn(
        scenario = "DoubleReturn",
        type = doubleScalarType(),
        resultType = f64ValueType(),
    )

    @Test
    fun `generates int parameter`() = assertNumericParameter(
        scenario = "IntParam",
        type = integerScalarType(),
    )

    @Test
    fun `generates long parameter`() = assertNumericParameter(
        scenario = "LongParam",
        type = longScalarType(),
    )

    @Test
    fun `generates float parameter`() = assertNumericParameter(
        scenario = "FloatParam",
        type = floatScalarType(),
    )

    @Test
    fun `generates double parameter`() = assertNumericParameter(
        scenario = "DoubleParam",
        type = doubleScalarType(),
    )

    @Test
    fun `generates multiple parameters`() = assertFunction(
        scenario = "MultipleParameters",
        function = function(
            name = "invoke",
            params = listOf(
                functionParameter("p0", integerScalarType()),
                functionParameter("p1", doubleScalarType()),
            ),
            returns = functionReturn(doubleScalarType()),
            resultTypes = listOf(f64ValueType()),
            implementation = functionProxy("invoke"),
        ),
    )

    @Test
    fun `generates multiple returns`() {
        val result = generatedType(
            name = "MultipleReturnsResult",
            fields = listOf(
                field("r0", integerScalarType()),
                field("r1", longScalarType()),
                field("r2", floatScalarType()),
                field("r3", doubleScalarType()),
            ),
        )
        assertGenerates(
            category = "function",
            wasmInterface = wasmInterface(
                interfaceName = "MultipleReturns",
                packageName = PACKAGE_NAME,
                types = listOf(result),
                functions = listOf(
                    function(
                        name = "invoke",
                        returns = functionReturn(aggregateType(result)),
                        resultTypes = listOf(i32ValueType(), i64ValueType(), f32ValueType(), f64ValueType()),
                        implementation = functionProxy("invoke"),
                    ),
                ),
            ),
        )
    }

    @Test
    fun `generates shared buffers for multiple arities`() = assertGenerates(
        category = "function",
        wasmInterface = wasmInterface(
            interfaceName = "MultipleArities",
            packageName = PACKAGE_NAME,
            functions = listOf(
                parameterFunction("one", 1),
                parameterFunction("two", 2),
                parameterFunction("three", 3),
            ),
        ),
    )

    @Test
    fun `generates pointer and length string parameter`() = assertStringParameter(
        scenario = "PointerAndLengthStringParam",
        encoding = StringEncodingStrategy.POINTER_AND_LENGTH,
    )

    @Test
    fun `generates pointer and length string return`() = assertStringReturn(
        scenario = "PointerAndLengthStringReturn",
        encoding = StringEncodingStrategy.POINTER_AND_LENGTH,
        resultTypes = listOf(i32ValueType(), i32ValueType()),
    )

    @Test
    fun `generates length prefixed string parameter`() = assertStringParameter(
        scenario = "LengthPrefixedStringParam",
        encoding = StringEncodingStrategy.LENGTH_PREFIXED,
    )

    @Test
    fun `generates length prefixed string return`() = assertStringReturn(
        scenario = "LengthPrefixedStringReturn",
        encoding = StringEncodingStrategy.LENGTH_PREFIXED,
        resultTypes = listOf(i32ValueType()),
    )

    @Test
    fun `generates null terminated string parameter`() = assertStringParameter(
        scenario = "NullTerminatedStringParam",
        encoding = StringEncodingStrategy.NULL_TERMINATED,
    )

    @Test
    fun `generates null terminated string return`() = assertStringReturn(
        scenario = "NullTerminatedStringReturn",
        encoding = StringEncodingStrategy.NULL_TERMINATED,
        resultTypes = listOf(i32ValueType()),
    )

    @Test
    fun `generates packed string parameter`() = assertStringParameter(
        scenario = "PackedStringParam",
        encoding = StringEncodingStrategy.PACKED_POINTER_AND_LENGTH,
    )

    @Test
    fun `generates packed string return`() = assertStringReturn(
        scenario = "PackedStringReturn",
        encoding = StringEncodingStrategy.PACKED_POINTER_AND_LENGTH,
        resultTypes = listOf(i64ValueType()),
    )

    @Test
    fun `generates freed string allocation`() = assertStringParameter(
        scenario = "FreeStringAllocation",
        encoding = StringEncodingStrategy.POINTER_AND_LENGTH,
        freeAfterCall = true,
    )

    private fun assertNumericReturn(
        scenario: String,
        type: Type,
        resultType: ValueType,
    ) = assertFunction(
        scenario = scenario,
        function = function(
            name = "invoke",
            returns = functionReturn(type),
            resultTypes = listOf(resultType),
            implementation = functionProxy("invoke"),
        ),
    )

    private fun assertNumericParameter(
        scenario: String,
        type: Type,
    ) = assertFunction(
        scenario = scenario,
        function = function(
            name = "invoke",
            params = listOf(functionParameter("value", type)),
            implementation = functionProxy("invoke"),
        ),
    )

    private fun assertStringParameter(
        scenario: String,
        encoding: StringEncodingStrategy,
        freeAfterCall: Boolean = false,
    ) = assertGenerates(
        category = "function",
        wasmInterface = wasmInterface(
            interfaceName = scenario,
            packageName = PACKAGE_NAME,
            allocator = ExportedAllocator("alloc", "free"),
            memories = listOf(memoryBinding("memory", "memory", exposed = false)),
            functions = listOf(
                function(
                    name = "invoke",
                    params = listOf(
                        functionParameter(
                            name = "value",
                            type = stringScalarType(),
                            stringAllocationStrategy = StringAllocationStrategy(freeAfterCall),
                            stringEncodingStrategy = encoding,
                        ),
                    ),
                    implementation = functionProxy("invoke"),
                ),
            ),
        ),
    )

    private fun assertStringReturn(
        scenario: String,
        encoding: StringEncodingStrategy,
        resultTypes: List<ValueType>,
    ) = assertGenerates(
        category = "function",
        wasmInterface = wasmInterface(
            interfaceName = scenario,
            packageName = PACKAGE_NAME,
            memories = listOf(memoryBinding("memory", "memory", exposed = false)),
            functions = listOf(
                function(
                    name = "invoke",
                    returns = functionReturn(stringScalarType(), encoding),
                    resultTypes = resultTypes,
                    implementation = functionProxy("invoke"),
                ),
            ),
        ),
    )

    private fun assertFunction(
        scenario: String,
        function: Function,
    ) = assertGenerates(
        category = "function",
        wasmInterface = wasmInterface(
            interfaceName = scenario,
            packageName = PACKAGE_NAME,
            functions = listOf(function),
        ),
    )

    private fun parameterFunction(
        name: String,
        parameterCount: Int,
    ) = function(
        name = name,
        params = List(parameterCount) { index -> functionParameter("p$index", integerScalarType()) },
        implementation = functionProxy(name),
    )

    private companion object {
        const val PACKAGE_NAME = "com.test"
    }
}
