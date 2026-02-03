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
import io.github.charlietap.chasm.gradle.StringEncodingStrategy
import io.github.charlietap.chasm.gradle.fixture.FakeLogger
import io.github.charlietap.chasm.gradle.fixture.NeverLogger
import io.github.charlietap.chasm.gradle.fixture.aggregateType
import io.github.charlietap.chasm.gradle.fixture.codegenConfig
import io.github.charlietap.chasm.gradle.fixture.doubleScalarType
import io.github.charlietap.chasm.gradle.fixture.field
import io.github.charlietap.chasm.gradle.fixture.floatScalarType
import io.github.charlietap.chasm.gradle.fixture.function
import io.github.charlietap.chasm.gradle.fixture.functionImplementation
import io.github.charlietap.chasm.gradle.fixture.functionParameter
import io.github.charlietap.chasm.gradle.fixture.functionParameterDefinition
import io.github.charlietap.chasm.gradle.fixture.functionProxy
import io.github.charlietap.chasm.gradle.fixture.functionReturn
import io.github.charlietap.chasm.gradle.fixture.generatedType
import io.github.charlietap.chasm.gradle.fixture.integerScalarType
import io.github.charlietap.chasm.gradle.fixture.longScalarType
import io.github.charlietap.chasm.gradle.fixture.returnTypeDefinition
import io.github.charlietap.chasm.gradle.fixture.stringScalarType
import io.github.charlietap.chasm.gradle.fixture.unitScalarType
import io.github.charlietap.chasm.gradle.fixture.wasmFunction
import io.github.charlietap.chasm.gradle.fixture.wasmInterface
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class WasmInterfaceFactoryTest {

    @Test
    fun `can generate a new type when the return type has multiple values`() {

        val packageName = "package name"
        val interfaceName = "interface name"
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
        val initializers = setOf(
            "foo",
            "bar",
        )

        val factory = WasmInterfaceFactory()
        val actual = factory(
            interfaceName = interfaceName,
            packageName = packageName,
            config = config,
            info = info,
            allocator = null,
            initializers = initializers,
            wasmFunctions = emptyList(),
            ignoredExports = emptySet(),
            logger = NeverLogger,
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
            interfaceName = interfaceName,
            packageName = packageName,
            initializers = initializers,
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

    @Test
    fun `excludes allocator exports from interface`() {

        val packageName = "package name"
        val interfaceName = "interface name"
        val config = codegenConfig()

        val info = moduleInfo(
            exports = listOf(
                exportDefinition(
                    name = "_malloc",
                    type = functionExternalType(
                        functionType = functionType(
                            params = resultType(listOf(i32ValueType())),
                            results = resultType(listOf(i32ValueType())),
                        ),
                    ),
                ),
                exportDefinition(
                    name = "_free",
                    type = functionExternalType(
                        functionType = functionType(
                            params = resultType(listOf(i32ValueType())),
                            results = resultType(emptyList()),
                        ),
                    ),
                ),
                exportDefinition(
                    name = "regular_export",
                    type = functionExternalType(),
                ),
            ),
        )

        val allocator = ExportedAllocator(
            allocationFunction = "_malloc",
            deallocationFunction = "_free",
        )

        val factory = WasmInterfaceFactory()
        val actual = factory(
            interfaceName = interfaceName,
            packageName = packageName,
            config = config,
            info = info,
            allocator = allocator,
            initializers = emptySet(),
            wasmFunctions = emptyList(),
            ignoredExports = emptySet(),
            logger = NeverLogger,
        )

        val expected = wasmInterface(
            interfaceName = interfaceName,
            packageName = packageName,
            initializers = emptySet(),
            types = emptyList(),
            functions = listOf(
                function(
                    name = "regularExport",
                    implementation = functionProxy(
                        name = "regular_export",
                    ),
                ),
            ),
            allocator = allocator,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can generate a function from a given wasm function spec`() {

        val packageName = "package name"
        val interfaceName = "interface name"
        val config = codegenConfig()
        val info = moduleInfo(
            exports = listOf(
                exportDefinition(
                    name = "foo",
                    type = functionExternalType(
                        functionType = functionType(
                            params = resultType(
                                listOf(
                                    i32ValueType(),
                                    i32ValueType(),
                                ),
                            ),
                            results = resultType(
                                listOf(
                                    i32ValueType(),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        )

        val function = wasmFunction(
            name = "foo",
            parameters = listOf(
                functionParameterDefinition(
                    name = "a",
                    type = integerScalarType(),
                ),
                functionParameterDefinition(
                    name = "b",
                    type = integerScalarType(),
                ),
            ),
            returnType = returnTypeDefinition(
                type = integerScalarType(),
            ),
        )

        val factory = WasmInterfaceFactory()
        val actual = factory(
            interfaceName = interfaceName,
            packageName = packageName,
            config = config,
            info = info,
            allocator = null,
            initializers = emptySet(),
            wasmFunctions = listOf(function),
            ignoredExports = emptySet(),
            logger = NeverLogger,
        )

        val expected = wasmInterface(
            interfaceName = interfaceName,
            packageName = packageName,
            initializers = emptySet(),
            types = emptyList(),
            functions = listOf(
                function(
                    name = "foo",
                    params = listOf(
                        functionParameter(
                            name = "a",
                            type = integerScalarType(),
                        ),
                        functionParameter(
                            name = "b",
                            type = integerScalarType(),
                        ),
                    ),
                    returns = functionReturn(
                        integerScalarType(),
                    ),
                    implementation = functionProxy(
                        name = "foo",
                    ),
                ),
            ),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `can ignore an export`() {

        val packageName = "package name"
        val interfaceName = "interface name"
        val config = codegenConfig()
        val info = moduleInfo(
            exports = listOf(
                exportDefinition(
                    name = "not_ignored",
                    type = functionExternalType(),
                ),
                exportDefinition(
                    name = "ignored",
                    type = functionExternalType(),
                ),
            ),
        )

        val factory = WasmInterfaceFactory()
        val actual = factory(
            interfaceName = interfaceName,
            packageName = packageName,
            config = config,
            info = info,
            allocator = null,
            initializers = emptySet(),
            wasmFunctions = emptyList(),
            ignoredExports = setOf("ignored"),
            logger = NeverLogger,
        )

        val expected = wasmInterface(
            interfaceName = interfaceName,
            packageName = packageName,
            initializers = emptySet(),
            types = emptyList(),
            functions = listOf(
                function(
                    name = "notIgnored",
                    implementation = functionProxy(
                        name = "not_ignored",
                    ),
                ),
            ),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `logs error and excludes function when string param spec mismatches wasm signature`() {

        val packageName = "package name"
        val interfaceName = "interface name"
        val config = codegenConfig()
        val info = moduleInfo(
            exports = listOf(
                exportDefinition(
                    name = "foo",
                ),
            ),
        )

        val function = wasmFunction(
            name = "foo",
            parameters = listOf(
                functionParameterDefinition(
                    name = "s",
                    type = stringScalarType(),
                    stringEncodingStrategy = StringEncodingStrategy.POINTER_AND_LENGTH,
                ),
            ),
            returnType = returnTypeDefinition(
                type = unitScalarType(),
            ),
        )

        val logger = FakeLogger()

        val factory = WasmInterfaceFactory()
        val actual = factory(
            interfaceName = interfaceName,
            packageName = packageName,
            config = config,
            info = info,
            allocator = null,
            initializers = emptySet(),
            wasmFunctions = listOf(function),
            ignoredExports = emptySet(),
            logger = logger,
        )

        val expected = wasmInterface(
            interfaceName = interfaceName,
            packageName = packageName,
            initializers = emptySet(),
            types = emptyList(),
            functions = emptyList(),
        )
        assertEquals(expected, actual)

        logger.expectExactlyOneErrorLog(
            "Failed to generate function foo because Function definition defines parameters [Number(numberType=I32), Number(numberType=I32)] but actual wasm function expects []",
        )
    }
}
