@file:OptIn(ExperimentalChasmApi::class)

package io.github.charlietap.chasm.benchmark.embedding

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.benchmark.BenchmarkConfig
import io.github.charlietap.chasm.benchmark.StabilizedBenchmark
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.invokeUnboxed
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.prepareFunction
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.embedding.value.NumberValue
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
import kotlinx.coroutines.runBlocking

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@Warmup(iterations = BenchmarkConfig.WARMUP_ITERATIONS, time = BenchmarkConfig.ITERATION_TIME)
@Measurement(iterations = BenchmarkConfig.MEASUREMENT_ITERATIONS, time = BenchmarkConfig.ITERATION_TIME)
class PreparedFunctionBenchmark : StabilizedBenchmark() {

    private val store = store()
    private val instance = runBlocking {
        instance(
            store = store,
            module = module(IDENTITY_MODULE).expect("Failed to decode benchmark module"),
            imports = emptyList(),
        )
    }.expect("Failed to instantiate benchmark module")
    private val preparedFunction = prepareFunction(instance, FUNCTION_NAME)
        .expect("Failed to prepare benchmark function")
    private val arguments = listOf(NumberValue.I32(ARGUMENT))
    private val words = LongArray(1)
    private val references = arrayOfNulls<Any>(1)

    @Benchmark
    fun invokeByName(blackhole: Blackhole) {
        blackhole.consume(invoke(instance, FUNCTION_NAME, arguments))
    }

    @Benchmark
    fun invokePrepared(blackhole: Blackhole) {
        blackhole.consume(preparedFunction(arguments))
    }

    @Benchmark
    fun invokeUnboxed(blackhole: Blackhole) {

        words[0] = ARGUMENT.toLong()

        blackhole.consume(preparedFunction.invokeUnboxed(words, references))
        blackhole.consume(words[0])
    }

    @Benchmark
    fun prepare(blackhole: Blackhole) {
        blackhole.consume(prepareFunction(instance, FUNCTION_NAME))
    }

    private companion object {
        const val FUNCTION_NAME = "identity"
        const val ARGUMENT = 42

        // (module (func (export "identity") (param i32) (result i32) local.get 0))
        val IDENTITY_MODULE = byteArrayOf(
            0x00,
            0x61,
            0x73,
            0x6D,
            0x01,
            0x00,
            0x00,
            0x00,
            0x01,
            0x06,
            0x01,
            0x60,
            0x01,
            0x7F,
            0x01,
            0x7F,
            0x03,
            0x02,
            0x01,
            0x00,
            0x07,
            0x0C,
            0x01,
            0x08,
            0x69,
            0x64,
            0x65,
            0x6E,
            0x74,
            0x69,
            0x74,
            0x79,
            0x00,
            0x00,
            0x0A,
            0x06,
            0x01,
            0x04,
            0x00,
            0x20,
            0x00,
            0x0B,
        )
    }
}
