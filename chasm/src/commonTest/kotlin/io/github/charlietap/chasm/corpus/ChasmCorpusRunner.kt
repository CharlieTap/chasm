package io.github.charlietap.chasm.corpus

import at.released.weh.bindings.chasm.ChasmEmscriptenHostBuilder
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
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.global
import io.github.charlietap.chasm.embedding.global.readGlobal
import io.github.charlietap.chasm.embedding.global.writeGlobal
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.memory
import io.github.charlietap.chasm.embedding.memory.readBytes
import io.github.charlietap.chasm.embedding.memory.writeBytes
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.embedding.validate
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.type.ExternalType
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.NumberValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.script.ext.readBytesFromPath
import io.github.charlietap.chasm.script.ext.readTextFromPath
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.CorpusRunner
import io.github.charlietap.corpus.lib.fixture.Fixture
import io.github.charlietap.corpus.lib.fixture.FixtureBytes
import io.github.charlietap.corpus.lib.fixture.FixtureImport
import io.github.charlietap.corpus.lib.fixture.FixtureTest
import io.github.charlietap.corpus.lib.fixture.FixtureWasiPreview1Host
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
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class ChasmCorpusRunner(
    private val config: Config = Config(),
) : CorpusRunner {

    override fun readText(path: String): String = path.readTextFromPath()

    override fun readBytes(path: String): ByteArray = path.readBytesFromPath()

    override fun execute(
        corpusRoot: String,
        fixture: Fixture,
        phase: CorpusPhase,
    ): CorpusResult {
        val modulePath = "$corpusRoot/${fixture.version ?: "1.0"}/${fixture.path}"
        val decoded = when (val result = module(readBytes(modulePath), config.moduleConfig)) {
            is ChasmResult.Success -> result.result
            is ChasmResult.Error -> return failure(fixture, "decode", "failed to decode wasm", result.error.toString())
        }

        if (phase == CorpusPhase.DECODING) return CorpusResult.Success

        val validated = when (val result = validate(decoded)) {
            is ChasmResult.Success -> result.result
            is ChasmResult.Error -> return failure(fixture, "validate", "failed to validate wasm", result.error.toString())
        }

        if (phase == CorpusPhase.VALIDATION) return CorpusResult.Success

        if (fixture.tests.isEmpty()) {
            val store = store()
            return instantiate(fixture, store, validated, null).fold(
                { setup ->
                    setup.close()
                    CorpusResult.Success
                },
                { error -> error },
            )
        }

        fixture.tests.forEachIndexed { testIndex, test ->
            val store = store()
            val setup = instantiate(fixture, store, validated, test).fold(
                { setup -> setup },
                { error -> return error },
            )
            if (phase == CorpusPhase.INSTANTIATION) return@forEachIndexed

            val captures = mutableMapOf<String, ExecutionValue>()
            try {
                test.steps.forEachIndexed { stepIndex, step ->
                    val result = runStep(
                        fixture = fixture,
                        context = "test[$testIndex] step[$stepIndex]",
                        store = store,
                        setup = setup,
                        captures = captures,
                        step = step,
                    )
                    if (result != CorpusResult.Success) return result
                }
            } finally {
                setup.close()
            }
        }

        return CorpusResult.Success
    }

    private fun instantiate(
        fixture: Fixture,
        store: Store,
        module: Module,
        test: FixtureTest?,
    ): RunnerResult<RuntimeSetup> {
        val importResolution = when (val result = resolveImports(fixture, store, module, test)) {
            is RunnerResult.Success -> result.value
            is RunnerResult.Error -> return RunnerResult.Error(result.error)
        }

        return instance(store, module, importResolution.imports, config.runtimeConfig).fold(
            { instance ->
                importResolution.emscriptenFinalizers.forEach { finalizer ->
                    finalizer.finalize(instance)
                }
                RunnerResult.Success(
                    RuntimeSetup(
                        instance = instance,
                        importMemories = importResolution.memories,
                        importGlobals = importResolution.globals,
                        hosts = importResolution.hosts,
                        stdout = importResolution.stdout,
                        stderr = importResolution.stderr,
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
    ): RunnerResult<ImportResolution> {
        val imports = mutableListOf<Import>()
        val memories = mutableMapOf<String, Memory>()
        val globals = mutableMapOf<String, Global>()
        val hosts = mutableListOf<EmbedderHost>()
        val stdout = mutableListOf<Byte>()
        val stderr = mutableListOf<Byte>()
        val emscriptenFinalizers = mutableListOf<ChasmEmscriptenHostBuilder.ChasmEmscriptenSetupFinalizer>()
        val wasiImportModules = setOf(WASI_SNAPSHOT_PREVIEW_1, WASI_UNSTABLE)
        val hasWasiImports = module.imports.any { it.moduleName in wasiImportModules }
        val hasEmscriptenImports = module.imports.any { it.moduleName == EMSCRIPTEN_ENV }

        val wasiHost = test?.host?.wasiPreview1
        if (wasiHost == null && hasWasiImports) {
            return RunnerResult.Error(CorpusResult.Skipped(fixture.name, "WASI Preview 1 imports require fixture host config"))
        }

        val wasi = if (wasiHost != null && (hasWasiImports || hasEmscriptenImports)) {
            createWasiHost(wasiHost, stdout, stderr)
        } else if (hasEmscriptenImports) {
            createWasiHost(FixtureWasiPreview1Host(), stdout, stderr)
        } else {
            null
        }

        if (wasi != null) {
            hosts += wasi.host
        }

        if (wasi != null && hasWasiImports) {
            imports += ChasmWasiPreview1Builder(store) {
                host = wasi.host
            }.build()
        }

        if (wasi != null && hasEmscriptenImports) {
            val emscriptenBuilder = ChasmEmscriptenHostBuilder(store) {
                host = wasi.host
                memoryProvider = { memories.getValue("$EMSCRIPTEN_ENV.memory") }
            }
            val finalizer = emscriptenBuilder.setupEmscriptenFunctions(EMSCRIPTEN_ENV)
            emscriptenFinalizers += finalizer
            imports += finalizer.emscriptenFunctions
        }

        val providedImports = imports.map { import -> import.moduleName to import.entityName }.toMutableSet()

        module.imports.forEach { definition ->
            if (definition.moduleName in wasiImportModules) return@forEach
            if ((definition.moduleName to definition.entityName) in providedImports) return@forEach

            when (val type = definition.type) {
                is ExternalType.Function -> {
                    val fixtureImport = fixture.findImport(definition.moduleName, definition.entityName)
                    imports += Import(
                        definition.moduleName,
                        definition.entityName,
                        function(store, type.functionType) { _ ->
                            fixtureImport?.stub?.trap?.let(::error)
                            defaultResults(type.functionType, fixtureImport)
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
            ImportResolution(imports, memories, globals, hosts, stdout, stderr, emscriptenFinalizers),
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
                    override fun generateEntropy(length: Int): ByteArray = ByteArray(length)
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
        store: Store,
        setup: RuntimeSetup,
        captures: MutableMap<String, ExecutionValue>,
        step: JsonObject,
    ): CorpusResult {
        return when (val type = step.string("type")) {
            "function.invoke" -> runFunctionInvoke(fixture, context, store, setup, captures, step)
            "memory.write" -> runMemoryWrite(fixture, context, store, setup, captures, step)
            "memory.read" -> runMemoryRead(fixture, context, store, setup, captures, step)
            "global.write" -> runGlobalWrite(fixture, context, store, setup, captures, step)
            "global.read" -> runGlobalRead(fixture, context, store, setup, captures, step)
            "table.write", "table.read" -> CorpusResult.Skipped(fixture.name, "$type steps are not supported yet")
            else -> failure(fixture, context, "unsupported corpus step", type)
        }
    }

    private fun runFunctionInvoke(
        fixture: Fixture,
        context: String,
        store: Store,
        setup: RuntimeSetup,
        captures: MutableMap<String, ExecutionValue>,
        step: JsonObject,
    ): CorpusResult {
        val function = step.string("function")
        val functionType = setup.functionType(store, function)
        val params = step.array("params").mapIndexed { index, value ->
            parseValue(value.jsonObject, captures, functionType?.params?.types?.getOrNull(index))
        }
        val expected = step.arrayOrNull("results") ?: JsonArray(emptyList())
        val trap = step["trap"]?.jsonPrimitive?.contentOrNull
        val hostEffects = step["host_effects"]?.jsonObject
        val expectedExitCode = hostEffects?.get("exit_code")?.jsonPrimitive?.intOrNull

        val result = when (val invocation = invoke(store, setup.instance, function, params)) {
            is ChasmResult.Success -> invocation.result
            is ChasmResult.Error -> {
                val actualExitCode = invocation.error.toString().hostFunctionExitCodeOrNull()
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
            if (parsed.toLongFromBoxed() != actualValue.toLongFromBoxed()) {
                return failure(
                    fixture,
                    context,
                    "result mismatch",
                    expected = "result[$index]=${parsed.describeBits()}",
                    actual = "result[$index]=${actualValue.describeBits()}",
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

        return null
    }

    private fun runMemoryWrite(
        fixture: Fixture,
        context: String,
        store: Store,
        setup: RuntimeSetup,
        captures: MutableMap<String, ExecutionValue>,
        step: JsonObject,
    ): CorpusResult {
        val memory = resolveMemory(setup, step.stringOrDefault("memory", "memory"))
            ?: return failure(fixture, context, "memory not found", step["memory"]?.toString())
        val offset = parseIndex(step.getValue("offset"), captures)
        val bytes = step["bytes"]?.let(::decodeBytes) ?: step["value"]?.jsonObject?.let(::encodeValueBytes)
            ?: return failure(fixture, context, "memory write requires bytes or value")

        return writeBytes(store, memory, offset, bytes).fold(
            { CorpusResult.Success },
            { error -> failure(fixture, context, "memory write failed", error.toString()) },
        )
    }

    private fun runMemoryRead(
        fixture: Fixture,
        context: String,
        store: Store,
        setup: RuntimeSetup,
        captures: MutableMap<String, ExecutionValue>,
        step: JsonObject,
    ): CorpusResult {
        val memory = resolveMemory(setup, step.stringOrDefault("memory", "memory"))
            ?: return failure(fixture, context, "memory not found", step["memory"]?.toString())
        val offset = parseIndex(step.getValue("offset"), captures)
        val capture = step["capture"]?.jsonObject
        if (capture != null) {
            val value = readMemoryValue(fixture, context, store, memory, offset, capture.string("type"))
                ?: return failure(fixture, context, "memory capture failed")
            captures[capture.string("capture")] = value
            return CorpusResult.Success
        }

        val assertion = step["assert"]?.jsonObject ?: return failure(fixture, context, "memory read requires assert or capture")
        val expected = assertion["bytes"]?.let(::decodeBytes)
            ?: assertion["value"]?.jsonObject?.let(::encodeValueBytes)
            ?: return failure(fixture, context, "unsupported memory assertion")
        val buffer = ByteArray(expected.size)

        val actual = when (val result = readBytes(store, memory, buffer, offset, expected.size)) {
            is ChasmResult.Success -> result.result
            is ChasmResult.Error -> return failure(fixture, context, "memory read failed", result.error.toString())
        }

        return if (expected.contentEquals(actual)) {
            CorpusResult.Success
        } else {
            failure(fixture, context, "memory mismatch", expected = expected.toHex(), actual = actual.toHex())
        }
    }

    private fun runGlobalWrite(
        fixture: Fixture,
        context: String,
        store: Store,
        setup: RuntimeSetup,
        captures: MutableMap<String, ExecutionValue>,
        step: JsonObject,
    ): CorpusResult {
        val global = resolveGlobal(setup, step.string("global"))
            ?: return failure(fixture, context, "global not found", step.string("global"))
        val value = parseValue(step.getValue("value").jsonObject, captures)

        return writeGlobal(store, global, value).fold(
            { CorpusResult.Success },
            { error -> failure(fixture, context, "global write failed", error.toString()) },
        )
    }

    private fun runGlobalRead(
        fixture: Fixture,
        context: String,
        store: Store,
        setup: RuntimeSetup,
        captures: MutableMap<String, ExecutionValue>,
        step: JsonObject,
    ): CorpusResult {
        val global = resolveGlobal(setup, step.string("global"))
            ?: return failure(fixture, context, "global not found", step.string("global"))
        val actual = when (val result = readGlobal(store, global)) {
            is ChasmResult.Success -> result.result
            is ChasmResult.Error -> return failure(fixture, context, "global read failed", result.error.toString())
        }
        val expected = parseValue(step.getValue("assert").jsonObject.getValue("value").jsonObject, captures)

        return if (expected.toLongFromBoxed() == actual.toLongFromBoxed()) {
            CorpusResult.Success
        } else {
            failure(fixture, context, "global mismatch", expected = expected.describeBits(), actual = actual.describeBits())
        }
    }

    private fun resolveMemory(
        setup: RuntimeSetup,
        name: String,
    ): Memory? {
        setup.instance.exports.firstOrNull { export -> export.name == name }?.value?.let { value ->
            return value as? Memory
        }
        if (name == "env.memory") return setup.importMemories[name]
        if (name == "memory") {
            setup.instance.exports.firstOrNull { export -> export.value is Memory }?.value?.let { value ->
                return value as Memory
            }
        }
        return setup.importMemories[name]
    }

    private fun resolveGlobal(
        setup: RuntimeSetup,
        name: String,
    ): Global? {
        setup.instance.exports.firstOrNull { export -> export.name == name }?.value?.let { value ->
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
        is ValueType.Number -> when (type.numberType.name) {
            "I32" -> NumberValue.I32(0)
            "I64" -> NumberValue.I64(0)
            "F32" -> NumberValue.F32(0f)
            "F64" -> NumberValue.F64(0.0)
            else -> NumberValue.I32(0)
        }
        is ValueType.Reference -> ReferenceValue.Null(type.referenceType.heapType)
        else -> ExecutionValue.Uninitialised
    }

    private fun parseFixtureValue(value: io.github.charlietap.corpus.lib.fixture.FixtureValue): ExecutionValue {
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
                    ReferenceValue.I31(parsed.toUInt())
                } else {
                    NumberValue.I32(parsed)
                }
            }
            "i64" -> NumberValue.I64(raw?.let { it.toLongOrNull() ?: it.toULong().toLong() } ?: 0)
            "f32" -> NumberValue.F32(raw?.let { Float.fromBits(it.toUInt().toInt()) } ?: 0f)
            "f64" -> NumberValue.F64(raw?.let { Double.fromBits(it.toULong().toLong()) } ?: 0.0)
            "funcref" -> ReferenceValue.Null(AbstractHeapType.Func)
            "externref" -> ReferenceValue.Null(AbstractHeapType.Extern)
            else -> ExecutionValue.Uninitialised
        }
    }

    private fun readMemoryValue(
        fixture: Fixture,
        context: String,
        store: Store,
        memory: Memory,
        offset: Int,
        type: String,
    ): ExecutionValue? {
        val size = if (type == "i64" || type == "f64") 8 else 4
        val buffer = ByteArray(size)
        val bytes = when (val result = readBytes(store, memory, buffer, offset, size)) {
            is ChasmResult.Success -> result.result
            is ChasmResult.Error -> return null
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
        return captures.getValue(variable).toLongFromBoxed().toInt()
    }

    private fun encodeValueBytes(value: JsonObject): ByteArray {
        val parsed = parseValue(value, emptyMap()).toLongFromBoxed()
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

    private fun io.github.charlietap.chasm.embedding.shapes.ImportDefinition.importKey(): String {
        return "$moduleName.$entityName"
    }

    private fun JsonObject.string(name: String): String = getValue(name).jsonPrimitive.content

    private fun JsonObject.stringOrDefault(name: String, default: String): String {
        return this[name]?.jsonPrimitive?.contentOrNull ?: default
    }

    private fun JsonObject.array(name: String): JsonArray = arrayOrNull(name) ?: JsonArray(emptyList())

    private fun JsonObject.arrayOrNull(name: String): JsonArray? = this[name]?.jsonArray

    private fun JsonElement.jsonPrimitiveOrNull(): JsonPrimitive? = this as? JsonPrimitive

    private fun ExecutionValue.describeBits(): String = "bits=${toLongFromBoxed()}"

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

    private fun String.hostFunctionExitCodeOrNull(): Int? {
        val prefix = "ExecutionError(error=HostFunctionError(error="
        val start = indexOf(prefix).takeIf { it >= 0 } ?: return null
        val valueStart = start + prefix.length
        val valueEnd = indexOf(")", valueStart).takeIf { it >= 0 } ?: return null
        return substring(valueStart, valueEnd).toIntOrNull()
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
        rerun = "./gradlew :chasm:jvmTest --tests '*${fixture.name}*'",
    )

    private data class ImportResolution(
        val imports: List<Import>,
        val memories: Map<String, Memory>,
        val globals: Map<String, Global>,
        val hosts: List<EmbedderHost>,
        val stdout: MutableList<Byte>,
        val stderr: MutableList<Byte>,
        val emscriptenFinalizers: List<ChasmEmscriptenHostBuilder.ChasmEmscriptenSetupFinalizer>,
    )

    private data class WasiRuntime(
        val host: EmbedderHost,
    )

    private data class RuntimeSetup(
        val instance: io.github.charlietap.chasm.embedding.shapes.Instance,
        val importMemories: Map<String, Memory>,
        val importGlobals: Map<String, Global>,
        val hosts: List<EmbedderHost>,
        val stdout: MutableList<Byte>,
        val stderr: MutableList<Byte>,
    ) {
        fun functionType(
            store: Store,
            name: String,
        ): FunctionType? {
            val address = instance.instance.exports
                .firstOrNull { export -> export.name.name == name }
                ?.value
                ?.let { value -> (value as? ExternalValue.Function)?.address }
                ?: return null

            return store.store.functions.getOrNull(address.address)?.functionType
        }

        fun close() {
            hosts.forEach(EmbedderHost::close)
        }
    }

    private sealed interface RunnerResult<out T> {
        data class Success<T>(val value: T) : RunnerResult<T>

        data class Error(val error: CorpusResult) : RunnerResult<Nothing>
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
        const val EMSCRIPTEN_ENV = "env"
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
