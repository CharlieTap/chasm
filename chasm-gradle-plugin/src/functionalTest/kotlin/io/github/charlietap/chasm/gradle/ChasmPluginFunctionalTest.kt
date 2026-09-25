package io.github.charlietap.chasm.gradle

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.nio.file.Files
import java.nio.file.Path
import java.util.Comparator
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ChasmPluginFunctionalTest {

    @Test
    fun `plugin metadata declares compatibility without runtime dependencies`() {
        assertTrue(
            Files.isRegularFile(pluginModuleMetadata),
            "Missing Gradle module metadata at $pluginModuleMetadata",
        )
        val moduleContent = Files.readString(pluginModuleMetadata)
        assertContains(
            moduleContent,
            "\"org.gradle.plugin.api-version\": \"$minimumGradleVersion\"",
        )
        assertFalse(
            moduleContent.contains("\"dependencies\""),
            "Gradle module metadata must not publish codegen dependencies",
        )
        assertTrue(Files.isRegularFile(pluginPom), "Missing Maven POM at $pluginPom")
        assertFalse(
            Files.readString(pluginPom).contains("<dependencies>"),
            "The Gradle plugin must not publish codegen dependencies on its runtime classpath",
        )
    }

    @Test
    fun `plugin loads without Kotlin or Android on supported Gradle versions`() {
        testedGradleVersions.forEach { gradleVersion ->
            val project = project(
                build = """
                    plugins {
                        id("$pluginId")
                    }
                """,
            )

            project.build("help", gradleVersion = gradleVersion)
        }
    }

    @Test
    fun `JVM and multiplatform tasks are registered when Chasm is applied first`() {
        val jvmProject = project(
            build = """
                plugins {
                    id("$pluginId")
                    id("org.jetbrains.kotlin.jvm")
                }

                chasm {
                    modules.create("JvmService") {
                        packageName.set("test.chasm")
                    }
                    modules.create("OtherJvmService") {
                        packageName.set("test.chasm")
                    }
                }
            """,
        )
        val jvmResult = jvmProject.build("compileKotlin", "--configuration-cache")
        assertContains(jvmResult.output, "codegenModuleMainJvmService")
        assertContains(jvmResult.output, "codegenModuleMainOtherJvmService")
        jvmProject.assertGenerated("main", "JvmService")
        jvmProject.assertGenerated("main", "OtherJvmService")

        val multiplatformProject = project(
            build = """
                import io.github.charlietap.chasm.gradle.CodegenConfig

                plugins {
                    id("$pluginId")
                    id("org.jetbrains.kotlin.multiplatform")
                }

                kotlin {
                    jvm()
                }

                chasm {
                    modules.create("CommonService") {
                        packageName.set("test.chasm")
                        codegenConfig.set(CodegenConfig(generateSuspendingFactories = true))
                    }
                }
            """,
        )
        val multiplatformResult = multiplatformProject.build("codegenModuleCommonMainCommonService")
        assertContains(multiplatformResult.output, "codegenModuleCommonMainCommonService")
    }

    @Test
    fun `Chasm runtime rejects multiplatform web targets`() {
        listOf("js()", "wasmJs()").forEach { target ->
            val project = project(
                build = """
                    import io.github.charlietap.chasm.gradle.CodegenConfig
                    import io.github.charlietap.chasm.gradle.CodegenRuntime

                    plugins {
                        id("$pluginId")
                        id("org.jetbrains.kotlin.multiplatform")
                    }

                    chasm {
                        modules.create("DirectService") {
                            packageName.set("test.chasm")
                            codegenConfig.set(CodegenConfig(runtime = CodegenRuntime.CHASM))
                        }
                    }

                    kotlin {
                        jvm()
                        $target
                    }
                """,
            )

            val result = project.buildAndFail("codegenModuleCommonMainDirectService")

            assertContains(
                result.output,
                "CodegenRuntime.CHASM only supports Chasm's JVM, Android, and Kotlin/Native targets.",
            )
        }
    }

    @Test
    fun `portable runtime supports multiplatform web targets`() {
        val project = project(
            build = """
                plugins {
                    id("$pluginId")
                    id("org.jetbrains.kotlin.multiplatform")
                }

                chasm {
                    modules.create("PortableService") {
                        packageName.set("test.chasm")
                    }
                }

                kotlin {
                    js()
                    wasmJs()
                }
            """,
        )

        val result = project.build("codegenModuleCommonMainPortableService")

        assertContains(result.output, "codegenModuleCommonMainPortableService")
    }

    @Test
    fun `Chasm multiplatform runtime supports configuration cache and isolated projects`() {
        val project = project(
            build = """
                import io.github.charlietap.chasm.gradle.CodegenConfig
                import io.github.charlietap.chasm.gradle.CodegenRuntime

                plugins {
                    id("$pluginId")
                    id("org.jetbrains.kotlin.multiplatform")
                }

                kotlin {
                    jvm()
                }

                chasm {
                    modules.create("DirectService") {
                        packageName.set("test.chasm")
                        codegenConfig.set(CodegenConfig(runtime = CodegenRuntime.CHASM))
                    }
                }
            """,
        )
        val arguments = arrayOf(
            "codegenModuleCommonMainDirectService",
            "--configuration-cache",
            "--isolated-projects",
            "-Dorg.gradle.isolated-projects.diagnostics=true",
        )

        project.build(*arguments)
        val reused = project.build(*arguments)

        assertContains(reused.output, "Configuration cache entry reused.")
    }

    @Test
    fun `JVM runtime dependency selection remains lazy`() {
        RuntimeDependencyConfiguration.entries.forEach { selection ->
            val configurationName = selection.name.lowercase()
            listOf(
                "PORTABLE_VM" to "vm-jvm",
                "CHASM" to "chasm-jvm",
            ).forEach { (runtime, artifact) ->
                val project = project(
                    build = """
                        import io.github.charlietap.chasm.gradle.CodegenConfig
                        import io.github.charlietap.chasm.gradle.CodegenRuntime
                        import io.github.charlietap.chasm.gradle.RuntimeDependencyConfiguration

                        plugins {
                            id("$pluginId")
                            id("org.jetbrains.kotlin.jvm")
                        }

                        chasm {
                            runtimeDependencyConfiguration.set(RuntimeDependencyConfiguration.${selection.name})
                            modules.create("RuntimeService") {
                                packageName.set("test.chasm")
                                codegenConfig.set(CodegenConfig(runtime = CodegenRuntime.$runtime))
                            }
                        }
                    """,
                )

                val result = project.build("dependencies", "--configuration=$configurationName")
                assertContains(result.output, "io.github.charlietap.chasm:$artifact:")
                val otherArtifact = if (artifact == "vm-jvm") "chasm-jvm" else "vm-jvm"
                assertFalse(result.output.contains("io.github.charlietap.chasm:$otherArtifact:"))
            }
        }
    }

    @Test
    fun `multiplatform runtime selects the matching dependency`() {
        listOf(
            "PORTABLE_VM" to "vm",
            "CHASM" to "chasm",
        ).forEach { (runtime, artifact) ->
            val project = project(
                build = """
                    import io.github.charlietap.chasm.gradle.CodegenConfig
                    import io.github.charlietap.chasm.gradle.CodegenRuntime

                    plugins {
                        id("$pluginId")
                        id("org.jetbrains.kotlin.multiplatform")
                    }

                    kotlin {
                        jvm()
                    }

                    chasm {
                        modules.create("RuntimeService") {
                            packageName.set("test.chasm")
                            codegenConfig.set(CodegenConfig(runtime = CodegenRuntime.$runtime))
                        }
                    }
                """,
            )

            val result = project.build("dependencies", "--configuration=commonMainImplementation")
            assertContains(result.output, "io.github.charlietap.chasm:$artifact:")
            val otherArtifact = if (artifact == "vm") "chasm" else "vm"
            assertFalse(result.output.contains("io.github.charlietap.chasm:$otherArtifact:"))
        }
    }

    @Test
    fun `Chasm coroutine dependency follows suspending factory generation`() {
        listOf(false, true).forEach { suspending ->
            val project = project(
                build = """
                    import io.github.charlietap.chasm.gradle.CodegenConfig
                    import io.github.charlietap.chasm.gradle.CodegenRuntime

                    plugins {
                        id("$pluginId")
                        id("org.jetbrains.kotlin.jvm")
                    }

                    chasm {
                        modules.create("RuntimeService") {
                            packageName.set("test.chasm")
                            codegenConfig.set(
                                CodegenConfig(
                                    generateSuspendingFactories = $suspending,
                                    runtime = CodegenRuntime.CHASM,
                                ),
                            )
                        }
                    }
                """,
            )

            val result = project.build("dependencies", "--configuration=implementation")
            assertEquals(
                suspending,
                result.output.contains("io.github.charlietap.chasm:chasm-coroutines-jvm:"),
            )
        }
    }

    @Test
    fun `top-level factories construct and execute synchronous and suspending services`() {
        val project = project(
            build = """
                import io.github.charlietap.chasm.gradle.CodegenConfig
                import io.github.charlietap.chasm.gradle.FactoryVisibility
                import io.github.charlietap.chasm.gradle.InterfaceVisibility

                plugins {
                    id("$pluginId")
                    id("org.jetbrains.kotlin.jvm")
                    application
                }

                dependencies {
                    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                }

                application {
                    mainClass.set("test.consumer.Main")
                }

                chasm {
                    modules.create("SyncService") {
                        packageName.set("test.generated")
                    }
                    modules.create("RichService") {
                        binary.set(layout.projectDirectory.file("src/main/wasm/rich.wasm"))
                        packageName.set("test.generated")
                        interfaceVisibility.set(InterfaceVisibility.INTERNAL)
                        factoryVisibility.set(FactoryVisibility.INTERNAL)
                        initializers.set(linkedSetOf("initialize", "start", "finish"))
                        codegenConfig.set(
                            CodegenConfig(
                                generateTypesafeGlobalProperties = true,
                                generateTypesafeMemoryProperties = true,
                                generateSuspendingFactories = true,
                            ),
                        )
                    }
                }
            """,
            binary = ANSWER_WASM_MODULE,
        )
        project.writeBytes("src/main/wasm/rich.wasm", RICH_WASM_MODULE)
        project.writeBytes("src/main/resources/module.wasm", ANSWER_WASM_MODULE)
        project.writeBytes("src/main/resources/rich.wasm", RICH_WASM_MODULE)
        project.write(
            "src/main/kotlin/test/consumer/Main.kt",
            """
                package test.consumer

                import io.github.charlietap.chasm.vm.ExternalAddress
                import io.github.charlietap.chasm.vm.FunctionType
                import io.github.charlietap.chasm.vm.Global
                import io.github.charlietap.chasm.vm.HostFunction
                import io.github.charlietap.chasm.vm.Import
                import io.github.charlietap.chasm.vm.Instance
                import io.github.charlietap.chasm.vm.Memory
                import io.github.charlietap.chasm.vm.Module
                import io.github.charlietap.chasm.vm.NumberType
                import io.github.charlietap.chasm.vm.PreparedFunction
                import io.github.charlietap.chasm.vm.Store
                import io.github.charlietap.chasm.vm.SuspendingWasmVirtualMachine
                import io.github.charlietap.chasm.vm.ValueType
                import io.github.charlietap.chasm.vm.WasmVirtualMachine
                import io.github.charlietap.chasm.vm.`expect`
                import io.github.charlietap.chasm.vm.codegen.FunctionImport
                import io.github.charlietap.chasm.vm.suspendingVirtualMachineFactory
                import kotlinx.coroutines.delay
                import kotlinx.coroutines.runBlocking
                import test.generated.RichService
                import test.generated.SyncService
                import test.generated.richService
                import test.generated.syncService

                private class TrackingVirtualMachine(
                    private val delegate: SuspendingWasmVirtualMachine,
                ) : SuspendingWasmVirtualMachine, WasmVirtualMachine by delegate {
                    var storeInitCalls = 0
                    var moduleDecodeCalls = 0
                    var moduleInstantiateCalls = 0
                    var allocateFunctionCalls = 0
                    var prepareFunctionCalls = 0
                    var exportGlobalCalls = 0
                    var failDecode = false
                    var failInstantiate = false
                    var failInitializer: String? = null
                    val initializerInvocations = mutableListOf<String>()

                    lateinit var store: Store
                    lateinit var allocatedFunction: ExternalAddress.Function
                    lateinit var expectedInstance: Instance

                    override fun storeInit(): Store = delegate.storeInit().also {
                        storeInitCalls += 1
                        store = it
                    }

                    override suspend fun moduleDecodeSuspending(
                        binary: ByteArray,
                    ): WasmVirtualMachine.Result<Module> {
                        moduleDecodeCalls += 1
                        if (failDecode) {
                            return WasmVirtualMachine.Result.Error("deliberate decode failure")
                        }
                        return delegate.moduleDecodeSuspending(binary)
                    }

                    override suspend fun moduleInstantiateSuspending(
                        store: Store,
                        module: Module,
                        imports: List<Import>,
                    ): WasmVirtualMachine.Result<Instance> {
                        moduleInstantiateCalls += 1
                        check(store === this.store)
                        check(imports.single().address === allocatedFunction)
                        if (failInstantiate) {
                            return WasmVirtualMachine.Result.Error("deliberate instantiate failure")
                        }
                        return delegate.moduleInstantiateSuspending(store, module, imports).also { result ->
                            if (result is WasmVirtualMachine.Result.Ok) {
                                expectedInstance = result.value
                            }
                        }
                    }

                    override fun allocateFunction(
                        store: Store,
                        type: FunctionType,
                        function: HostFunction,
                    ): WasmVirtualMachine.Result<ExternalAddress.Function> {
                        allocateFunctionCalls += 1
                        check(store === this.store)
                        return delegate.allocateFunction(store, type, function).also { result ->
                            if (result is WasmVirtualMachine.Result.Ok) {
                                allocatedFunction = result.value
                            }
                        }
                    }

                    override fun prepareFunction(
                        store: Store,
                        instance: Instance,
                        functionName: String,
                        resultTypes: List<ValueType>,
                    ): WasmVirtualMachine.Result<PreparedFunction> {
                        prepareFunctionCalls += 1
                        checkRuntimeObjects(store, instance)
                        return delegate.prepareFunction(store, instance, functionName, resultTypes)
                    }

                    override fun functionInvokeTyped(
                        store: Store,
                        instance: Instance,
                        functionName: String,
                        args: List<WasmVirtualMachine.Value>,
                        resultTypes: List<ValueType>,
                    ): WasmVirtualMachine.Result<List<WasmVirtualMachine.Value>> {
                        checkRuntimeObjects(store, instance)
                        initializerInvocations += functionName
                        if (functionName == failInitializer) {
                            return WasmVirtualMachine.Result.Error("deliberate initializer failure")
                        }
                        return delegate.functionInvokeTyped(store, instance, functionName, args, resultTypes)
                    }

                    override fun exportGlobal(
                        instance: Instance,
                        name: String,
                    ): WasmVirtualMachine.Result<Global> {
                        exportGlobalCalls += 1
                        check(instance === expectedInstance)
                        return delegate.exportGlobal(instance, name)
                    }

                    override fun globalRead(
                        store: Store,
                        global: Global,
                    ): WasmVirtualMachine.Result<WasmVirtualMachine.Value> {
                        check(store === this.store)
                        return delegate.globalRead(store, global)
                    }

                    override fun globalWrite(
                        store: Store,
                        global: Global,
                        value: WasmVirtualMachine.Value,
                    ): WasmVirtualMachine.Result<Unit> {
                        check(store === this.store)
                        return delegate.globalWrite(store, global, value)
                    }

                    override fun exportMemory(
                        instance: Instance,
                        name: String,
                    ): WasmVirtualMachine.Result<Memory> {
                        check(instance === expectedInstance)
                        return delegate.exportMemory(instance, name)
                    }

                    override fun memoryReadBytes(
                        store: Store,
                        memory: Memory,
                        pointer: Int,
                        bytesToRead: Int,
                        buffer: ByteArray,
                        bufferPointer: Int,
                    ): WasmVirtualMachine.Result<ByteArray> {
                        check(store === this.store)
                        return delegate.memoryReadBytes(store, memory, pointer, bytesToRead, buffer, bufferPointer)
                    }

                    override fun memoryWriteBytes(
                        store: Store,
                        memory: Memory,
                        pointer: Int,
                        bytes: ByteArray,
                    ): WasmVirtualMachine.Result<Unit> {
                        check(store === this.store)
                        return delegate.memoryWriteBytes(store, memory, pointer, bytes)
                    }

                    fun recordInstance(instance: Instance): Instance = instance.also {
                        expectedInstance = it
                    }

                    private fun checkRuntimeObjects(store: Store, instance: Instance) {
                        check(store === this.store)
                        check(instance === expectedInstance)
                    }
                }

                private fun recordingImport(events: MutableList<Int>) = FunctionImport(
                    moduleName = "env",
                    entityName = "record",
                    type = FunctionType(
                        params = listOf(ValueType.Number(NumberType.I32)),
                        results = emptyList(),
                    ),
                    function = { values ->
                        events += (values.single() as WasmVirtualMachine.Value.I32).value
                        emptyList()
                    },
                )

                object Main {
                    @JvmStatic
                    fun main(args: Array<String>) = runBlocking {
                        val binary = requireNotNull(Main::class.java.getResourceAsStream("/module.wasm")).readBytes()

                        val syncService: SyncService = syncService(binary)
                        check(syncService.answer() == 42)

                        val richBinary = requireNotNull(Main::class.java.getResourceAsStream("/rich.wasm")).readBytes()
                        val realVirtualMachine = suspendingVirtualMachineFactory()
                        val virtualMachine = TrackingVirtualMachine(realVirtualMachine)
                        val initializerEvents = mutableListOf<Int>()
                        var moduleHookCalls = 0
                        var instanceHookCalls = 0
                        var decodedModule: Module? = null
                        val richService: RichService = richService(
                            binary = richBinary,
                            imports = listOf(recordingImport(initializerEvents)),
                            virtualMachine = virtualMachine,
                            moduleFactory = { bytes ->
                                delay(1)
                                moduleHookCalls += 1
                                check(bytes === richBinary)
                                realVirtualMachine.moduleDecodeSuspending(bytes).`expect`("module hook").also {
                                    decodedModule = it
                                }
                            },
                            instanceFactory = { store, module, imports ->
                                delay(1)
                                instanceHookCalls += 1
                                check(store === virtualMachine.store)
                                check(module === decodedModule)
                                check(imports.single().address === virtualMachine.allocatedFunction)
                                realVirtualMachine.moduleInstantiateSuspending(store, module, imports)
                                    .`expect`("instance hook")
                                    .let(virtualMachine::recordInstance)
                            },
                        )
                        check(virtualMachine.storeInitCalls == 1)
                        check(moduleHookCalls == 1)
                        check(instanceHookCalls == 1)
                        check(virtualMachine.moduleDecodeCalls == 0)
                        check(virtualMachine.moduleInstantiateCalls == 0)
                        check(virtualMachine.allocateFunctionCalls == 1)
                        check(virtualMachine.prepareFunctionCalls == 1)
                        check(virtualMachine.exportGlobalCalls == 1)
                        check(virtualMachine.initializerInvocations == listOf("initialize", "start", "finish"))
                        check(initializerEvents == listOf(1, 2, 3))
                        check(richService.counter == 16)
                        check(richService.counter == 16)
                        check(richService.answer() == 16)
                        check(richService.answer() == 16)
                        check(virtualMachine.prepareFunctionCalls == 1)

                        richService.counter = 21
                        check(richService.counter == 21)
                        check(richService.answer() == 21)
                        check(virtualMachine.exportGlobalCalls == 1)
                        val memoryBytes = byteArrayOf(4, 5, 6)
                        richService.memory.write(pointer = 8, buffer = memoryBytes)
                        check(
                            richService.memory.read(
                                buffer = ByteArray(memoryBytes.size),
                                memoryPointer = 8,
                            ).contentEquals(memoryBytes),
                        )

                        val failureEvents = mutableListOf<Int>()
                        val failureVirtualMachine = TrackingVirtualMachine(suspendingVirtualMachineFactory()).apply {
                            failInitializer = "start"
                        }
                        var failedService: RichService? = null
                        val initializerFailure = runCatching {
                            failedService = richService(
                                binary = richBinary,
                                imports = listOf(recordingImport(failureEvents)),
                                virtualMachine = failureVirtualMachine,
                            )
                        }.exceptionOrNull()
                        check(initializerFailure != null)
                        check(failedService == null)
                        check(failureVirtualMachine.storeInitCalls == 1)
                        check(failureVirtualMachine.moduleDecodeCalls == 1)
                        check(failureVirtualMachine.moduleInstantiateCalls == 1)
                        check(failureVirtualMachine.allocateFunctionCalls == 1)
                        check(failureVirtualMachine.prepareFunctionCalls == 1)
                        check(failureVirtualMachine.initializerInvocations == listOf("initialize", "start"))
                        check(failureEvents == listOf(1))

                        val decodeFailureVirtualMachine = TrackingVirtualMachine(
                            suspendingVirtualMachineFactory(),
                        ).apply {
                            failDecode = true
                        }
                        var decodeFailureService: RichService? = null
                        val decodeFailure = runCatching {
                            decodeFailureService = richService(
                                binary = richBinary,
                                imports = listOf(recordingImport(mutableListOf())),
                                virtualMachine = decodeFailureVirtualMachine,
                            )
                        }.exceptionOrNull()
                        check(decodeFailure != null)
                        check(decodeFailureService == null)
                        check(decodeFailureVirtualMachine.storeInitCalls == 1)
                        check(decodeFailureVirtualMachine.moduleDecodeCalls == 1)
                        check(decodeFailureVirtualMachine.allocateFunctionCalls == 0)
                        check(decodeFailureVirtualMachine.moduleInstantiateCalls == 0)
                        check(decodeFailureVirtualMachine.prepareFunctionCalls == 0)
                        check(decodeFailureVirtualMachine.initializerInvocations.isEmpty())

                        val instantiateFailureVirtualMachine = TrackingVirtualMachine(
                            suspendingVirtualMachineFactory(),
                        ).apply {
                            failInstantiate = true
                        }
                        var instantiateFailureService: RichService? = null
                        val instantiateFailure = runCatching {
                            instantiateFailureService = richService(
                                binary = richBinary,
                                imports = listOf(recordingImport(mutableListOf())),
                                virtualMachine = instantiateFailureVirtualMachine,
                            )
                        }.exceptionOrNull()
                        check(instantiateFailure != null)
                        check(instantiateFailureService == null)
                        check(instantiateFailureVirtualMachine.storeInitCalls == 1)
                        check(instantiateFailureVirtualMachine.moduleDecodeCalls == 1)
                        check(instantiateFailureVirtualMachine.allocateFunctionCalls == 1)
                        check(instantiateFailureVirtualMachine.moduleInstantiateCalls == 1)
                        check(instantiateFailureVirtualMachine.prepareFunctionCalls == 0)
                        check(instantiateFailureVirtualMachine.initializerInvocations.isEmpty())

                        println("FACTORIES_OK")
                    }
                }
            """,
        )

        val result = project.build("run")

        assertContains(result.output, "FACTORIES_OK")
    }

    @Test
    fun `private implementation cannot be called from consumer source`() {
        val project = project(
            build = """
                plugins {
                    id("$pluginId")
                    id("org.jetbrains.kotlin.jvm")
                }

                chasm {
                    modules.create("HiddenService") {
                        packageName.set("test.chasm")
                    }
                }
            """,
        )
        project.write(
            "src/main/kotlin/test/consumer/Consumer.kt",
            """
                package test.consumer

                import test.chasm.HiddenServiceImpl

                fun construct(binary: ByteArray) = HiddenServiceImpl(binary)
            """,
        )

        val result = project.buildAndFail("compileKotlin")

        assertContains(
            project.readGenerated("main", "HiddenService", "HiddenServiceImpl.kt"),
            "private class HiddenServiceImpl(",
        )
        assertContains(result.output, "HiddenServiceImpl")
        assertContains(result.output, "private")
    }

    @Test
    fun `configured implementations compile at their intended boundaries`() {
        val directory = createTempDirectory("chasm-gradle-plugin-test")
        directory.write(
            "settings.gradle.kts",
            settings(includedProjects = listOf(":producer", ":consumer")),
        )
        directory.write("build.gradle.kts", "")

        Files.createDirectories(directory.resolve("producer/src/main/wasm"))
        Files.write(directory.resolve("producer/src/main/wasm/module.wasm"), ANSWER_WASM_MODULE)
        directory.write(
            "producer/build.gradle.kts",
            """
                import io.github.charlietap.chasm.gradle.CodegenConfig
                import io.github.charlietap.chasm.gradle.FactoryVisibility
                import io.github.charlietap.chasm.gradle.ImplementationVisibility
                import io.github.charlietap.chasm.gradle.InterfaceVisibility
                import io.github.charlietap.chasm.gradle.RuntimeDependencyConfiguration

                plugins {
                    id("$pluginId")
                    id("org.jetbrains.kotlin.jvm")
                    `java-library`
                }

                chasm {
                    runtimeDependencyConfiguration.set(RuntimeDependencyConfiguration.API)
                    modules.create("PublicService") {
                        packageName.set("test.generated")
                        implementationVisibility.set(ImplementationVisibility.PUBLIC)
                    }
                    modules.create("InternalSuspendingService") {
                        packageName.set("test.generated")
                        interfaceVisibility.set(InterfaceVisibility.INTERNAL)
                        implementationVisibility.set(ImplementationVisibility.INTERNAL)
                        factoryVisibility.set(FactoryVisibility.INTERNAL)
                        codegenConfig.set(CodegenConfig(generateSuspendingFactories = true))
                    }
                }
            """,
        )
        directory.write(
            "producer/src/main/kotlin/test/producer/InternalAccess.kt",
            """
                package test.producer

                import io.github.charlietap.chasm.vm.Import
                import io.github.charlietap.chasm.vm.Instance
                import io.github.charlietap.chasm.vm.Store
                import io.github.charlietap.chasm.vm.WasmVirtualMachine
                import test.generated.InternalSuspendingService
                import test.generated.InternalSuspendingServiceImpl
                import test.generated.internalSuspendingService

                internal fun constructInternal(
                    imports: List<Import>,
                    instance: Instance,
                    store: Store,
                    virtualMachine: WasmVirtualMachine,
                ): InternalSuspendingServiceImpl = InternalSuspendingServiceImpl(
                    imports,
                    instance,
                    store,
                    virtualMachine,
                )

                internal suspend fun createInternal(binary: ByteArray): InternalSuspendingService =
                    internalSuspendingService(binary)
            """,
        )

        Files.createDirectories(directory.resolve("consumer"))
        directory.write(
            "consumer/build.gradle.kts",
            """
                plugins {
                    id("org.jetbrains.kotlin.jvm")
                }

                dependencies {
                    implementation(project(":producer"))
                }
            """,
        )
        directory.write(
            "consumer/src/main/kotlin/test/consumer/Consumer.kt",
            """
                package test.consumer

                import io.github.charlietap.chasm.vm.Import
                import io.github.charlietap.chasm.vm.Instance
                import io.github.charlietap.chasm.vm.Store
                import io.github.charlietap.chasm.vm.WasmVirtualMachine
                import test.generated.PublicService
                import test.generated.PublicServiceImpl
                import test.generated.publicService

                fun constructWithFactory(binary: ByteArray): PublicService = publicService(binary)

                fun constructImplementation(
                    imports: List<Import>,
                    instance: Instance,
                    store: Store,
                    virtualMachine: WasmVirtualMachine,
                ): PublicServiceImpl = PublicServiceImpl(imports, instance, store, virtualMachine)
            """,
        )
        val project = FunctionalProject(directory, WarningMode.FAIL)

        project.build(":consumer:compileKotlin")
    }

    @Test
    fun `configuration cache is reused with configured modules`() {
        val project = project(
            build = """
                plugins {
                    id("org.jetbrains.kotlin.jvm")
                    id("$pluginId")
                }

                chasm {
                    modules.create("CachedService") {
                        packageName.set("test.chasm")
                    }
                }
            """,
        )

        project.build("compileKotlin", "--configuration-cache")
        project.deleteGenerated("main", "CachedService")
        val reused = project.build("compileKotlin", "--configuration-cache")

        assertContains(reused.output, "Configuration cache entry reused.")
        assertContains(reused.output, "codegenModuleMainCachedService")
        project.assertGenerated("main", "CachedService")
    }

    @Test
    fun `factory and implementation visibility are independent codegen inputs`() {
        fun build(
            factoryVisibility: String,
            implementationVisibility: String,
        ) = """
            import io.github.charlietap.chasm.gradle.FactoryVisibility
            import io.github.charlietap.chasm.gradle.ImplementationVisibility

            plugins {
                id("org.jetbrains.kotlin.jvm")
                id("$pluginId")
            }

            chasm {
                modules.create("StableService") {
                    packageName.set("test.chasm")
                }
                modules.create("ChangingService") {
                    packageName.set("test.chasm")
                    factoryVisibility.set(FactoryVisibility.$factoryVisibility)
                    implementationVisibility.set(ImplementationVisibility.$implementationVisibility)
                }
            }
        """
        val project = project(build = build("INTERNAL", "INTERNAL"))

        project.build("compileKotlin", "--configuration-cache")
        assertContains(
            project.readGenerated("main", "StableService", "StableServiceImpl.kt"),
            "public fun stableService(",
        )
        assertContains(
            project.readGenerated("main", "ChangingService", "ChangingServiceImpl.kt"),
            "internal fun changingService(",
        )
        assertContains(
            project.readGenerated("main", "ChangingService", "ChangingServiceImpl.kt"),
            "internal class ChangingServiceImpl(",
        )

        project.write("build.gradle.kts", build("INTERNAL", "PUBLIC"))
        val implementationChanged = project.build("compileKotlin", "--configuration-cache")

        assertTrue(implementationChanged.task(":codegenModuleMainStableService")?.outcome == TaskOutcome.UP_TO_DATE)
        assertTrue(implementationChanged.task(":codegenModuleMainChangingService")?.outcome == TaskOutcome.SUCCESS)
        assertContains(
            project.readGenerated("main", "ChangingService", "ChangingServiceImpl.kt"),
            "internal fun changingService(",
        )
        assertContains(
            project.readGenerated("main", "ChangingService", "ChangingServiceImpl.kt"),
            "public class ChangingServiceImpl(",
        )

        project.write("build.gradle.kts", build("PUBLIC", "PUBLIC"))
        val factoryChanged = project.build("compileKotlin", "--configuration-cache")

        assertTrue(factoryChanged.task(":codegenModuleMainStableService")?.outcome == TaskOutcome.UP_TO_DATE)
        assertTrue(factoryChanged.task(":codegenModuleMainChangingService")?.outcome == TaskOutcome.SUCCESS)
        assertContains(
            project.readGenerated("main", "ChangingService", "ChangingServiceImpl.kt"),
            "public fun changingService(",
        )
        assertContains(
            project.readGenerated("main", "ChangingService", "ChangingServiceImpl.kt"),
            "public class ChangingServiceImpl(",
        )

        val unchanged = project.build("compileKotlin", "--configuration-cache")

        assertContains(unchanged.output, "Configuration cache entry reused.")
        assertTrue(unchanged.task(":codegenModuleMainStableService")?.outcome == TaskOutcome.UP_TO_DATE)
        assertTrue(unchanged.task(":codegenModuleMainChangingService")?.outcome == TaskOutcome.UP_TO_DATE)
    }

    @Test
    fun `plugin is compatible with isolated projects`() {
        val project = project(
            build = """
                plugins {
                    id("$pluginId")
                    id("org.jetbrains.kotlin.jvm")
                }

                chasm {
                    modules.create("IsolatedService") {
                        packageName.set("test.chasm")
                    }
                }
            """,
        )

        project.build(
            "help",
            "--isolated-projects",
            "-Dorg.gradle.isolated-projects.diagnostics=true",
        )
    }

    @Test
    fun `Android portable runtime selects the multiplatform coordinate`() {
        val project = androidProject(
            androidPluginId = "com.android.library",
            moduleName = "AndroidService",
            compileSdk = currentCompileSdk,
        )
        val result = project.build("dependencies", "--configuration=implementation")
        assertContains(result.output, "io.github.charlietap.chasm:vm:")
        assertFalse(result.output.contains("io.github.charlietap.chasm:vm-jvm:"))
    }

    @Test
    fun `Android Chasm runtime selects the multiplatform coordinate`() {
        val project = androidProject(
            androidPluginId = "com.android.library",
            moduleName = "AndroidService",
            compileSdk = currentCompileSdk,
            runtime = "CHASM",
        )
        val result = project.build("dependencies", "--configuration=implementation")
        assertContains(result.output, "io.github.charlietap.chasm:chasm:")
        assertFalse(result.output.contains("io.github.charlietap.chasm:chasm-jvm:"))
        assertFalse(result.output.contains("io.github.charlietap.chasm:vm:"))
    }

    @Test
    fun `current AGP 9 registers generated Kotlin sources on supported Gradle versions`() {
        testedGradleVersions.forEach { gradleVersion ->
            val project = androidProject(
                androidPluginId = "com.android.library",
                moduleName = "AndroidService",
                compileSdk = currentCompileSdk,
            )

            val result = project.build("tasks", "--group=chasm", gradleVersion = gradleVersion)
            assertContains(result.output, "codegenModuleDebugAndroidService")
            assertContains(result.output, "codegenModuleReleaseAndroidService")
        }
    }

    @Test
    fun `AGP 9 application library and test plugins are supported`() {
        listOf(
            "com.android.application" to "ApplicationService",
            "com.android.library" to "LibraryService",
        ).forEach { (androidPluginId, moduleName) ->
            val project = androidProject(
                androidPluginId = androidPluginId,
                moduleName = moduleName,
                compileSdk = currentCompileSdk,
            )

            val result = project.build("tasks", "--group=chasm")
            assertContains(result.output, "codegenModuleDebug$moduleName")
        }

        val testProject = androidTestProject()
        val testResult = testProject.build(":test:tasks", "--group=chasm")
        assertContains(testResult.output, "codegenModuleDebugTestService")
    }

    @Test
    fun `minimum AGP 9 API remains supported`() {
        val project = androidProject(
            androidPluginId = "com.android.library",
            moduleName = "MinimumAgpService",
            compileSdk = minimumAgpCompileSdk,
            minimumAgp = true,
        )

        val result = project.build(
            "tasks",
            "--group=chasm",
            gradleVersion = minimumAgpGradleVersion,
        )
        assertContains(result.output, "codegenModuleDebugMinimumAgpService")
        assertContains(result.output, "codegenModuleReleaseMinimumAgpService")
    }

    @Test
    fun `AGP 9 Kotlin multiplatform library is supported`() {
        val project = project(
            build = """
                plugins {
                    id("$pluginId")
                    id("org.jetbrains.kotlin.multiplatform")
                    id("com.android.kotlin.multiplatform.library")
                }

                kotlin {
                    jvm()
                    androidLibrary {
                        namespace = "test.chasm"
                        compileSdk = $currentCompileSdk
                        minSdk = $currentMinSdk
                    }
                }

                chasm {
                    modules.create("KmpAndroidService") {
                        packageName.set("test.chasm")
                    }
                }
            """,
            gradleProperties = """
                android.builtInKotlin=true
                android.newDsl=true
            """,
            warningMode = WarningMode.KNOWN_KMP_DEPRECATION,
        )

        val result = project.build("tasks", "--group=chasm")
        assertContains(result.output, "codegenModuleCommonMainKmpAndroidService")
    }

    private fun project(
        build: String,
        gradleProperties: String? = null,
        minimumAgp: Boolean = false,
        warningMode: WarningMode = WarningMode.FAIL,
        binary: ByteArray = MINIMAL_WASM_MODULE,
    ): FunctionalProject {
        val directory = createTempDirectory("chasm-gradle-plugin-test")
        directory.write("settings.gradle.kts", settings(minimumAgp = minimumAgp))
        directory.write("build.gradle.kts", build)
        Files.createDirectories(directory.resolve("src/main/wasm"))
        Files.write(directory.resolve("src/main/wasm/module.wasm"), binary)
        gradleProperties?.let { directory.write("gradle.properties", it) }
        return FunctionalProject(
            directory = directory,
            warningMode = warningMode,
        )
    }

    private fun androidProject(
        androidPluginId: String,
        moduleName: String,
        compileSdk: Int,
        minimumAgp: Boolean = false,
        runtime: String = "PORTABLE_VM",
    ): FunctionalProject {
        return project(
            build = """
                import io.github.charlietap.chasm.gradle.CodegenConfig
                import io.github.charlietap.chasm.gradle.CodegenRuntime

                plugins {
                    id("$pluginId")
                    id("$androidPluginId")
                }

                android {
                    namespace = "test.chasm"
                    compileSdk = $compileSdk
                }

                chasm {
                    modules.create("$moduleName") {
                        packageName.set("test.chasm")
                        codegenConfig.set(CodegenConfig(runtime = CodegenRuntime.$runtime))
                    }
                }
            """,
            gradleProperties = """
                android.builtInKotlin=true
                android.newDsl=true
            """,
            minimumAgp = minimumAgp,
            warningMode = WarningMode.KNOWN_AGP_DEPRECATION,
        )
    }

    private fun androidTestProject(): FunctionalProject {
        val directory = createTempDirectory("chasm-gradle-plugin-test")
        directory.write("settings.gradle.kts", settings(includedProjects = listOf(":app", ":test")))
        directory.write("build.gradle.kts", "")
        Files.createDirectories(directory.resolve("app"))
        directory.write(
            "app/build.gradle.kts",
            """
                plugins {
                    id("com.android.application")
                }

                android {
                    namespace = "test.chasm.app"
                    compileSdk = $currentCompileSdk
                }
            """,
        )
        Files.createDirectories(directory.resolve("test"))
        directory.write(
            "test/build.gradle.kts",
            """
                plugins {
                    id("$pluginId")
                    id("com.android.test")
                }

                android {
                    namespace = "test.chasm.test"
                    compileSdk = $currentCompileSdk
                    targetProjectPath = ":app"
                }

                chasm {
                    modules.create("TestService") {
                        packageName.set("test.chasm")
                    }
                }
            """,
        )
        directory.write(
            "gradle.properties",
            """
                android.builtInKotlin=true
                android.newDsl=true
            """,
        )
        return FunctionalProject(
            directory = directory,
            warningMode = WarningMode.KNOWN_AGP_DEPRECATION,
        )
    }

    private fun settings(
        minimumAgp: Boolean = false,
        includedProjects: List<String> = emptyList(),
    ): String {
        val agpVersion = if (minimumAgp) minimumAgpPluginVersion else androidPluginVersion
        return """
            import org.gradle.api.initialization.resolve.RepositoriesMode
            import org.gradle.util.GradleVersion

            pluginManagement {
                repositories {
                    maven {
                        url = uri("$pluginRepository")
                        metadataSources {
                            mavenPom()
                            artifact()
                        }
                    }
                    gradlePluginPortal()
                    google()
                    mavenCentral()
                }
                plugins {
                    id("$pluginId") version "$pluginVersion"
                    id("org.jetbrains.kotlin.jvm") version "$kotlinPluginVersion"
                    id("org.jetbrains.kotlin.multiplatform") version "$kotlinPluginVersion"
                    id("com.android.application") version "$agpVersion"
                    id("com.android.library") version "$agpVersion"
                    id("com.android.test") version "$agpVersion"
                    id("com.android.kotlin.multiplatform.library") version "$agpVersion"
                }
            }

            dependencyResolutionManagement {
                repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
                repositories {
                    maven {
                        url = uri("$functionalTestRepository")
                        metadataSources {
                            mavenPom()
                            artifact()
                        }
                    }
                    google()
                    mavenCentral()
                }
            }

            if (GradleVersion.current() >= GradleVersion.version("9.6")) {
                enableFeaturePreview("NO_IMPLICIT_LOOKUP_IN_PARENT_PROJECTS")
            }

            rootProject.name = "chasm-plugin-test"
            ${includedProjects.joinToString("\n") { projectPath -> "include(\"$projectPath\")" }}
        """
    }

    private class FunctionalProject(
        private val directory: Path,
        private val warningMode: WarningMode,
    ) {
        fun build(
            vararg arguments: String,
            gradleVersion: String? = null,
        ): BuildResult {
            val runner = GradleRunner.create()
                .withProjectDir(directory.toFile())
                .withArguments(
                    *arguments,
                    "--stacktrace",
                    warningMode.argument,
                )

            gradleVersion?.let(runner::withGradleVersion)
            return runner.build().also { result ->
                if (warningMode == WarningMode.KNOWN_KMP_DEPRECATION) {
                    assertContains(result.output, "getTaskDependencyFromProjectDependency")
                }
                if (warningMode == WarningMode.KNOWN_AGP_DEPRECATION) {
                    // AGP 9.3.1 calls setVisible; Gradle 9.8 now consistently reports it.
                    val unexpectedDeprecations = result.output.lineSequence()
                        .filter { it.contains("deprecat", ignoreCase = true) }
                        .filterNot { line ->
                            line.startsWith("The Configuration.setVisible(boolean) method has been deprecated.") ||
                                line.startsWith("Deprecated Gradle features were used") ||
                                line.trimStart().startsWith("at ")
                        }
                        .toList()
                    assertTrue(
                        unexpectedDeprecations.isEmpty(),
                        "Unexpected deprecations: ${unexpectedDeprecations.joinToString("\n")}",
                    )
                }
            }
        }

        fun buildAndFail(vararg arguments: String): BuildResult {
            return GradleRunner.create()
                .withProjectDir(directory.toFile())
                .withArguments(
                    *arguments,
                    "--stacktrace",
                    warningMode.argument,
                ).buildAndFail()
        }

        fun write(relativePath: String, content: String) {
            val path = directory.resolve(relativePath)
            Files.createDirectories(requireNotNull(path.parent))
            Files.writeString(path, content.trimIndent())
        }

        fun writeBytes(relativePath: String, content: ByteArray) {
            val path = directory.resolve(relativePath)
            Files.createDirectories(requireNotNull(path.parent))
            Files.write(path, content)
        }

        fun assertGenerated(
            sourceSetName: String,
            moduleName: String,
        ) {
            val outputDirectory = directory.resolve("build/generated/kotlin/$sourceSetName/$moduleName")
            assertTrue(Files.isDirectory(outputDirectory), "Missing generated output directory $outputDirectory")
            Files.walk(outputDirectory).use { files ->
                assertTrue(
                    files.anyMatch(Files::isRegularFile),
                    "Missing generated source in $outputDirectory",
                )
            }
        }

        fun deleteGenerated(
            sourceSetName: String,
            moduleName: String,
        ) {
            val outputDirectory = directory.resolve("build/generated/kotlin/$sourceSetName/$moduleName")
            Files.walk(outputDirectory).use { files ->
                files.sorted(Comparator.reverseOrder()).forEach(Files::delete)
            }
        }

        fun readGenerated(
            sourceSetName: String,
            moduleName: String,
            fileName: String,
        ): String {
            val path = directory.resolve(
                "build/generated/kotlin/$sourceSetName/$moduleName/test/chasm/$fileName",
            )
            return Files.readString(path)
        }
    }

    private enum class WarningMode(val argument: String) {
        FAIL("--warning-mode=fail"),
        KNOWN_AGP_DEPRECATION("--warning-mode=all"),
        KNOWN_KMP_DEPRECATION("--warning-mode=all"),
    }

    private companion object {
        val MINIMAL_WASM_MODULE = byteArrayOf(0, 97, 115, 109, 1, 0, 0, 0)
        val ANSWER_WASM_MODULE = byteArrayOf(
            0,
            97,
            115,
            109,
            1,
            0,
            0,
            0,
            1,
            5,
            1,
            96,
            0,
            1,
            127,
            3,
            2,
            1,
            0,
            7,
            10,
            1,
            6,
            97,
            110,
            115,
            119,
            101,
            114,
            0,
            0,
            10,
            6,
            1,
            4,
            0,
            65,
            42,
            11,
        )

        // Imports record(i32); initializers record 1/2/3 and set counter to 10/+5/+1.
        // Exports mutable counter, memory, and answer() so construction and bindings run for real.
        val RICH_WASM_MODULE = byteArrayOf(
            0,
            97,
            115,
            109,
            1,
            0,
            0,
            0,
            1,
            12,
            3,
            96,
            1,
            127,
            0,
            96,
            0,
            0,
            96,
            0,
            1,
            127,
            2,
            14,
            1,
            3,
            101,
            110,
            118,
            6,
            114,
            101,
            99,
            111,
            114,
            100,
            0,
            0,
            3,
            5,
            4,
            1,
            1,
            1,
            2,
            5,
            3,
            1,
            0,
            1,
            6,
            6,
            1,
            127,
            1,
            65,
            0,
            11,
            7,
            59,
            6,
            6,
            109,
            101,
            109,
            111,
            114,
            121,
            2,
            0,
            7,
            99,
            111,
            117,
            110,
            116,
            101,
            114,
            3,
            0,
            10,
            105,
            110,
            105,
            116,
            105,
            97,
            108,
            105,
            122,
            101,
            0,
            1,
            5,
            115,
            116,
            97,
            114,
            116,
            0,
            2,
            6,
            102,
            105,
            110,
            105,
            115,
            104,
            0,
            3,
            6,
            97,
            110,
            115,
            119,
            101,
            114,
            0,
            4,
            10,
            45,
            4,
            10,
            0,
            65,
            1,
            16,
            0,
            65,
            10,
            36,
            0,
            11,
            13,
            0,
            65,
            2,
            16,
            0,
            35,
            0,
            65,
            5,
            106,
            36,
            0,
            11,
            13,
            0,
            65,
            3,
            16,
            0,
            35,
            0,
            65,
            1,
            106,
            36,
            0,
            11,
            4,
            0,
            35,
            0,
            11,
            0,
            28,
            4,
            110,
            97,
            109,
            101,
            1,
            9,
            1,
            0,
            6,
            114,
            101,
            99,
            111,
            114,
            100,
            7,
            10,
            1,
            0,
            7,
            99,
            111,
            117,
            110,
            116,
            101,
            114,
        )
        val functionalTestRepository = requiredSystemProperty("chasm.functionalTest.repository")
        val pluginRepository = requiredSystemProperty("chasm.functionalTest.pluginRepository")
        val pluginPom = Path.of(requiredSystemProperty("chasm.functionalTest.pluginPom"))
        val pluginModuleMetadata = Path.of(
            requiredSystemProperty("chasm.functionalTest.pluginModuleMetadata"),
        )
        val pluginId = requiredSystemProperty("chasm.functionalTest.pluginId")
        val pluginVersion = requiredSystemProperty("chasm.functionalTest.pluginVersion")
        val kotlinPluginVersion = requiredSystemProperty("chasm.functionalTest.kotlinPluginVersion")
        val coroutinesVersion = requiredSystemProperty("chasm.functionalTest.coroutinesVersion")
        val androidPluginVersion = requiredSystemProperty("chasm.functionalTest.androidPluginVersion")
        val minimumAgpPluginVersion = requiredSystemProperty("chasm.functionalTest.minimumAgpPluginVersion")
        val minimumGradleVersion = requiredSystemProperty("chasm.functionalTest.minimumGradleVersion")
        val minimumAgpGradleVersion = requiredSystemProperty("chasm.functionalTest.minimumAgpGradleVersion")
        val testedGradleVersions = requiredSystemProperty("chasm.functionalTest.testedGradleVersions").split(',')
        val currentCompileSdk = requiredSystemProperty("chasm.functionalTest.compileSdk").toInt()
        val minimumAgpCompileSdk = requiredSystemProperty("chasm.functionalTest.minimumAgpCompileSdk").toInt()
        val currentMinSdk = requiredSystemProperty("chasm.functionalTest.minSdk").toInt()

        fun requiredSystemProperty(name: String): String {
            return requireNotNull(System.getProperty(name)) { "Missing system property $name" }
        }
    }
}

private fun Path.write(
    relativePath: String,
    content: String,
) {
    val path = resolve(relativePath)
    Files.createDirectories(requireNotNull(path.parent))
    Files.writeString(path, content.trimIndent())
}
