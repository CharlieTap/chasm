package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.gradle.fixture.doubleScalarType
import io.github.charlietap.chasm.gradle.fixture.floatScalarType
import io.github.charlietap.chasm.gradle.fixture.globalProxy
import io.github.charlietap.chasm.gradle.fixture.integerScalarType
import io.github.charlietap.chasm.gradle.fixture.longScalarType
import io.github.charlietap.chasm.gradle.fixture.property
import io.github.charlietap.chasm.gradle.fixture.wasmInterface
import io.github.charlietap.chasm.vm.WasmVirtualMachine
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
                implementation = globalProxy("value", WasmVirtualMachine.Value.I32::class),
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
                implementation = globalProxy("value", WasmVirtualMachine.Value.I32::class),
            ),
        ),
    )

    @Test
    fun `generates every numeric global type`() = assertGlobal(
        scenario = "NumericGlobals",
        properties = listOf(
            property("intValue", integerScalarType(), true, globalProxy("int_value", WasmVirtualMachine.Value.I32::class)),
            property("longValue", longScalarType(), true, globalProxy("long_value", WasmVirtualMachine.Value.I64::class)),
            property("floatValue", floatScalarType(), true, globalProxy("float_value", WasmVirtualMachine.Value.F32::class)),
            property("doubleValue", doubleScalarType(), true, globalProxy("double_value", WasmVirtualMachine.Value.F64::class)),
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
