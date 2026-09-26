package io.github.charlietap.chasm.benchmark.coremark

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.benchmark.BenchmarkMode
import io.github.charlietap.chasm.benchmark.BenchmarkStabilizer
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.addFuel
import io.github.charlietap.chasm.embedding.dsl.imports
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.host.writeI64
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.NumberValue
import kotlin.time.Clock

/** Takes an optional store configuration: `plain` (the default), `fuel`, `interrupt`, or `fuel+interrupt`. */
fun main(args: Array<String>) {
    val config = when (val name = args.firstOrNull() ?: "plain") {
        "plain" -> StoreConfig()
        "fuel" -> StoreConfig(meterFuel = true)
        "interrupt" -> StoreConfig(interruptible = true)
        "fuel+interrupt" -> StoreConfig(meterFuel = true, interruptible = true)
        else -> error("Unknown store configuration: $name")
    }
    val topology = BenchmarkStabilizer.topology()
    val application = BenchmarkStabilizer.enter(BenchmarkMode.PREFER_FASTEST, topology)
    val effectiveTopology = if (application.isSupported) topology else topology.copy(fastestCpuIds = emptySet())
    val start = BenchmarkStabilizer.awaitFastestCore(topology = effectiveTopology)
    check(application.canProceed) {
        application.message ?: "Could not request fastest-core placement"
    }
    check(!effectiveTopology.isPlacementSupported || start.isFastest == true) {
        "CoreMark started on logical CPU ${start.cpuId}, outside the fastest class"
    }

    val benchmark = CoremarkBenchmark(config)
    benchmark.run()

    val placement = BenchmarkStabilizer.finishTrial(start, effectiveTopology)
    check(placement.isValid) {
        "CoreMark placement was invalid: ${placement.start.cpuId} -> ${placement.end.cpuId}"
    }
}

class CoremarkBenchmark(private val config: StoreConfig = StoreConfig()) {

    fun run() {
        val bytes = Resource(FILE_DIR + "coremark.wasm").readBytes()
        val store = store(config)
        if (config.meterFuel) addFuel(store, Long.MAX_VALUE)

        val imports = imports(store) {
            function {
                moduleName = "env"
                entityName = "clock_ms"
                type {
                    results { i64() }
                }

                reference { _, results ->
                    val time = Clock.System.now()
                    results.writeI64(0, time.toEpochMilliseconds())
                }
            }
        }

        val results = module(bytes)
            .flatMap { module ->
                instance(store, module, imports)
            }.flatMap { instance ->
                invoke(store, instance, "run")
            }

        if (results is ChasmResult.Success<List<ExecutionValue>>) {
            val score = results.result.first() as NumberValue.F32
            println("CoreMark 1.0 : ${score.value}")
        } else {
            println("Benchmark failed with error: $results")
        }
    }

    companion object {
        private const val FILE_DIR = "benchmark/"
    }
}
