package io.github.charlietap.chasm.compiler.backend.analysis.gc

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class RttKeysTest {

    private val keys = RttKeys(module("rtt_keys").definedTypes)

    @Test
    fun `structurally equal types in different rec group positions have different keys`() {
        assertNotEquals(keys[FIRST], keys[SECOND])
    }

    @Test
    fun `a type and its copy in another module share a key`() {

        val copy = RttKeys(module("rtt_keys_copy").definedTypes)

        assertEquals(keys[LEAF], copy[COPIED_LEAF])
        assertEquals(keys[FIRST], copy[COPIED_FIRST])
        assertEquals(keys[SECOND], copy[COPIED_SECOND])
    }

    private fun module(name: String): Module {

        val bytes = checkNotNull(javaClass.classLoader.getResourceAsStream("fixtures/$name.wasm")) { "missing fixture $name" }
            .use { stream -> stream.readBytes() }
        val config = ModuleConfig()
        val module = WasmModuleDecoder(config, bytes).getOrThrow { error -> IllegalStateException(error.toString()) }

        return WasmModuleValidator(config, module).getOrThrow { error -> IllegalStateException(error.toString()) }
    }

    private companion object {
        const val LEAF = 0
        const val FIRST = 1
        const val SECOND = 2
        const val COPIED_LEAF = 1
        const val COPIED_FIRST = 2
        const val COPIED_SECOND = 3
    }
}
