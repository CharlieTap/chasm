package io.github.charlietap.chasm.benchmark.embedding

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.benchmark.BenchmarkConfig
import io.github.charlietap.chasm.benchmark.StabilizedBenchmark
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.host.readI32
import io.github.charlietap.chasm.host.writeI32
import io.github.charlietap.chasm.runtime.value.NumberValue
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.ValueType
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Measurement
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import kotlinx.benchmark.Warmup

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@Warmup(iterations = BenchmarkConfig.WARMUP_ITERATIONS, time = BenchmarkConfig.ITERATION_TIME)
@Measurement(iterations = BenchmarkConfig.MEASUREMENT_ITERATIONS, time = BenchmarkConfig.ITERATION_TIME)
class HostFunctionBenchmark : StabilizedBenchmark() {

    private val store = store()
    private var hostAccumulator = 0
    private val imports = listOf(
        Import(
            "bench",
            "noop",
            function(store, functionType()) { _, _ -> },
        ),
        Import(
            "bench",
            "consume4",
            function(store, functionType(I32, I32, I32, I32)) { parameters, _ ->
                hostAccumulator += parameters.readI32(0) +
                    parameters.readI32(1) +
                    parameters.readI32(2) +
                    parameters.readI32(3)
            },
        ),
        Import(
            "bench",
            "roundtrip2",
            function(store, functionType(I32, I32, results = listOf(I32))) { parameters, results ->
                results.writeI32(0, parameters.readI32(0) + parameters.readI32(1))
            },
        ),
    )
    private val instance = module(Resource("benchmark/host-function.wasm").readBytes())
        .flatMap { instance(store, it, imports) }
        .expect("Failed to instantiate host-function benchmark module")
    private val arguments = listOf(NumberValue.I32(CALL_COUNT))

    @Benchmark
    fun baselineNoop(blackhole: Blackhole) {
        blackhole.consume(invokeExport("baseline_noop"))
    }

    @Benchmark
    fun hostNoop(blackhole: Blackhole) {
        blackhole.consume(invokeExport("host_noop"))
    }

    @Benchmark
    fun wasmNoop(blackhole: Blackhole) {
        blackhole.consume(invokeExport("wasm_noop"))
    }

    @Benchmark
    fun wasmIdentity1(blackhole: Blackhole) {
        blackhole.consume(invokeExport("wasm_identity1"))
    }

    @Benchmark
    fun wasmSum3(blackhole: Blackhole) {
        blackhole.consume(invokeExport("wasm_sum3"))
    }

    @Benchmark
    fun wasmSum4(blackhole: Blackhole) {
        blackhole.consume(invokeExport("wasm_sum4"))
    }

    @Benchmark
    fun baselineConsume4(blackhole: Blackhole) {
        blackhole.consume(invokeExport("baseline_consume4"))
    }

    @Benchmark
    fun hostConsume4(blackhole: Blackhole) {
        blackhole.consume(invokeExport("host_consume4"))
        blackhole.consume(hostAccumulator)
    }

    @Benchmark
    fun baselineRoundtrip2(blackhole: Blackhole) {
        blackhole.consume(invokeExport("baseline_roundtrip2"))
    }

    @Benchmark
    fun hostRoundtrip2(blackhole: Blackhole) {
        blackhole.consume(invokeExport("host_roundtrip2"))
    }

    @Benchmark
    fun wasmRoundtrip2(blackhole: Blackhole) {
        blackhole.consume(invokeExport("wasm_roundtrip2"))
    }

    private fun invokeExport(name: String): Int =
        (invoke(store, instance, name, arguments).expect("Failed to invoke $name").first() as NumberValue.I32).value

    private companion object {
        const val CALL_COUNT = 10_000
        val I32 = ValueType.Number(NumberType.I32)

        fun functionType(
            vararg parameters: ValueType,
            results: List<ValueType> = emptyList(),
        ) = FunctionType(ResultType(parameters.toList()), ResultType(results))
    }
}
