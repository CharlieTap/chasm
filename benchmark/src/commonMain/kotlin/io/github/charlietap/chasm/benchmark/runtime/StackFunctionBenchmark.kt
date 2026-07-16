package io.github.charlietap.chasm.benchmark.runtime

import io.github.charlietap.chasm.benchmark.BenchmarkConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.instantiator.allocation.function.StackFunctionAllocator
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.function.StackFunctionBody
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
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
class StackFunctionBenchmark {

    private val config = RuntimeConfig()
    private val store: Store = store()
    private val instance: ModuleInstance = moduleInstance()
    private var function = Address.Function(0)

    @Setup
    fun setup() {
        val body = StackFunctionBody { _, _, _, _ -> }
        val external = StackFunctionAllocator(store, functionType(), body)
        function = external.address
    }

    @Benchmark
    fun invoke(blackhole: Blackhole) {
        val result = FunctionInvoker(config, store, instance, function, emptyList())
        blackhole.consume(result)
    }
}
