package io.github.charlietap.chasm.benchmark.runtime

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.benchmark.BenchmarkConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.dropInstance
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.store
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Measurement
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.benchmark.Warmup

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@Warmup(iterations = BenchmarkConfig.WARMUP_ITERATIONS, time = BenchmarkConfig.ITERATION_TIME)
@Measurement(iterations = BenchmarkConfig.MEASUREMENT_ITERATIONS, time = BenchmarkConfig.ITERATION_TIME)
class CoreModuleBenchmark {

    private val config = RuntimeConfig()
    private val module: Module = module(Resource(MODULE_PATH).readBytes()).success()

    private lateinit var store: Store
    private lateinit var instance: Instance
    private lateinit var function: Function

    @Setup
    fun setup() {
        store = store()
        instance = instance(store, module, emptyList(), config).success()
        function = instance.exports.single().value as Function
    }

    @Benchmark
    fun instantiateCompiled(blackhole: Blackhole) {
        val result = instance(store(), module, emptyList(), config)
        blackhole.consume(result)
    }

    @Benchmark
    fun instantiateAndDropCompiledInSharedStore(blackhole: Blackhole) {
        val store = store()
        repeat(SHARED_STORE_BATCH_SIZE) {
            val instance = instance(store, module, emptyList(), config).success()
            dropInstance(store, instance).success()
        }
        blackhole.consume(store)
    }

    @Benchmark
    fun invoke(blackhole: Blackhole) {
        val result = invoke(store, instance, function)
        blackhole.consume(result)
    }
}

private fun <T> ChasmResult<T, *>.success(): T = when (this) {
    is ChasmResult.Success -> result
    is ChasmResult.Error -> error("Benchmark setup failed: $error")
}

private const val MODULE_PATH = "benchmark/nop.wasm"
private const val SHARED_STORE_BATCH_SIZE = 16
