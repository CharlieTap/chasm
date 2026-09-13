package io.github.charlietap.chasm.tools.aot

import io.github.charlietap.chasm.compiler.kotlin.JvmKotlinProgramCompiler
import io.github.charlietap.chasm.compiler.kotlin.KotlinCompilationMode
import io.github.charlietap.chasm.compiler.kotlin.KotlinCompilationReport
import io.github.charlietap.chasm.corpus.ChasmCorpusRunner
import io.github.charlietap.chasm.embedding.dropStore
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.corpus.lib.CorpusFileReader
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.fixture.Fixture
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.io.File

internal val json = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
}

fun main(args: Array<String>) {
    require(args.isNotEmpty()) { "Commands: corpus, coremark. Options: --mode interpreter|prepare|cached --artifacts DIR --report FILE" }
    require((args.size - 1) % 2 == 0) { "Options must be --name value pairs" }
    val options = args.drop(1).chunked(2).associate { (name, value) -> name.removePrefix("--") to value }
    when (args[0]) {
        "corpus" -> runCorpus(options)
        "coremark" -> runCoremark(options)
        else -> error("Unknown command ${args[0]}")
    }
}

internal fun backend(options: Map<String, String>, reports: MutableList<KotlinCompilationReport>, count: Boolean): JvmKotlinProgramCompiler? {
    val mode = options["mode"] ?: "cached"
    if (mode == "interpreter") return null
    return JvmKotlinProgramCompiler(
        directory = File(options.getValue("artifacts")),
        mode = when (mode) {
            "prepare" -> KotlinCompilationMode.PREPARE
            "cached" -> KotlinCompilationMode.CACHED
            else -> error("Unknown mode $mode")
        },
        countExecutions = count,
        onCompilation = reports::add,
    )
}

private fun runCorpus(options: Map<String, String>) {
    val root = File(options.getValue("root")).canonicalFile
    val fixtures = json.decodeFromString<List<Fixture>>(File(options.getValue("index")).readText())
        .filter { it.version == "1.0" && (options["filter"]?.let(it.name::contains) ?: true) }
    require(fixtures.isNotEmpty()) { "No Wasm 1.0 fixtures selected" }
    val compilations = mutableListOf<KotlinCompilationReport>()
    val compiler = backend(options, compilations, count = true)
    val stores = mutableListOf<Store>()
    val runner = ChasmCorpusRunner(
        fileReader = object : CorpusFileReader {
            override fun readText(path: String) = File(path).readText()

            override fun readBytes(path: String) = File(path).readBytes()
        },
        storeFactory = { (compiler?.let { store(it) } ?: store()).also(stores::add) },
    )
    val results = mutableListOf<JsonObject>()
    try {
        for ((index, fixture) in fixtures.withIndex()) {
            val previousCompilations = compilations.size
            val blocksBefore = compiler?.generatedBlockExecutions ?: 0
            val instructionsBefore = compiler?.generatedInstructionExecutions ?: 0
            val result = try {
                runner.execute(root.path, fixture, CorpusPhase.INVOCATION)
            } finally {
                stores.forEach { dropStore(it) }
                stores.clear()
            }
            val status = when (result.result) {
                CorpusResult.Success -> "passed"
                is CorpusResult.Failure -> "failed"
                is CorpusResult.Skipped -> "skipped"
            }
            val newCompilations = compilations.subList(previousCompilations, compilations.size)
            results.add(
                JsonObject(
                    mapOf(
                        "name" to JsonPrimitive(fixture.name),
                        "version" to JsonPrimitive(fixture.version),
                        "sha256" to JsonPrimitive(fixture.sha256),
                        "status" to JsonPrimitive(status),
                        "detail" to JsonPrimitive(result.result.toString()),
                        "tests" to JsonPrimitive(fixture.tests.size),
                        "steps" to JsonPrimitive(fixture.tests.sumOf { it.steps.size }),
                        "moduleBuilds" to JsonPrimitive(result.moduleBuildCount),
                        "generatedModules" to JsonPrimitive(newCompilations.size),
                        "generatedBlocksExecuted" to JsonPrimitive((compiler?.generatedBlockExecutions ?: 0) - blocksBefore),
                        "generatedInstructionsExecuted" to JsonPrimitive((compiler?.generatedInstructionExecutions ?: 0) - instructionsBefore),
                        "totalNanos" to JsonPrimitive(result.timings.totalNanos),
                        "executeNanos" to JsonPrimitive(result.timings.executeNanos),
                        "compilations" to JsonArray(newCompilations.map(::compilationJson)),
                    ),
                ),
            )
            writeReport(
                options,
                JsonObject(
                    mapOf(
                        "mode" to JsonPrimitive(options["mode"] ?: "cached"),
                        "version" to JsonPrimitive("1.0"),
                        "selected" to JsonPrimitive(fixtures.size),
                        "results" to JsonArray(results),
                    ),
                ),
            )
            println("${index + 1}/${fixtures.size} $status ${fixture.name}: generated=${newCompilations.sumOf { it.generatedInstructionCount }} blocksExecuted=${(compiler?.generatedBlockExecutions ?: 0) - blocksBefore}")
            if (status != "passed") println(result.result)
        }
        check(results.all { it["status"] == JsonPrimitive("passed") }) { "Corpus had failures or skips; see ${options["report"]}" }
        println("PASS: ${fixtures.size} Wasm 1.0 fixtures; generated block executions=${compiler?.generatedBlockExecutions ?: 0}")
    } finally {
        compiler?.close()
    }
}

internal fun compilationJson(report: KotlinCompilationReport): JsonObject = JsonObject(
    mapOf(
        "key" to JsonPrimitive(report.key),
        "instructions" to JsonPrimitive(report.instructionCount),
        "generatedInstructions" to JsonPrimitive(report.generatedInstructionCount),
        "controlInstructions" to JsonPrimitive(report.controlInstructionCount),
        "blocks" to JsonPrimitive(report.blockCount),
        "classes" to JsonPrimitive(report.classCount),
        "sourceBytes" to JsonPrimitive(report.sourceBytes),
        "cacheHit" to JsonPrimitive(report.cacheHit),
        "compilationNanos" to JsonPrimitive(report.compilationNanos),
    ),
)

internal fun writeReport(options: Map<String, String>, report: JsonObject) {
    File(options.getValue("report")).apply {
        parentFile?.mkdirs()
        writeText(json.encodeToString(JsonObject.serializer(), report))
    }
}
