package io.github.charlietap.chasm.bindgen

import io.github.charlietap.chasm.bindgen.fixture.FakeLogger
import io.github.charlietap.chasm.bindgen.fixture.codegenConfig
import io.github.charlietap.chasm.embedding.shapes.ModuleInfo
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.runtime.type.ExternalType
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class GcCodegenTest {

    @Test
    fun `generates loaded compiled struct views`() {
        assertModuleGenerates(
            category = "gc",
            module = "struct_views",
            options = BindingOptions(
                interfaceName = "LoadedStructViews",
                packageName = PACKAGE_NAME,
                config = LOADED_CONFIG,
            ),
        )
    }

    @Test
    fun `skipped functions leave no struct views behind`() {

        val logger = FakeLogger()

        assertModuleGenerates(
            category = "gc",
            module = "skipped_views",
            options = BindingOptions(
                interfaceName = "LoadedSkippedViews",
                packageName = PACKAGE_NAME,
                config = LOADED_CONFIG,
            ),
            logger = logger,
        )

        logger.expectErrorLogCount(2)
    }

    @Test
    fun `generates interpreter struct views`() {
        assertModuleGenerates(
            category = "gc",
            module = "struct_views",
            options = BindingOptions(
                interfaceName = "StructViews",
                packageName = PACKAGE_NAME,
                config = CodegenConfig(runtime = CodegenRuntime.CHASM, executionTier = ExecutionTier.Interpreter()),
            ),
        )
    }

    @Test
    fun `generates compiled struct views`() {
        assertModuleGenerates(
            category = "gc",
            module = "struct_views",
            options = BindingOptions(
                interfaceName = "CompiledStructViews",
                packageName = PACKAGE_NAME,
                config = GENERATED_CONFIG,
            ),
        )
    }

    @Test
    fun `generates compiled struct views over inherited and spilled lanes`() {
        assertModuleGenerates(
            category = "gc",
            module = "struct_storage",
            options = BindingOptions(
                interfaceName = "CompiledStructStorage",
                packageName = PACKAGE_NAME,
                config = GENERATED_CONFIG,
            ),
        )
    }

    @Test
    fun `struct references skip on the portable VM`() {

        val info = moduleInfo(moduleBytes("struct_views")).expect("struct_views should decode")
        val logger = FakeLogger()

        val files = BindingGenerator(logger).generate(
            info,
            BindingOptions(
                interfaceName = "Views",
                packageName = PACKAGE_NAME,
                config = codegenConfig(runtime = CodegenRuntime.PORTABLE_VM),
            ),
        )

        logger.expectErrorLogCount(10)
        assertFalse(files.first().toString().contains("StructReference"))
    }

    @Test
    fun `generates compiled strings over GC arrays`() {
        assertModuleGenerates(
            category = "gc",
            module = "gc_strings",
            options = BindingOptions(
                interfaceName = "CompiledGcStrings",
                packageName = PACKAGE_NAME,
                config = GENERATED_CONFIG,
                functions = gcStringFunctions(),
                ignoredExports = setOf("nullable_result"),
            ),
        )
    }

    @Test
    fun `strings over GC arrays require a managed tier`() {

        val info = moduleInfo(moduleBytes("gc_strings")).expect("gc_strings should decode")
        val config = codegenConfig(runtime = CodegenRuntime.CHASM)

        assertFailsWith<IllegalArgumentException> {
            BindingGenerator().generate(
                info,
                BindingOptions(
                    interfaceName = "Strings",
                    packageName = PACKAGE_NAME,
                    config = config,
                    functions = gcStringFunctions(),
                ),
            )
        }
    }

    @Test
    fun `strings over GC arrays cannot be freed after the call`() {

        val info = moduleInfo(moduleBytes("gc_strings")).expect("gc_strings should decode")
        val function = WasmFunction(
            name = "echo_utf16",
            parameters = listOf(
                FunctionParameterDefinition(
                    name = "value",
                    type = Scalar.String,
                    stringEncodingStrategy = StringEncodingStrategy.GC_UTF16_ARRAY,
                    stringAllocationStrategy = StringAllocationStrategy(freeAfterCall = true),
                ),
            ),
            returnType = ReturnTypeDefinition(Scalar.String, StringEncodingStrategy.GC_UTF16_ARRAY),
        )

        assertFailsWith<IllegalArgumentException> {
            BindingGenerator().generate(
                info,
                BindingOptions(
                    interfaceName = "Strings",
                    packageName = PACKAGE_NAME,
                    config = LOADED_CONFIG,
                    functions = listOf(function),
                ),
            )
        }
    }

    @Test
    fun `strings over GC arrays must match the array element type`() {

        val info = moduleInfo(moduleBytes("gc_strings")).expect("gc_strings should decode")
        val logger = FakeLogger()
        val function = WasmFunction(
            name = "echo_utf16",
            parameters = listOf(
                FunctionParameterDefinition("value", Scalar.String, StringEncodingStrategy.GC_UTF8_ARRAY),
            ),
            returnType = ReturnTypeDefinition(Scalar.String, StringEncodingStrategy.GC_UTF16_ARRAY),
        )

        BindingGenerator(logger).generate(
            info,
            BindingOptions(
                interfaceName = "Strings",
                packageName = PACKAGE_NAME,
                config = LOADED_CONFIG,
                functions = listOf(function),
                ignoredExports = GC_STRING_EXPORTS - "echo_utf16",
            ),
        )

        logger.expectExactlyOneErrorLog(
            "Failed to generate function echo_utf16 because Function definition defines parameters " +
                "[(ref (array I8))] but actual wasm function expects ${wasmParameters(info, "echo_utf16")}",
        )
    }

    @Test
    fun `strings over GC arrays must be non-null results`() {

        val info = moduleInfo(moduleBytes("gc_strings")).expect("gc_strings should decode")
        val logger = FakeLogger()
        val function = WasmFunction(
            name = "nullable_result",
            parameters = listOf(
                FunctionParameterDefinition("value", Scalar.String, StringEncodingStrategy.GC_UTF16_ARRAY),
            ),
            returnType = ReturnTypeDefinition(Scalar.String, StringEncodingStrategy.GC_UTF16_ARRAY),
        )

        BindingGenerator(logger).generate(
            info,
            BindingOptions(
                interfaceName = "Strings",
                packageName = PACKAGE_NAME,
                config = LOADED_CONFIG,
                functions = listOf(function),
                ignoredExports = GC_STRING_EXPORTS - "nullable_result",
            ),
        )

        logger.expectExactlyOneErrorLog(
            "Failed to generate function nullable_result because Function definition returns a String from a GC array " +
                "but the wasm function returns a nullable reference; GC array string results must be non-null references",
        )
    }

    private fun wasmParameters(
        info: ModuleInfo,
        exportName: String,
    ): String {

        val export = info.exports.single { export -> export.name == exportName }
        val type = export.type as ExternalType.Function

        return type.functionType.params.types.toString()
    }

    private fun gcStringFunctions(): List<WasmFunction> {
        return listOf(
            gcStringEcho("echo_utf16", StringEncodingStrategy.GC_UTF16_ARRAY),
            gcStringEcho("echo_utf8", StringEncodingStrategy.GC_UTF8_ARRAY),
            gcStringEcho("frozen", StringEncodingStrategy.GC_UTF16_ARRAY),
            WasmFunction(
                name = "utf16_length",
                parameters = listOf(
                    FunctionParameterDefinition("offset", Scalar.Integer),
                    FunctionParameterDefinition("value", Scalar.String, StringEncodingStrategy.GC_UTF16_ARRAY),
                ),
                returnType = ReturnTypeDefinition(Scalar.Integer),
            ),
            WasmFunction(
                name = "nullable_utf16",
                parameters = listOf(
                    FunctionParameterDefinition("value", Scalar.String, StringEncodingStrategy.GC_UTF16_ARRAY),
                ),
                returnType = ReturnTypeDefinition(Scalar.Integer),
            ),
        )
    }

    private fun gcStringEcho(
        name: String,
        encoding: StringEncodingStrategy,
    ): WasmFunction {
        return WasmFunction(
            name = name,
            parameters = listOf(FunctionParameterDefinition("value", Scalar.String, encoding)),
            returnType = ReturnTypeDefinition(Scalar.String, encoding),
        )
    }

    private companion object {
        const val PACKAGE_NAME = "com.test"
        val LOADED_CONFIG = CodegenConfig(runtime = CodegenRuntime.CHASM, executionTier = ExecutionTier.Compiled(CompiledSource.Loaded()))
        val GENERATED_CONFIG = CodegenConfig(
            runtime = CodegenRuntime.CHASM,
            executionTier = ExecutionTier.Compiled(CompiledSource.Generated("com.test.aot.WasmModule")),
        )
        val GC_STRING_EXPORTS = setOf("echo_utf16", "echo_utf8", "utf16_length", "nullable_utf16", "frozen", "nullable_result")
    }
}
