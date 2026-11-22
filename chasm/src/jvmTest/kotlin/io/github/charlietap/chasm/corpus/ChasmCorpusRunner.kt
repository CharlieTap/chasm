package io.github.charlietap.chasm.corpus

import at.released.weh.bindings.chasm.wasip1.ChasmWasiPreview1Builder
import at.released.weh.filesystem.stdio.StdioSink
import at.released.weh.filesystem.stdio.StdioSource
import at.released.weh.host.CommandArgsProvider
import at.released.weh.host.EmbedderHost
import at.released.weh.host.EntropySource
import at.released.weh.host.SystemEnvProvider
import at.released.weh.host.clock.Clock
import at.released.weh.host.clock.MonotonicClock
import io.github.charlietap.chasm.config.Config
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.global
import io.github.charlietap.chasm.embedding.global.readGlobal
import io.github.charlietap.chasm.embedding.global.readGlobalReference
import io.github.charlietap.chasm.embedding.global.writeGlobal
import io.github.charlietap.chasm.embedding.global.writeGlobalReference
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.memory
import io.github.charlietap.chasm.embedding.memory.growMemory
import io.github.charlietap.chasm.embedding.memory.readBytes
import io.github.charlietap.chasm.embedding.memory.writeBytes
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Exports
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.ImportDefinition
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.embedding.validate
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.host.HostStack
import io.github.charlietap.chasm.host.writeReference
import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.reference.FunctionReference
import io.github.charlietap.chasm.reference.I31Reference
import io.github.charlietap.chasm.reference.StructReference
import io.github.charlietap.chasm.runtime.type.ExternalType
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.corpus.lib.CorpusExecution
import io.github.charlietap.corpus.lib.CorpusFileReader
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.CorpusRunner
import io.github.charlietap.corpus.lib.CorpusTimings
import io.github.charlietap.corpus.lib.fixture.Fixture
import io.github.charlietap.corpus.lib.fixture.FixtureBytes
import io.github.charlietap.corpus.lib.fixture.FixtureImport
import io.github.charlietap.corpus.lib.fixture.FixtureImportCapture
import io.github.charlietap.corpus.lib.fixture.FixtureTest
import io.github.charlietap.corpus.lib.fixture.FixtureValue
import io.github.charlietap.corpus.lib.fixture.FixtureWasiPreview1Host
import kotlinx.coroutines.runBlocking
import kotlinx.io.Buffer
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.contextOf
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.TimeSource

class ChasmCorpusRunner(
    private val fileReader: CorpusFileReader,
    private val config: Config = Config(
        runtimeConfig = RuntimeConfig(gcStrategy = GCStrategy.TRADITIONAL),
    ),
) : CorpusRunner {

    override fun readText(path: String): String = fileReader.readText(path)

    override fun readBytes(path: String): ByteArray = fileReader.readBytes(path)

    override fun execute(
        corpusRoot: String,
        fixture: Fixture,
        phase: CorpusPhase,
    ): CorpusExecution = runBlocking {
        val timings = CorpusTimingRecorder()
        val totalStart = TimeSource.Monotonic.markNow()
        val result = executeFixture(corpusRoot, fixture, phase, timings)
        CorpusExecution(
            result = result,
            timings = timings.snapshot(totalStart.elapsedNow().inWholeNanoseconds),
            binarySizeBytes = timings.binarySizeBytes,
            moduleBuildCount = timings.moduleBuildCount,
        )
    }

    private suspend fun executeFixture(
        corpusRoot: String,
        fixture: Fixture,
        phase: CorpusPhase,
        timings: CorpusTimingRecorder,
    ): CorpusResult {
        val modulePath = "$corpusRoot/${fixture.version ?: "1.0"}/${fixture.path}"
        val bytes = readBytes(modulePath)
        timings.binarySizeBytes = bytes.size.toLong()
        val decoded = when (
            val result = timings.decode {
                module(bytes, config.moduleConfig, validate = false)
            }
        ) {
            is ChasmResult.Success -> result.result
            is ChasmResult.Error -> return failure(fixture, "decode", "failed to decode wasm", result.error.toString())
        }

        if (phase == CorpusPhase.DECODING) return CorpusResult.Success

        val validated = when (val result = timings.validate { validate(decoded) }) {
            is ChasmResult.Success -> result.result
            is ChasmResult.Error -> return failure(fixture, "validate", "failed to validate wasm", result.error.toString())
        }

        if (phase == CorpusPhase.VALIDATION) return CorpusResult.Success

        if (fixture.tests.isEmpty()) {
            val setup = timings.instantiate {
                val store = store()
                instantiate(fixture, store, validated, null, timings)
            }
            return setup.fold(
                { setup ->
                    timings.moduleBuildCount++
                    setup.close()
                    CorpusResult.Success
                },
                { error -> error },
            )
        }

        fixture.tests.forEachIndexed { testIndex, test ->
            val setupResult = timings.instantiate {
                val store = store()
                instantiate(fixture, store, validated, test, timings)
            }
            val setup = setupResult.fold(
                { setup ->
                    timings.moduleBuildCount++
                    setup
                },
                { error -> return error },
            )
            try {
                if (phase == CorpusPhase.INSTANTIATION) return@forEachIndexed

                val captures = mutableMapOf<String, ExecutionValue>()
                test.steps.forEachIndexed { stepIndex, step ->
                    val result = runStep(
                        fixture = fixture,
                        context = "test[$testIndex] step[$stepIndex]",
                        setup = setup,
                        captures = captures,
                        step = step,
                        timings = timings,
                    )
                    if (result != CorpusResult.Success) return result
                }
            } finally {
                setup.close()
            }
        }

        return CorpusResult.Success
    }

    private suspend fun instantiate(
        fixture: Fixture,
        store: Store,
        module: Module,
        test: FixtureTest?,
        timings: CorpusTimingRecorder,
    ): RunnerResult<RuntimeSetup> {
        val importResolution = when (val result = resolveImports(fixture, store, module, test, timings)) {
            is RunnerResult.Success -> result.value
            is RunnerResult.Error -> return RunnerResult.Error(result.error)
        }

        return instance(store, module, importResolution.imports, config.runtimeConfig).fold(
            { instance ->
                val exports = exports(instance)
                val exportedMemory = exports.all.firstNotNullOfOrNull { export -> export.value as? Memory }
                if (importResolution.memories.isEmpty() && exportedMemory != null) {
                    importResolution.memories["memory"] = exportedMemory
                }
                RunnerResult.Success(
                    RuntimeSetup(
                        module = module,
                        instance = instance,
                        exports = exports,
                        importMemories = importResolution.memories,
                        importGlobals = importResolution.globals,
                        hosts = importResolution.hosts,
                        stdout = importResolution.stdout,
                        stderr = importResolution.stderr,
                        importCaptures = importResolution.importCaptures,
                    ),
                )
            },
            { error ->
                importResolution.hosts.forEach(EmbedderHost::close)
                RunnerResult.Error(failure(fixture, "instantiate", "failed to instantiate wasm", error.toString()))
            },
        )
    }

    private fun resolveImports(
        fixture: Fixture,
        store: Store,
        module: Module,
        test: FixtureTest?,
        timings: CorpusTimingRecorder,
    ): RunnerResult<ImportResolution> {
        val imports = mutableListOf<Import>()
        val memories = mutableMapOf<String, Memory>()
        val globals = mutableMapOf<String, Global>()
        val hosts = mutableListOf<EmbedderHost>()
        val stdout = mutableListOf<Byte>()
        val stderr = mutableListOf<Byte>()
        val importCaptures = mutableMapOf<String, ImportCapture>()
        val wasiImportModules = setOf(WASI_SNAPSHOT_PREVIEW_1, WASI_UNSTABLE)
        val hasWasiImports = module.imports.any { it.moduleName in wasiImportModules }

        val wasiHost = test?.host?.wasiPreview1
        if (wasiHost == null && hasWasiImports) {
            return RunnerResult.Error(CorpusResult.Skipped(fixture.name, "WASI Preview 1 imports require fixture host config"))
        }

        val wasi = if (wasiHost != null && hasWasiImports) {
            createWasiHost(wasiHost, stdout, stderr)
        } else {
            null
        }

        if (wasi != null) {
            hosts += wasi.host
        }

        if (wasi != null && hasWasiImports) {
            imports += ChasmWasiPreview1Builder(store, module) {
                host = wasi.host
            }.build()
        }

        val providedImports = imports.map { import -> import.moduleName to import.entityName }.toMutableSet()

        module.imports.forEach { definition ->
            if (definition.moduleName in wasiImportModules) return@forEach
            if ((definition.moduleName to definition.entityName) in providedImports) return@forEach

            when (val type = definition.type) {
                is ExternalType.Function -> {
                    val fixtureImport = fixture.findImport(definition.moduleName, definition.entityName)
                    val capture = fixtureImport?.stub?.capture
                    if (capture != null) {
                        val existing = importCaptures[capture.name]
                        val maxCalls = capture.maxCalls ?: Int.MAX_VALUE
                        if (existing == null) {
                            importCaptures[capture.name] = ImportCapture(maxCalls = maxCalls)
                        } else {
                            existing.maxCalls = maxOf(existing.maxCalls, maxCalls)
                        }
                    }
                    val trap = fixtureImport?.stub?.trap
                    val parameterCount = type.functionType.params.types.size
                    val defaults = defaultResults(type.functionType, fixtureImport)
                    imports += Import(
                        definition.moduleName,
                        definition.entityName,
                        function(store, type.functionType) { parameters, results ->
                            val stack = contextOf<HostStack>()
                            if (capture != null) {
                                timings.harness {
                                    val params = List(parameterCount) { index -> stack[parameters + index] }
                                    importCaptures.getValue(capture.name).append(capture, params)
                                }
                            }
                            trap?.let(::error)
                            defaults.forEachIndexed { index, value ->
                                when (value) {
                                    is NumberValue<*> -> stack[results + index] = value.bits()
                                    is ReferenceValue -> results.writeReference(index, value.reference())
                                    else -> error("Unsupported corpus import result: $value")
                                }
                            }
                        },
                    )
                }
                is ExternalType.Global -> {
                    val fixtureImport = fixture.findImport(definition.moduleName, definition.entityName)
                    val value = fixtureImport?.stub?.value?.let(::parseFixtureValue) ?: defaultValue(type.globalType.valueType)
                    val imported = global(store, type.globalType, value)
                    globals[definition.importKey()] = imported
                    imports += Import(definition.moduleName, definition.entityName, imported)
                }
                is ExternalType.Memory -> {
                    val imported = memory(store, type.memoryType)
                    memories[definition.importKey()] = imported
                    imports += Import(definition.moduleName, definition.entityName, imported)
                }
                is ExternalType.Table -> return RunnerResult.Error(
                    CorpusResult.Skipped(fixture.name, "table imports are not supported yet: ${definition.importKey()}"),
                )
                is ExternalType.Tag -> return RunnerResult.Error(
                    CorpusResult.Skipped(fixture.name, "tag imports are not supported yet: ${definition.importKey()}"),
                )
            }
            providedImports += definition.moduleName to definition.entityName
        }

        return RunnerResult.Success(
            ImportResolution(imports, memories, globals, hosts, stdout, stderr, importCaptures),
        )
    }

    private fun createWasiHost(
        host: FixtureWasiPreview1Host,
        stdout: MutableList<Byte>,
        stderr: MutableList<Byte>,
    ): WasiRuntime {
        val stdin = host.stdin?.let(::decodeFixtureBytes) ?: ByteArray(0)
        return WasiRuntime(
            host = EmbedderHost {
                this.stdin = StdioSource.Provider { ByteArrayStdioSource(stdin) }
                this.stdout = StdioSink.Provider { CapturingStdioSink(stdout) }
                this.stderr = StdioSink.Provider { CapturingStdioSink(stderr) }
                this.entropySource = object : EntropySource {
                    override fun generateEntropy(size: Int): ByteArray = ByteArray(size)
                }
                this.realTimeClock = object : Clock {
                    private var nanos = 0L

                    override fun getCurrentTimeEpochNanoseconds(): Long = ++nanos

                    override fun getResolutionNanoseconds(): Long = 1L
                }
                this.monotonicClock = object : MonotonicClock {
                    private var nanos = 0L

                    override fun getTimeMarkNanoseconds(): Long = ++nanos

                    override fun getResolutionNanoseconds(): Long = 1L
                }

                commandArgs = CommandArgsProvider { host.commandArgs }
                systemEnv = SystemEnvProvider { host.systemEnv }
                fileSystem {
                    host.preopenedDirectories.forEach { (hostPath, guestPath) ->
                        addPreopenedDirectory(hostPath, guestPath)
                    }
                }
            },
        )
    }

    private fun runStep(
        fixture: Fixture,
        context: String,
        setup: RuntimeSetup,
        captures: MutableMap<String, ExecutionValue>,
        step: JsonObject,
        timings: CorpusTimingRecorder,
    ): CorpusResult {
        return when (val type = step.string("type")) {
            "function.invoke" -> runFunctionInvoke(fixture, context, setup, captures, step, timings)
            "memory.write" -> runMemoryWrite(fixture, context, setup, captures, step, timings)
            "memory.read" -> runMemoryRead(fixture, context, setup, captures, step, timings)
            "memory.grow" -> runMemoryGrow(fixture, context, setup, captures, step, timings)
            "global.write" -> runGlobalWrite(fixture, context, setup, captures, step, timings)
            "global.read" -> runGlobalRead(fixture, context, setup, captures, step, timings)
            "table.write", "table.read" -> CorpusResult.Skipped(fixture.name, "$type steps are not supported yet")
            else -> failure(fixture, context, "unsupported corpus step", type)
        }
    }

    private fun runFunctionInvoke(
        fixture: Fixture,
        context: String,
        setup: RuntimeSetup,
        captures: MutableMap<String, ExecutionValue>,
        step: JsonObject,
        timings: CorpusTimingRecorder,
    ): CorpusResult {
        val function = step.string("function")
        val functionType = setup.functionType(function)
        val params = step.array("params").mapIndexed { index, value ->
            parseValue(value.jsonObject, captures, functionType?.params?.types?.getOrNull(index))
        }
        val expected = step.arrayOrNull("results") ?: JsonArray(emptyList())
        val trap = step["trap"]?.jsonPrimitive?.contentOrNull
        val hostEffects = step["host_effects"]?.jsonObject
        val expectedExitCode = hostEffects?.get("exit_code")?.jsonPrimitive?.intOrNull

        val result = when (val invocation = timings.execute { invoke(setup.instance, function, params) }) {
            is ChasmResult.Success -> invocation.result
            is ChasmResult.Error -> {
                val actualExitCode = hostFunctionExitCodeOrNull(invocation.error)
                if (trap != null) {
                    return CorpusResult.Success
                } else if (expectedExitCode != null && actualExitCode != null) {
                    if (expectedExitCode != actualExitCode) {
                        return failure(
                            fixture,
                            context,
                            "exit code mismatch",
                            expected = expectedExitCode.toString(),
                            actual = actualExitCode.toString(),
                            detail = "stdout=${setup.stdout.toByteArray().decodeToString().take(MAX_FAILURE_OUTPUT)} " +
                                "stderr=${setup.stderr.toByteArray().decodeToString().take(MAX_FAILURE_OUTPUT)}",
                        )
                    }
                    hostEffects.let { effects ->
                        verifyHostEffects(fixture, context, setup, effects)?.let { return it }
                    }
                    emptyList()
                } else {
                    return failure(fixture, context, "function invocation failed", invocation.error.toString())
                }
            }
        }

        if (trap != null) {
            return failure(fixture, context, "expected function invocation to trap", expected = trap, actual = "success")
        }

        if (expected.size != result.size) {
            return failure(
                fixture,
                context,
                "result count mismatch",
                expected = expected.size.toString(),
                actual = result.size.toString(),
            )
        }

        hostEffects?.let { effects ->
            verifyHostEffects(fixture, context, setup, effects)?.let { return it }
        }

        expected.zip(result).forEachIndexed { index, (expectedValue, actualValue) ->
            val expectedObject = expectedValue.jsonObject
            expectedObject["capture"]?.jsonPrimitive?.contentOrNull?.let { capture ->
                captures[capture] = actualValue
                return@forEachIndexed
            }
            if (expectedObject["value"] == null) return@forEachIndexed

            val parsed = parseValue(expectedObject, captures, functionType?.results?.types?.getOrNull(index))
            if (!parsed.matches(actualValue)) {
                return failure(
                    fixture,
                    context,
                    "result mismatch",
                    expected = "result[$index]=${parsed.describe()}",
                    actual = "result[$index]=${actualValue.describe()}",
                )
            }
        }

        return CorpusResult.Success
    }

    private fun verifyHostEffects(
        fixture: Fixture,
        context: String,
        setup: RuntimeSetup,
        hostEffects: JsonObject,
    ): CorpusResult? {
        hostEffects["stdout"]?.let { expected ->
            val expectedBytes = decodeBytes(expected)
            val actual = setup.stdout.toByteArray()
            if (!expectedBytes.contentEquals(actual)) {
                return failure(fixture, context, "stdout mismatch", expected = expectedBytes.toHex(), actual = actual.toHex())
            }
        }

        hostEffects["stderr"]?.let { expected ->
            val expectedBytes = decodeBytes(expected)
            val actual = setup.stderr.toByteArray()
            if (!expectedBytes.contentEquals(actual)) {
                return failure(fixture, context, "stderr mismatch", expected = expectedBytes.toHex(), actual = actual.toHex())
            }
        }

        hostEffects["import_captures"]?.jsonArray?.forEach { element ->
            val assertion = element.jsonObject
            val name = assertion.string("name")
            val capture = setup.importCaptures[name]
                ?: return failure(fixture, context, "missing import capture", expected = name)
            val expected = assertion["text"]?.jsonPrimitive?.contentOrNull?.encodeToByteArray()
                ?: assertion["bytes"]?.let(::decodeBytes)
                ?: return failure(fixture, context, "import capture requires text or bytes", name)
            val actual = capture.text.toString().encodeToByteArray()
            if (!expected.contentEquals(actual)) {
                return failure(
                    fixture,
                    context,
                    "import capture mismatch",
                    detail = name,
                    expected = expected.toHex(),
                    actual = actual.toHex(),
                )
            }
        }

        return null
    }

    private fun runMemoryWrite(
        fixture: Fixture,
        context: String,
        setup: RuntimeSetup,
        captures: MutableMap<String, ExecutionValue>,
        step: JsonObject,
        timings: CorpusTimingRecorder,
    ): CorpusResult {
        val memory = resolveMemory(setup, step.stringOrDefault("memory", "memory"))
            ?: return failure(fixture, context, "memory not found", step["memory"]?.toString())
        val offset = parseIndex(step.getValue("offset"), captures)
        val bytes = step["bytes"]?.let(::decodeBytes) ?: step["value"]?.jsonObject?.let(::encodeValueBytes)
            ?: return failure(fixture, context, "memory write requires bytes or value")

        return try {
            timings.execute { writeBytes(memory, offset, bytes) }
            CorpusResult.Success
        } catch (failure: IndexOutOfBoundsException) {
            failure(fixture, context, "memory write failed", failure.message)
        }
    }

    private fun runMemoryRead(
        fixture: Fixture,
        context: String,
        setup: RuntimeSetup,
        captures: MutableMap<String, ExecutionValue>,
        step: JsonObject,
        timings: CorpusTimingRecorder,
    ): CorpusResult {
        val memory = resolveMemory(setup, step.stringOrDefault("memory", "memory"))
            ?: return failure(fixture, context, "memory not found", step["memory"]?.toString())
        val offset = parseIndex(step.getValue("offset"), captures)
        val capture = step["capture"]?.jsonObject
        if (capture != null) {
            val value = readMemoryValue(fixture, context, memory, offset, capture.string("type"), timings)
                ?: return failure(fixture, context, "memory capture failed")
            captures[capture.string("capture")] = value
            return CorpusResult.Success
        }

        val assertion = step["assert"]?.jsonObject ?: return failure(fixture, context, "memory read requires assert or capture")
        val expected = assertion["bytes"]?.let(::decodeBytes)
            ?: assertion["value"]?.jsonObject?.let(::encodeValueBytes)
            ?: return failure(fixture, context, "unsupported memory assertion")
        val buffer = ByteArray(expected.size)

        val actual = try {
            timings.execute { readBytes(memory, buffer, offset, expected.size) }
        } catch (failure: IndexOutOfBoundsException) {
            return failure(fixture, context, "memory read failed", failure.message)
        }

        return if (expected.contentEquals(actual)) {
            CorpusResult.Success
        } else {
            failure(fixture, context, "memory mismatch", expected = expected.toHex(), actual = actual.toHex())
        }
    }

    private fun runMemoryGrow(
        fixture: Fixture,
        context: String,
        setup: RuntimeSetup,
        captures: MutableMap<String, ExecutionValue>,
        step: JsonObject,
        timings: CorpusTimingRecorder,
    ): CorpusResult {
        val memory = resolveMemory(setup, step.stringOrDefault("memory", "memory"))
            ?: return failure(fixture, context, "memory not found", step["memory"]?.toString())
        val pagesToAdd = parseIndex(step.getValue("pages"), captures)
        val actual = NumberValue.I32(timings.execute { growMemory(memory, pagesToAdd) })

        step["capture"]?.jsonPrimitive?.contentOrNull?.let { capture ->
            captures[capture] = actual
        }

        val assertion = step["assert"]?.jsonObject ?: return CorpusResult.Success
        val expected = assertion["value"]?.jsonObject?.let { value -> parseValue(value, captures) }
            ?: return failure(fixture, context, "unsupported memory grow assertion")

        return if (expected.matches(actual)) {
            CorpusResult.Success
        } else {
            failure(fixture, context, "memory grow mismatch", expected = expected.describe(), actual = actual.describe())
        }
    }

    private fun runGlobalWrite(
        fixture: Fixture,
        context: String,
        setup: RuntimeSetup,
        captures: MutableMap<String, ExecutionValue>,
        step: JsonObject,
        timings: CorpusTimingRecorder,
    ): CorpusResult {
        val global = resolveGlobal(setup, step.string("global"))
            ?: return failure(fixture, context, "global not found", step.string("global"))
        val value = parseValue(step.getValue("value").jsonObject, captures)

        return try {
            when (value) {
                is NumberValue<*> -> {
                    val bits = value.bits()
                    timings.execute { writeGlobal(global, bits) }
                }
                is ReferenceValue -> {
                    val reference = value.reference()
                    timings.execute { writeGlobalReference(global, reference) }
                }
                else -> return failure(fixture, context, "global write failed", "unsupported value $value")
            }
            CorpusResult.Success
        } catch (failure: IllegalArgumentException) {
            failure(fixture, context, "global write failed", failure.message)
        }
    }

    private fun runGlobalRead(
        fixture: Fixture,
        context: String,
        setup: RuntimeSetup,
        captures: MutableMap<String, ExecutionValue>,
        step: JsonObject,
        timings: CorpusTimingRecorder,
    ): CorpusResult {
        val global = resolveGlobal(setup, step.string("global"))
            ?: return failure(fixture, context, "global not found", step.string("global"))
        val actual = when (val type = global.type.valueType) {
            is ValueType.Reference -> referenceValue(timings.execute { readGlobalReference(global) }, type.referenceType.heapType)
            else -> numberValue(timings.execute { readGlobal(global) }, type)
        }
        val expected = parseValue(step.getValue("assert").jsonObject.getValue("value").jsonObject, captures)

        return if (expected.matches(actual)) {
            CorpusResult.Success
        } else {
            failure(fixture, context, "global mismatch", expected = expected.describe(), actual = actual.describe())
        }
    }

    private fun resolveMemory(
        setup: RuntimeSetup,
        name: String,
    ): Memory? {
        setup.exports.all.firstOrNull { export -> export.name == name }?.value?.let { value ->
            return value as? Memory
        }
        if (name == "env.memory") return setup.importMemories[name]
        if (name == "memory") {
            setup.exports.all.firstOrNull { export -> export.value is Memory }?.value?.let { value ->
                return value as Memory
            }
        }
        return setup.importMemories[name]
    }

    private fun resolveGlobal(
        setup: RuntimeSetup,
        name: String,
    ): Global? {
        setup.exports.all.firstOrNull { export -> export.name == name }?.value?.let { value ->
            return value as? Global
        }
        return setup.importGlobals[name]
    }

    private fun defaultResults(
        type: FunctionType,
        import: FixtureImport?,
    ): List<ExecutionValue> {
        val declared = import?.stub?.returns.orEmpty()
        return type.results.types.mapIndexed { index, valueType ->
            declared.getOrNull(index)?.let(::parseFixtureValue) ?: defaultValue(valueType)
        }
    }

    private fun defaultValue(type: ValueType): ExecutionValue = when (type) {
        is ValueType.Number -> numberValue(0L, type)
        is ValueType.Reference -> ReferenceValue.Null(type.referenceType.heapType)
        else -> error("Unsupported corpus value type: $type")
    }

    private fun numberValue(
        bits: Long,
        type: ValueType,
    ): NumberValue<*> = when ((type as ValueType.Number).numberType) {
        NumberType.I32 -> NumberValue.I32(bits.toInt())
        NumberType.I64 -> NumberValue.I64(bits)
        NumberType.F32 -> NumberValue.F32(Float.fromBits(bits.toInt()))
        NumberType.F64 -> NumberValue.F64(Double.fromBits(bits))
    }

    private fun referenceValue(
        reference: Any?,
        heapType: HeapType,
    ): ReferenceValue = when (reference) {
        null -> ReferenceValue.Null(heapType)
        is StructReference -> ReferenceValue.Struct(reference)
        is ArrayReference -> ReferenceValue.Array(reference)
        is I31Reference -> ReferenceValue.I31(reference)
        is FunctionReference -> ReferenceValue.Function(reference)
        is ExceptionReference -> ReferenceValue.Exception(reference)
        else -> ReferenceValue.Extern(reference)
    }

    private fun parseFixtureValue(value: FixtureValue): ExecutionValue {
        return parseValue(
            JsonObject(
                buildMap {
                    put("type", JsonPrimitive(value.type))
                    value.value?.let { put("value", JsonPrimitive(it)) }
                    value.capture?.let { put("capture", JsonPrimitive(it)) }
                    value.variable?.let { put("var", JsonPrimitive(it)) }
                },
            ),
            emptyMap(),
            null,
        )
    }

    private fun parseValue(
        value: JsonObject,
        captures: Map<String, ExecutionValue>,
        expectedType: ValueType? = null,
    ): ExecutionValue {
        value["var"]?.jsonPrimitive?.contentOrNull?.let { name ->
            return captures.getValue(name)
        }
        val type = value.string("type")
        val raw = value["value"]?.jsonPrimitive?.contentOrNull

        return when (type) {
            "i32" -> {
                val parsed = raw?.let { it.toIntOrNull() ?: it.toUInt().toInt() } ?: 0
                if (expectedType is ValueType.Reference) {
                    ReferenceValue.I31(I31Reference(parsed))
                } else {
                    NumberValue.I32(parsed)
                }
            }
            "i64" -> NumberValue.I64(raw?.let { it.toLongOrNull() ?: it.toULong().toLong() } ?: 0)
            "f32" -> NumberValue.F32(raw?.let { Float.fromBits(it.toUInt().toInt()) } ?: 0f)
            "f64" -> NumberValue.F64(raw?.let { Double.fromBits(it.toULong().toLong()) } ?: 0.0)
            "funcref" -> ReferenceValue.Null(AbstractHeapType.Func)
            "externref" -> ReferenceValue.Null(AbstractHeapType.Extern)
            else -> error("Unsupported corpus value type: $type")
        }
    }

    private fun readMemoryValue(
        fixture: Fixture,
        context: String,
        memory: Memory,
        offset: Int,
        type: String,
        timings: CorpusTimingRecorder,
    ): ExecutionValue? {
        val size = if (type == "i64" || type == "f64") 8 else 4
        val buffer = ByteArray(size)
        val bytes = try {
            timings.execute { readBytes(memory, buffer, offset, size) }
        } catch (_: IndexOutOfBoundsException) {
            return null
        }
        val bits = bytes.readLittleEndian()
        return when (type) {
            "i32" -> NumberValue.I32(bits.toInt())
            "i64" -> NumberValue.I64(bits)
            "f32" -> NumberValue.F32(Float.fromBits(bits.toInt()))
            "f64" -> NumberValue.F64(Double.fromBits(bits))
            else -> {
                failure(fixture, context, "unsupported memory capture type", type)
                null
            }
        }
    }

    private fun parseIndex(
        value: JsonElement,
        captures: Map<String, ExecutionValue>,
    ): Int {
        value.jsonPrimitiveOrNull()?.intOrNull?.let { return it }
        val variable = value.jsonObject["var"]?.jsonPrimitive?.content ?: error("Invalid corpus index expression")
        return (captures.getValue(variable) as NumberValue<*>).bits().toInt()
    }

    private fun encodeValueBytes(value: JsonObject): ByteArray {
        val parsed = (parseValue(value, emptyMap()) as NumberValue<*>).bits()
        val size = when (value.string("type")) {
            "i64", "f64" -> 8
            else -> 4
        }
        return ByteArray(size) { index -> ((parsed ushr (index * 8)) and 0xff).toByte() }
    }

    private fun decodeBytes(value: JsonElement): ByteArray {
        val bytes = value.jsonObject["bytes"]?.jsonObject ?: value.jsonObject
        val encoding = bytes.string("encoding")
        val raw = bytes.string("value")
        return when (encoding) {
            "utf8" -> raw.encodeToByteArray()
            "hex" -> raw.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
            "base64" -> decodeBase64(raw)
            else -> error("Unsupported byte encoding: $encoding")
        }
    }

    private fun decodeFixtureBytes(value: FixtureBytes): ByteArray = when (value.encoding) {
        "utf8" -> value.value.encodeToByteArray()
        "hex" -> value.value.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        "base64" -> decodeBase64(value.value)
        else -> error("Unsupported byte encoding: ${value.encoding}")
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun decodeBase64(value: String): ByteArray = Base64.decode(value)

    private fun Fixture.findImport(module: String, name: String): FixtureImport? {
        return imports.firstOrNull { import -> import.module == module && import.name == name }
    }

    private fun ImportDefinition.importKey(): String {
        return "$moduleName.$entityName"
    }

    private fun JsonObject.string(name: String): String = getValue(name).jsonPrimitive.content

    private fun JsonObject.stringOrDefault(name: String, default: String): String {
        return this[name]?.jsonPrimitive?.contentOrNull ?: default
    }

    private fun JsonObject.array(name: String): JsonArray = arrayOrNull(name) ?: JsonArray(emptyList())

    private fun JsonObject.arrayOrNull(name: String): JsonArray? = this[name]?.jsonArray

    private fun JsonElement.jsonPrimitiveOrNull(): JsonPrimitive? = this as? JsonPrimitive

    private fun NumberValue<*>.bits(): Long = when (this) {
        is NumberValue.I32 -> value.toLong()
        is NumberValue.I64 -> value
        is NumberValue.F32 -> value.toRawBits().toLong()
        is NumberValue.F64 -> value.toRawBits()
    }

    private fun ReferenceValue.reference(): Any? = when (this) {
        is ReferenceValue.Null -> null
        is ReferenceValue.Struct -> reference
        is ReferenceValue.Array -> reference
        is ReferenceValue.I31 -> reference
        is ReferenceValue.Function -> reference
        is ReferenceValue.Exception -> reference
        is ReferenceValue.Extern -> value
    }

    private fun ExecutionValue.matches(other: ExecutionValue): Boolean = when (this) {
        is NumberValue<*> -> other is NumberValue<*> && bits() == other.bits()
        else -> this == other
    }

    private fun ExecutionValue.describe(): String = when (this) {
        is NumberValue<*> -> "bits=${bits()}"
        else -> toString()
    }

    private fun ByteArray.readLittleEndian(): Long {
        var result = 0L
        forEachIndexed { index, byte ->
            result = result or ((byte.toLong() and 0xff) shl (index * 8))
        }
        return result
    }

    private fun ByteArray.toHex(): String = joinToString("") { byte ->
        byte.toUByte().toString(16).padStart(2, '0')
    }

    private fun hostFunctionExitCodeOrNull(error: ExecutionError): Int? {

        val trap = (error as? ExecutionError.Trapped)?.trap ?: return null
        if (trap.reason != TrapReason.HOST_FUNCTION_FAILURE) {
            return null
        }

        val prefix = "HostFunctionError(error="
        val message = trap.message
        val start = message.indexOf(prefix).takeIf { index -> index >= 0 } ?: return null
        val valueStart = start + prefix.length
        val valueEnd = message.indexOf(")", valueStart).takeIf { index -> index >= 0 } ?: return null
        return message.substring(valueStart, valueEnd).toIntOrNull()
    }

    private fun failure(
        fixture: Fixture,
        context: String,
        message: String,
        detail: String? = null,
        expected: String? = null,
        actual: String? = null,
    ): CorpusResult.Failure = CorpusResult.Failure(
        fixture = fixture.name,
        context = context,
        message = message,
        detail = detail,
        expected = expected,
        actual = actual,
        rerun = "./gradlew :chasm:corpus -PwasmCorpus.targets=${fixture.name}",
    )

    private data class ImportResolution(
        val imports: List<Import>,
        val memories: MutableMap<String, Memory>,
        val globals: Map<String, Global>,
        val hosts: List<EmbedderHost>,
        val stdout: MutableList<Byte>,
        val stderr: MutableList<Byte>,
        val importCaptures: MutableMap<String, ImportCapture>,
    )

    private data class WasiRuntime(
        val host: EmbedderHost,
    )

    private data class RuntimeSetup(
        val module: Module,
        val instance: Instance,
        val exports: Exports,
        val importMemories: Map<String, Memory>,
        val importGlobals: Map<String, Global>,
        val hosts: List<EmbedderHost>,
        val stdout: MutableList<Byte>,
        val stderr: MutableList<Byte>,
        val importCaptures: Map<String, ImportCapture>,
    ) {
        fun functionType(name: String): FunctionType? {
            val export = module.exports.firstOrNull { definition -> definition.name == name } ?: return null
            return (export.type as? ExternalType.Function)?.functionType
        }

        fun close() {
            hosts.forEach(EmbedderHost::close)
        }
    }

    private sealed interface RunnerResult<out T> {
        data class Success<T>(val value: T) : RunnerResult<T>

        data class Error(val error: CorpusResult) : RunnerResult<Nothing>
    }

    private class CorpusTimingRecorder {
        var binarySizeBytes: Long = 0
        var moduleBuildCount: Int = 0

        private var decodeNanos: Long = 0
        private var validateNanos: Long = 0
        private var instantiateNanos: Long = 0
        var executeNanos: Long = 0
        var harnessNanos: Long = 0
        var executeDepth: Int = 0

        suspend fun <T> decode(block: suspend () -> T): T = measureSuspending(block) { elapsed -> decodeNanos += elapsed }

        suspend fun <T> validate(block: suspend () -> T): T = measureSuspending(block) { elapsed -> validateNanos += elapsed }

        suspend fun <T> instantiate(block: suspend () -> T): T = measureSuspending(block) { elapsed -> instantiateNanos += elapsed }

        inline fun <T> execute(block: () -> T): T {
            if (executeDepth > 0) {
                return block()
            }

            executeDepth++
            return try {
                measure(block) { elapsed -> executeNanos += elapsed }
            } finally {
                executeDepth--
            }
        }

        inline fun <T> harness(block: () -> T): T {
            if (executeDepth == 0) {
                return block()
            }

            return measure(block) { elapsed -> harnessNanos += elapsed }
        }

        fun snapshot(totalNanos: Long) = CorpusTimings(
            totalNanos = totalNanos,
            decodeNanos = decodeNanos,
            validateNanos = validateNanos,
            instantiateNanos = instantiateNanos,
            executeNanos = executeNanos - harnessNanos,
        )

        private suspend fun <T> measureSuspending(
            block: suspend () -> T,
            record: (Long) -> Unit,
        ): T {
            val start = TimeSource.Monotonic.markNow()
            return try {
                block()
            } finally {
                record(start.elapsedNow().inWholeNanoseconds)
            }
        }

        inline fun <T> measure(
            block: () -> T,
            record: (Long) -> Unit,
        ): T {
            val start = TimeSource.Monotonic.markNow()
            return try {
                block()
            } finally {
                record(start.elapsedNow().inWholeNanoseconds)
            }
        }
    }

    private data class ImportCapture(
        val text: StringBuilder = StringBuilder(),
        var calls: Int = 0,
        var maxCalls: Int,
    ) {
        fun append(
            capture: FixtureImportCapture,
            params: List<Long>,
        ) {
            if (calls < maxCalls) {
                val bits = params.getOrNull(capture.param)
                    ?: error("Import capture ${capture.name} param ${capture.param} is missing")
                when (capture.format ?: capture.encoding ?: "utf16_code_unit") {
                    "utf16_code_unit" -> text.append((bits.toInt() and 0xffff).toChar())
                    "unicode_code_point" -> text.appendCodePoint(bits.toInt())
                    "decimal_i32" -> text.append(bits.toInt())
                    else -> error("Unsupported import capture format: ${capture.format ?: capture.encoding}")
                }
            }
            calls++
        }
    }

    private inline fun <T, R> RunnerResult<T>.fold(
        onSuccess: (T) -> R,
        onError: (CorpusResult) -> R,
    ): R = when (this) {
        is RunnerResult.Success -> onSuccess(value)
        is RunnerResult.Error -> onError(error)
    }

    private companion object {
        const val WASI_SNAPSHOT_PREVIEW_1 = "wasi_snapshot_preview1"
        const val WASI_UNSTABLE = "wasi_unstable"
        const val MAX_FAILURE_OUTPUT = 2048
    }
}

private class ByteArrayStdioSource(
    private val bytes: ByteArray,
) : StdioSource {

    private var offset = 0

    override fun readAtMostTo(
        sink: Buffer,
        byteCount: Long,
    ): Long {
        if (offset >= bytes.size) return -1

        val count = minOf(byteCount.toInt(), bytes.size - offset)
        sink.write(bytes, offset, offset + count)
        offset += count
        return count.toLong()
    }

    override fun close() = Unit
}

private class CapturingStdioSink(
    private val bytes: MutableList<Byte>,
) : StdioSink {

    override fun write(
        source: Buffer,
        byteCount: Long,
    ) {
        repeat(byteCount.toInt()) {
            bytes += source.readByte()
        }
    }

    override fun flush() = Unit

    override fun close() = Unit
}
