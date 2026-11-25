package io.github.charlietap.chasm.bindgen

import io.github.charlietap.chasm.bindgen.fixture.codegenConfig
import io.github.charlietap.chasm.bindgen.fixture.function
import io.github.charlietap.chasm.bindgen.fixture.functionProxy
import io.github.charlietap.chasm.bindgen.fixture.functionReturn
import io.github.charlietap.chasm.bindgen.fixture.globalProxy
import io.github.charlietap.chasm.bindgen.fixture.integerScalarType
import io.github.charlietap.chasm.bindgen.fixture.memoryBinding
import io.github.charlietap.chasm.bindgen.fixture.property
import io.github.charlietap.chasm.bindgen.fixture.stringScalarType
import io.github.charlietap.chasm.bindgen.fixture.wasmInterface
import io.github.charlietap.chasm.config.CpuLimits
import io.github.charlietap.chasm.config.Limits
import io.github.charlietap.chasm.config.MB
import io.github.charlietap.chasm.config.MemoryLimits
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.fuel
import io.github.charlietap.chasm.fixture.type.i32ValueType
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFailsWith

class ConstructionCodegenTest {

    @Test
    fun `generates synchronous portable VM construction`() = assertConstruction(
        scenario = "SynchronousConstruction",
        runtimes = listOf(CodegenRuntime.PORTABLE_VM),
    )

    @Test
    fun `generates suspending portable VM construction from codegen config`() = assertConstruction(
        scenario = "SuspendingConstruction",
        config = codegenConfig(generateSuspendingFactories = true),
        runtimes = listOf(CodegenRuntime.PORTABLE_VM),
    )

    @Test
    fun `generates construction`() = assertConstruction(
        scenario = "Construction",
        runtimes = listOf(CodegenRuntime.CHASM),
    )

    @Test
    fun `generates WASI Preview 1 construction`() = assertConstruction(
        scenario = "WasiPreview1Construction",
        linksWasiPreview1 = true,
        runtimes = listOf(CodegenRuntime.CHASM),
    )

    @Test
    fun `generates construction from a loaded compiled module`() = assertConstruction(
        scenario = "LoadedConstruction",
        config = codegenConfig(executionTier = ExecutionTier.Compiled(CompiledSource.Loaded())),
        runtimes = listOf(CodegenRuntime.CHASM),
    )

    @Test
    fun `generates construction from a generated compiled module`() = assertConstruction(
        scenario = "CompiledConstruction",
        config = codegenConfig(executionTier = ExecutionTier.Compiled(CompiledSource.Generated("com.test.aot.WasmModule"))),
        runtimes = listOf(CodegenRuntime.CHASM),
    )

    @Test
    fun `generates compiled construction with store controls`() = assertConstruction(
        scenario = "CompiledStoreControlsConstruction",
        config = codegenConfig(
            executionTier = ExecutionTier.Compiled(CompiledSource.Generated("com.test.aot.WasmModule")),
            runtimeConfig = RuntimeConfig(interrupts = true, limits = Limits(cpu = CpuLimits.Metered(1_000.fuel))),
        ),
        runtimes = listOf(CodegenRuntime.CHASM),
    )

    @Test
    fun `generates compiled construction from a compiled module bridge object`() = assertConstruction(
        scenario = "CompiledBridgeConstruction",
        config = codegenConfig(executionTier = ExecutionTier.Compiled(CompiledSource.Generated("com.test.CompiledBridgeConstructionCompiledModule"))),
        runtimes = listOf(CodegenRuntime.CHASM),
    )

    @Test
    fun `generates construction with store settings`() = assertConstruction(
        scenario = "StoreSettingsConstruction",
        config = codegenConfig(
            runtimeConfig = RuntimeConfig(
                interrupts = true,
                stacktraces = true,
                limits = Limits(
                    cpu = CpuLimits.Metered(1_000.fuel),
                    memory = MemoryLimits(linearMemory = 1.MB, gcHeap = 1.MB),
                ),
            ),
        ),
        runtimes = listOf(CodegenRuntime.CHASM),
    )

    @Test
    fun `generates initializers`() = assertGenerates(
        category = "construction",
        wasmInterface = wasmInterface(
            interfaceName = "Initializers",
            packageName = "com.test",
            initializers = linkedSetOf("initialize", "start"),
        ),
    )

    @Test
    fun `generates internal visibility`() = assertGenerates(
        category = "construction",
        wasmInterface = wasmInterface(
            interfaceName = "InternalVisibility",
            packageName = "com.test",
        ),
        interfaceVisibility = InterfaceVisibility.INTERNAL,
        factoryVisibility = FactoryVisibility.INTERNAL,
        implementationVisibility = ImplementationVisibility.INTERNAL,
    )

    @Test
    fun `generates suspending portable VM internal visibility`() = assertConstruction(
        scenario = "SuspendingInternalVisibility",
        interfaceVisibility = InterfaceVisibility.INTERNAL,
        factoryVisibility = FactoryVisibility.INTERNAL,
        implementationVisibility = ImplementationVisibility.INTERNAL,
        config = codegenConfig(generateSuspendingFactories = true),
        runtimes = listOf(CodegenRuntime.PORTABLE_VM),
    )

    @Test
    fun `generates non-empty suspending portable VM service`() = assertGenerates(
        category = "construction",
        wasmInterface = wasmInterface(
            interfaceName = "SuspendingService",
            packageName = "com.test",
            initializers = linkedSetOf("initialize", "start"),
            functions = listOf(
                function(
                    name = "readString",
                    returns = functionReturn(
                        type = stringScalarType(),
                        stringEncodingStrategy = StringEncodingStrategy.NULL_TERMINATED,
                    ),
                    resultTypes = listOf(i32ValueType()),
                    implementation = functionProxy("read_string"),
                ),
            ),
            properties = listOf(
                property(
                    name = "counter",
                    type = integerScalarType(),
                    const = false,
                    implementation = globalProxy("counter", VALUE_I32_CLASS_NAME),
                ),
            ),
            memories = listOf(memoryBinding("memory", "memory")),
        ),
        config = codegenConfig(generateSuspendingFactories = true),
        runtimes = listOf(CodegenRuntime.PORTABLE_VM),
    )

    @Test
    fun `rejects public factory for internal interface`() {
        val error = assertFailsWith<IllegalStateException> {
            WasmInterfaceGenerator()(
                interfaceVisibility = InterfaceVisibility.INTERNAL,
                factoryVisibility = FactoryVisibility.PUBLIC,
                wasmInterface = wasmInterface(interfaceName = "InternalService"),
            )
        }

        assertContains(error.message.orEmpty(), "public factory for internal interface InternalService")
    }

    @Test
    fun `implementation visibility is independent of factory visibility in both construction modes`() {
        listOf(false, true).forEach { suspending ->
            ImplementationVisibility.entries.forEach { visibility ->
                FactoryVisibility.entries.forEach { factoryVisibility ->
                    CodegenRuntime.entries.forEach { runtime ->
                        val files = WasmInterfaceGenerator()(
                            interfaceVisibility = InterfaceVisibility.PUBLIC,
                            implementationVisibility = visibility,
                            factoryVisibility = factoryVisibility,
                            wasmInterface = wasmInterface(interfaceName = "Service"),
                            config = codegenConfig(generateSuspendingFactories = suspending)
                                .copy(runtime = runtime),
                        )
                        val implementation = files.last().toString()
                        val suspendModifier = if (suspending || runtime == CodegenRuntime.CHASM) "suspend " else ""
                        assertContains(implementation, "${visibility.name.lowercase()} class ServiceImpl(")
                        assertContains(implementation, "${factoryVisibility.name.lowercase()} ${suspendModifier}fun service(")
                    }
                }
            }
        }
    }

    @Test
    fun `rejects public implementation for internal interface`() {
        val error = assertFailsWith<IllegalStateException> {
            WasmInterfaceGenerator()(
                interfaceVisibility = InterfaceVisibility.INTERNAL,
                implementationVisibility = ImplementationVisibility.PUBLIC,
                factoryVisibility = FactoryVisibility.INTERNAL,
                wasmInterface = wasmInterface(interfaceName = "InternalService"),
            )
        }

        assertContains(error.message.orEmpty(), "public implementation for internal interface InternalService")
    }

    private fun assertConstruction(
        scenario: String,
        interfaceVisibility: InterfaceVisibility = InterfaceVisibility.PUBLIC,
        factoryVisibility: FactoryVisibility = FactoryVisibility.PUBLIC,
        implementationVisibility: ImplementationVisibility = ImplementationVisibility.PRIVATE,
        config: CodegenConfig = codegenConfig(),
        linksWasiPreview1: Boolean = false,
        runtimes: Iterable<CodegenRuntime> = CodegenRuntime.entries,
    ) = assertGenerates(
        category = "construction",
        wasmInterface = wasmInterface(
            interfaceName = scenario,
            packageName = "com.test",
            linksWasiPreview1 = linksWasiPreview1,
        ),
        interfaceVisibility = interfaceVisibility,
        factoryVisibility = factoryVisibility,
        implementationVisibility = implementationVisibility,
        config = config,
        runtimes = runtimes,
    )
}
