package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.gradle.fixture.doubleScalarType
import io.github.charlietap.chasm.gradle.fixture.floatScalarType
import io.github.charlietap.chasm.gradle.fixture.globalProxy
import io.github.charlietap.chasm.gradle.fixture.integerScalarType
import io.github.charlietap.chasm.gradle.fixture.longScalarType
import io.github.charlietap.chasm.gradle.fixture.property
import io.github.charlietap.chasm.gradle.fixture.wasmInterface
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

    private fun assertGlobal(
        scenario: String,
        properties: List<Property>,
    ) = assertGenerates(
        category = "global",
        wasmInterface = wasmInterface(
            interfaceName = scenario,
            packageName = "com.test",
            properties = properties,
        ),
    )
}
