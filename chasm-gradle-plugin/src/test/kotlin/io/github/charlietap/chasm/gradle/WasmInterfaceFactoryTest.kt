package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.fixture.chasm.embedding.exportDefinition
import io.github.charlietap.chasm.fixture.chasm.embedding.moduleInfo
import io.github.charlietap.chasm.fixture.runtime.type.functionExternalType
import io.github.charlietap.chasm.fixture.type.f32ValueType
import io.github.charlietap.chasm.fixture.type.f64ValueType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.gradle.fixture.aggregateType
import io.github.charlietap.chasm.gradle.fixture.codegenConfig
import io.github.charlietap.chasm.gradle.fixture.doubleScalarType
import io.github.charlietap.chasm.gradle.fixture.field
import io.github.charlietap.chasm.gradle.fixture.floatScalarType
import io.github.charlietap.chasm.gradle.fixture.function
import io.github.charlietap.chasm.gradle.fixture.functionProxy
import io.github.charlietap.chasm.gradle.fixture.functionReturn
import io.github.charlietap.chasm.gradle.fixture.generatedType
import io.github.charlietap.chasm.gradle.fixture.integerScalarType
import io.github.charlietap.chasm.gradle.fixture.longScalarType
import io.github.charlietap.chasm.gradle.fixture.stringScalarType
import io.github.charlietap.chasm.gradle.fixture.wasmInterface
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class WasmInterfaceFactoryTest {

    @Test
    fun `can generate a function with a string return type when config value is set to true`() {

        val config = codegenConfig(
            transformStrings = true,
        )
        val info = moduleInfo(
            exports = listOf(
                exportDefinition(
                    name = "string",
                    type = functionExternalType(
                        functionType = functionType(
                            results = resultType(
                                listOf(
                                    i32ValueType(),
                                    i32ValueType(),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        )

        val factory = WasmInterfaceFactory()
        val actual = factory(
            config = config,
            info = info,
            logger = LOGGER,
        )

        val expected = wasmInterface(
            functions = listOf(
                function(
                    name = "string",
                    returns = functionReturn(
                        stringScalarType(),
                    ),
                    implementation = functionProxy(
                        name = "string",
                    ),
                ),
            ),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `generates a new type when return type qualifies as string but when config value is set to false`() {

        val config = codegenConfig(
            transformStrings = false,
        )
        val info = moduleInfo(
            exports = listOf(
                exportDefinition(
                    name = "string",
                    type = functionExternalType(
                        functionType = functionType(
                            results = resultType(
                                listOf(
                                    i32ValueType(),
                                    i32ValueType(),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        )

        val factory = WasmInterfaceFactory()
        val actual = factory(
            config = config,
            info = info,
            logger = LOGGER,
        )

        val type = generatedType(
            name = "StringResult",
            fields = listOf(
                field(
                    name = "r0",
                    type = integerScalarType(),
                ),
                field(
                    name = "r1",
                    type = integerScalarType(),
                ),
            ),
        )

        val expected = wasmInterface(
            types = listOf(
                type,
            ),
            functions = listOf(
                function(
                    name = "string",
                    returns = functionReturn(
                        aggregateType(generated = type),
                    ),
                    implementation = functionProxy(
                        name = "string",
                    ),
                ),
            ),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `can generate a new type when the return type has multiple values`() {

        val config = codegenConfig()
        val info = moduleInfo(
            exports = listOf(
                exportDefinition(
                    name = "multiple_return",
                    type = functionExternalType(
                        functionType = functionType(
                            results = resultType(
                                listOf(
                                    i32ValueType(),
                                    i64ValueType(),
                                    f32ValueType(),
                                    f64ValueType(),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        )

        val factory = WasmInterfaceFactory()
        val actual = factory(
            config = config,
            info = info,
            logger = LOGGER,
        )

        val type = generatedType(
            name = "MultipleReturnResult",
            fields = listOf(
                field(
                    name = "r0",
                    type = integerScalarType(),
                ),
                field(
                    name = "r1",
                    type = longScalarType(),
                ),
                field(
                    name = "r2",
                    type = floatScalarType(),
                ),
                field(
                    name = "r3",
                    type = doubleScalarType(),
                ),
            ),
        )

        val expected = wasmInterface(
            types = listOf(
                type,
            ),
            functions = listOf(
                function(
                    name = "multipleReturn",
                    returns = functionReturn(
                        aggregateType(
                            generated = type,
                        ),
                    ),
                    implementation = functionProxy(
                        name = "multiple_return",
                    ),
                ),
            ),
        )
        assertEquals(expected, actual)
    }

    private companion object {
        val LOGGER: (String) -> Unit = {
            fail("Logger should never be called but was with message: $it")
        }
    }
}
