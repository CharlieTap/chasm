package io.github.charlietap.chasm.bindgen

import io.github.charlietap.chasm.config.CpuLimits
import io.github.charlietap.chasm.config.Limits
import io.github.charlietap.chasm.config.MB
import io.github.charlietap.chasm.config.MemoryLimits
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.fuel
import io.github.charlietap.chasm.fixture.chasm.embedding.exportDefinition
import io.github.charlietap.chasm.fixture.chasm.embedding.importDefinition
import io.github.charlietap.chasm.fixture.chasm.embedding.moduleInfo
import io.github.charlietap.chasm.fixture.runtime.type.functionExternalType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

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
    fun `every execution tier generates the interface and its implementation`() {
        val tiers = listOf(ExecutionTier.Interpreter(), ExecutionTier.Jit(), ExecutionTier.Aot("com.example.aot.WasmModule"))

        tiers.forEach { tier ->
            val config = CodegenConfig(runtime = CodegenRuntime.CHASM, executionTier = tier)

            val files = BindingGenerator().generate(
                moduleInfo(),
                BindingOptions(interfaceName = "Service", packageName = "com.example", config = config),
            )

            assertEquals(listOf("Service", "ServiceImpl"), files.map { file -> file.name }, tier::class.simpleName)
        }
    }

    @Test
    fun `compiled tiers cannot link WASI imports automatically yet`() {

        val tiers = listOf(ExecutionTier.Jit(), ExecutionTier.Aot("com.example.aot.WasmModule"))
        val info = moduleInfo(imports = listOf(importDefinition(moduleName = "wasi_snapshot_preview1", entityName = "fd_write")))

        tiers.forEach { tier ->

            val config = CodegenConfig(runtime = CodegenRuntime.CHASM, wasi = WasiLinking.AUTOMATIC, executionTier = tier)

            assertFailsWith<IllegalArgumentException>(tier::class.simpleName) {
                BindingGenerator().generate(info, BindingOptions(interfaceName = "Service", packageName = "com.example", config = config))
            }
        }
    }

    @Test
    fun `compiled tiers generate with automatic WASI linking when the module imports no WASI`() {
        val tiers = listOf(ExecutionTier.Jit(), ExecutionTier.Aot("com.example.aot.WasmModule"))

        tiers.forEach { tier ->
            val config = CodegenConfig(runtime = CodegenRuntime.CHASM, wasi = WasiLinking.AUTOMATIC, executionTier = tier)

            val files = BindingGenerator().generate(
                moduleInfo(),
                BindingOptions(interfaceName = "Service", packageName = "com.example", config = config),
            )

            assertEquals(listOf("Service", "ServiceImpl"), files.map { file -> file.name }, tier::class.simpleName)
        }
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

    @Test
    fun `configurations no tier can serve fail generation`() {
        val rejected = listOf(
            CodegenConfig(runtime = CodegenRuntime.PORTABLE_VM, executionTier = ExecutionTier.Jit()),
            CodegenConfig(runtime = CodegenRuntime.PORTABLE_VM, executionTier = ExecutionTier.Aot("com.example.aot.WasmModule")),
            CodegenConfig(runtime = CodegenRuntime.PORTABLE_VM, runtimeConfig = RuntimeConfig(limits = Limits(cpu = CpuLimits.Metered(10.fuel)))),
            CodegenConfig(runtime = CodegenRuntime.PORTABLE_VM, runtimeConfig = RuntimeConfig(limits = Limits(memory = MemoryLimits(linearMemory = 1.MB)))),
            CodegenConfig(runtime = CodegenRuntime.PORTABLE_VM, runtimeConfig = RuntimeConfig(interrupts = true)),
            CodegenConfig(runtime = CodegenRuntime.PORTABLE_VM, runtimeConfig = RuntimeConfig(stacktraces = true)),
            CodegenConfig(runtime = CodegenRuntime.CHASM, executionTier = ExecutionTier.Aot("com.example.Outer\$Inner")),
            CodegenConfig(
                runtime = CodegenRuntime.CHASM,
                executionTier = ExecutionTier.Aot("com.example.aot.WasmModule"),
                runtimeConfig = RuntimeConfig(limits = Limits(memory = MemoryLimits(linearMemory = 1.MB))),
            ),
            CodegenConfig(
                runtime = CodegenRuntime.CHASM,
                executionTier = ExecutionTier.Aot("com.example.aot.WasmModule"),
                runtimeConfig = RuntimeConfig(stacktraces = true),
            ),
        )

        rejected.forEach { config ->
            assertFailsWith<IllegalArgumentException>(config.toString()) {
                BindingGenerator().generate(moduleInfo(), BindingOptions(interfaceName = "Service", packageName = "com.example", config = config))
            }
        }
    }

    @Test
    fun `an export named like a store control fails generation`() {
        val config = CodegenConfig(runtime = CodegenRuntime.CHASM, runtimeConfig = RuntimeConfig(interrupts = true))
        val info = moduleInfo(exports = listOf(exportDefinition(name = "interrupt", type = functionExternalType())))

        val failure = assertFailsWith<IllegalArgumentException> {
            BindingGenerator().generate(info, BindingOptions(interfaceName = "Service", packageName = "com.example", config = config))
        }

        assertTrue("interrupt" in failure.message.orEmpty(), failure.message)
    }

    @Test
    fun `an export named like a store control is fine when that control is off`() {
        val config = CodegenConfig(runtime = CodegenRuntime.CHASM, runtimeConfig = RuntimeConfig(limits = Limits(cpu = CpuLimits.Metered(10.fuel))))
        val info = moduleInfo(exports = listOf(exportDefinition(name = "interrupt", type = functionExternalType())))

        val files = BindingGenerator().generate(info, BindingOptions(interfaceName = "Service", packageName = "com.example", config = config))

        assertEquals(listOf("Service", "ServiceImpl"), files.map { file -> file.name })
    }
}
