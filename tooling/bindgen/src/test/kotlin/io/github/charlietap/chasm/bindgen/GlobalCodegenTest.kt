package io.github.charlietap.chasm.bindgen

import io.github.charlietap.chasm.bindgen.fixture.codegenConfig
import io.github.charlietap.chasm.bindgen.fixture.doubleScalarType
import io.github.charlietap.chasm.bindgen.fixture.floatScalarType
import io.github.charlietap.chasm.bindgen.fixture.globalProxy
import io.github.charlietap.chasm.bindgen.fixture.integerScalarType
import io.github.charlietap.chasm.bindgen.fixture.longScalarType
import io.github.charlietap.chasm.bindgen.fixture.property
import io.github.charlietap.chasm.bindgen.fixture.wasmInterface
import kotlin.test.Test

class GlobalCodegenTest {

    @Test
    fun `generates mutable global`() = assertGlobal(
        scenario = "MutableGlobal",
        properties = listOf(
            property(
                name = "value",
                type = integerScalarType(),
                const = false,
                implementation = globalProxy("value", VALUE_I32_CLASS_NAME),
            ),
        ),
    )

    @Test
    fun `generates immutable global`() = assertGlobal(
        scenario = "ImmutableGlobal",
        properties = listOf(
            property(
                name = "value",
                type = integerScalarType(),
                const = true,
                implementation = globalProxy("value", VALUE_I32_CLASS_NAME),
            ),
        ),
    )

    @Test
    fun `generates every numeric global type`() = assertGlobal(
        scenario = "NumericGlobals",
        properties = listOf(
            property("intValue", integerScalarType(), true, globalProxy("int_value", VALUE_I32_CLASS_NAME)),
            property("longValue", longScalarType(), true, globalProxy("long_value", VALUE_I64_CLASS_NAME)),
            property("floatValue", floatScalarType(), true, globalProxy("float_value", VALUE_F32_CLASS_NAME)),
            property("doubleValue", doubleScalarType(), true, globalProxy("double_value", VALUE_F64_CLASS_NAME)),
        ),
    )

    @Test
    fun `generates JIT globals`() {
        assertGlobal(
            scenario = "JitGlobals",
            properties = listOf(
                property("intValue", integerScalarType(), false, globalProxy("int_value", VALUE_I32_CLASS_NAME)),
                property("longValue", longScalarType(), false, globalProxy("long_value", VALUE_I64_CLASS_NAME)),
                property("floatValue", floatScalarType(), false, globalProxy("float_value", VALUE_F32_CLASS_NAME)),
                property("doubleValue", doubleScalarType(), false, globalProxy("double_value", VALUE_F64_CLASS_NAME)),
                property("constant", integerScalarType(), true, globalProxy("constant", VALUE_I32_CLASS_NAME)),
            ),
            config = codegenConfig(executionTier = ExecutionTier.Jit()),
            runtimes = listOf(CodegenRuntime.CHASM),
        )
    }

    private fun assertGlobal(
        scenario: String,
        properties: List<Property>,
        config: CodegenConfig = CodegenConfig(),
        runtimes: Iterable<CodegenRuntime> = CodegenRuntime.entries,
    ) {
        assertGenerates(
            category = "global",
            wasmInterface = wasmInterface(
                interfaceName = scenario,
                packageName = "com.test",
                properties = properties,
            ),
            config = config,
            runtimes = runtimes,
        )
    }
}
