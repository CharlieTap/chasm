package io.github.charlietap.chasm.bindgen

import io.github.charlietap.chasm.fixture.chasm.embedding.importDefinition
import io.github.charlietap.chasm.fixture.chasm.embedding.moduleInfo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BindingGeneratorTest {

    @Test
    fun `generates the interface and its implementation`() {
        val files = BindingGenerator().generate(
            moduleInfo(),
            BindingOptions(interfaceName = "Service", packageName = "com.example"),
        )

        assertEquals(listOf("Service", "ServiceImpl"), files.map { file -> file.name })
    }

    @Test
    fun `compiled code replaces the implementation with a compiled one`() {
        val config = CodegenConfig(runtime = CodegenRuntime.CHASM, compiledCode = "com.example.compiled.WasmModule")

        val files = BindingGenerator().generate(
            moduleInfo(),
            BindingOptions(interfaceName = "Service", packageName = "com.example", config = config),
        )

        assertEquals(listOf("Service", "ServiceCompiled"), files.map { file -> file.name })
    }

    @Test
    fun `compiled code cannot link WASI imports automatically yet`() {
        val config = CodegenConfig(runtime = CodegenRuntime.CHASM, wasi = WasiLinking.AUTOMATIC, compiledCode = "com.example.compiled.WasmModule")
        val info = moduleInfo(imports = listOf(importDefinition(moduleName = "wasi_snapshot_preview1", entityName = "fd_write")))

        assertFailsWith<IllegalArgumentException> {
            BindingGenerator().generate(info, BindingOptions(interfaceName = "Service", packageName = "com.example", config = config))
        }
    }

    @Test
    fun `compiled code generates with automatic WASI linking when the module imports no WASI`() {
        val config = CodegenConfig(runtime = CodegenRuntime.CHASM, wasi = WasiLinking.AUTOMATIC, compiledCode = "com.example.compiled.WasmModule")

        val files = BindingGenerator().generate(
            moduleInfo(),
            BindingOptions(interfaceName = "Service", packageName = "com.example", config = config),
        )

        assertEquals(listOf("Service", "ServiceCompiled"), files.map { file -> file.name })
    }

    @Test
    fun `automatic WASI linking requires the Chasm runtime`() {
        val options = BindingOptions(
            interfaceName = "Service",
            packageName = "com.example",
            config = CodegenConfig(runtime = CodegenRuntime.PORTABLE_VM, wasi = WasiLinking.AUTOMATIC),
        )

        assertFailsWith<IllegalArgumentException> {
            BindingGenerator().generate(moduleInfo(), options)
        }
    }
}
