package io.github.charlietap.chasm.tools.aot

import io.github.charlietap.chasm.benchmark.BenchmarkMode
import io.github.charlietap.chasm.benchmark.BenchmarkStabilizer
import io.github.charlietap.chasm.compiler.kotlin.KotlinCompilationReport
import io.github.charlietap.chasm.embedding.dropStore
import io.github.charlietap.chasm.embedding.dsl.imports
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.memory.readBytes
import io.github.charlietap.chasm.embedding.memory.sizeMemory
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.host.writeI64
import io.github.charlietap.chasm.runtime.value.NumberValue
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.io.File
import java.security.MessageDigest

internal fun runCoremark(options: Map<String, String>) {
    val verify = options["verify"]?.toBooleanStrict() ?: false
    val reports = mutableListOf<KotlinCompilationReport>()
    val compiler = backend(options, reports, count = verify)
    val store = compiler?.let { store(it) } ?: store()
    var clockCalls = 0L
    try {
        val imports = imports(store) {
            function {
                moduleName = "env"
                entityName = "clock_ms"
                type { results { i64() } }
                reference { _, results ->
                    val tick = clockCalls++
                    results.writeI64(0, if (verify) tick * 10_000L else System.currentTimeMillis())
                }
            }
        }
        val preparationStart = System.nanoTime()
        val module = module(File(options.getValue("wasm")).readBytes()).expect("CoreMark decoding failed")
        val instance = instance(store, module, imports).expect("CoreMark instantiation failed")
        val preparationNanos = System.nanoTime() - preparationStart

        val topology = BenchmarkStabilizer.topology()
        val application = BenchmarkStabilizer.enter(BenchmarkMode.PREFER_FASTEST, topology)
        check(application.canProceed) { application.message ?: "CPU placement failed" }
        val effective = if (application.isSupported) topology else topology.copy(fastestCpuIds = emptySet())
        val start = BenchmarkStabilizer.awaitFastestCore(topology = effective)
        check(verify || !effective.isPlacementSupported || start.isFastest == true) { "CoreMark started outside the fastest CPU class" }
        val executionStart = System.nanoTime()
        val result = invoke(store, instance, "run").expect("CoreMark execution failed")
        val executionNanos = System.nanoTime() - executionStart
        val placement = BenchmarkStabilizer.finishTrial(start, effective)
        check(verify || placement.isValid) { "Invalid CPU placement: $placement" }
        val score = (result.single() as NumberValue.F32).value
        check(score.isFinite() && score > 0) { "CoreMark CRC/duration check failed: $score" }

        var memoryHash = ""
        var memoryBytes = 0L
        if (verify) {
            val digest = MessageDigest.getInstance("SHA-256")
            val memories = instance.exports.mapNotNull { it.value as? Memory }
            check(memories.isNotEmpty()) { "CoreMark must export memory for equivalence verification" }
            memories.forEach { memory ->
                val byteCount = sizeMemory(store, memory).expect("Memory size failed")
                check(byteCount > 0) { "CoreMark verification memory must be nonempty" }
                val buffer = ByteArray(byteCount)
                readBytes(store, memory, buffer, 0, buffer.size).expect("Memory read failed")
                digest.update(buffer)
                memoryBytes += buffer.size
            }
            memoryHash = digest.digest().joinToString("") { "%02x".format(it) }
        }
        val report = JsonObject(
            mapOf(
                "mode" to JsonPrimitive(options["mode"] ?: "cached"),
                "tier" to JsonPrimitive(options["tier"] ?: "TYPED"),
                "verify" to JsonPrimitive(verify),
                "score" to JsonPrimitive(score),
                "clockCalls" to JsonPrimitive(clockCalls),
                "memorySHA256" to JsonPrimitive(memoryHash),
                "memoryBytes" to JsonPrimitive(memoryBytes),
                "preparationNanos" to JsonPrimitive(preparationNanos),
                "executionNanos" to JsonPrimitive(executionNanos),
                "startCpu" to JsonPrimitive(placement.start.cpuId),
                "endCpu" to JsonPrimitive(placement.end.cpuId),
                "placementSupported" to JsonPrimitive(placement.isSupported),
                "placementValid" to JsonPrimitive(placement.isValid),
                "javaVersion" to JsonPrimitive(System.getProperty("java.version")),
                "generatedBlocksExecuted" to JsonPrimitive(compiler?.generatedBlockExecutions ?: 0),
                "generatedInstructionsExecuted" to JsonPrimitive(compiler?.generatedInstructionExecutions ?: 0),
                "compilations" to JsonArray(reports.map(::compilationJson)),
            ),
        )
        writeReport(options, report)
        println(json.encodeToString(JsonObject.serializer(), report))
    } finally {
        dropStore(store)
        compiler?.close()
    }
}
