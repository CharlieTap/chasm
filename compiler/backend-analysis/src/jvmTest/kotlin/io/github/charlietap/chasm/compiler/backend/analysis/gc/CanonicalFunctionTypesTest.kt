package io.github.charlietap.chasm.compiler.backend.analysis.gc

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ext.functionType
import io.github.charlietap.chasm.validator.WasmModuleValidator
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CanonicalFunctionTypesTest {

    @Test
    fun `function types have equal keys exactly when they are structurally equal`() {

        val fixtures = checkNotNull(File(checkNotNull(javaClass.classLoader.getResource("fixtures")).toURI()).listFiles())
            .filter { file -> file.extension == "wasm" }

        fixtures.forEach { file ->

            val module = module(file.readBytes())
            val keys = CanonicalFunctionTypes(RttKeys(module.definedTypes))
            val types = functionTypes(module)

            types.forEach { first ->
                types.forEach { second ->
                    assertEquals(first == second, keys.key(first) == keys.key(second), file.name)
                }
            }
        }
    }

    @Test
    fun `references to structurally equal types declared apart give equal keys`() {

        val module = module(fixture("canonical_function_types"))
        val keys = CanonicalFunctionTypes(RttKeys(module.definedTypes))

        assertEquals(keys.key(functionType(module, TAKES_FIRST)), keys.key(functionType(module, TAKES_SECOND)))
        assertEquals(keys.key(functionType(module, RETURNS_FIRST)), keys.key(functionType(module, RETURNS_SECOND)))
    }

    @Test
    fun `references that differ in nullability or target give different keys`() {

        val module = module(fixture("canonical_function_types"))
        val keys = CanonicalFunctionTypes(RttKeys(module.definedTypes))

        assertNotEquals(keys.key(functionType(module, TAKES_FIRST)), keys.key(functionType(module, TAKES_NULLABLE_FIRST)))
        assertNotEquals(keys.key(functionType(module, TAKES_FIRST)), keys.key(functionType(module, TAKES_OTHER)))
    }

    private fun functionTypes(module: Module): List<FunctionType> {
        return module.definedTypes.mapNotNull { definedType -> definedType.functionType() }
    }

    private fun functionType(
        module: Module,
        index: Int,
    ): FunctionType {
        return checkNotNull(module.definedTypes[index].functionType())
    }

    private fun fixture(name: String): ByteArray {
        return checkNotNull(javaClass.classLoader.getResourceAsStream("fixtures/$name.wasm")) { "missing fixture $name" }
            .use { stream -> stream.readBytes() }
    }

    private fun module(bytes: ByteArray): Module {

        val config = ModuleConfig()
        val module = WasmModuleDecoder(config, bytes).getOrThrow { error -> IllegalStateException(error.toString()) }

        return WasmModuleValidator(config, module).getOrThrow { error -> IllegalStateException(error.toString()) }
    }

    private companion object {
        const val TAKES_FIRST = 3
        const val TAKES_SECOND = 4
        const val TAKES_NULLABLE_FIRST = 5
        const val TAKES_OTHER = 6
        const val RETURNS_FIRST = 7
        const val RETURNS_SECOND = 8
    }
}
