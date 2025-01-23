package io.github.charlietap.chasm.benchmark.runtime

import io.github.charlietap.chasm.benchmark.BenchmarkConfig
import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame
import io.github.charlietap.chasm.fixture.executor.runtime.stack.label
import io.github.charlietap.chasm.fixture.executor.runtime.value.executionValue
import io.github.charlietap.chasm.stack.stackOf
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Measurement
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import kotlinx.benchmark.TearDown
import kotlinx.benchmark.Warmup

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@Warmup(iterations = BenchmarkConfig.WARMUP_ITERATIONS, time = BenchmarkConfig.ITERATION_TIME)
@Measurement(iterations = BenchmarkConfig.MEASUREMENT_ITERATIONS, time = BenchmarkConfig.ITERATION_TIME)
class StackBenchmark {

    private val stack = stackOf<Any>()

    private val frame = frame()
    private val label = label()
    private val value = executionValue()

    @TearDown
    fun cleanup() {
        stack.clear()
    }

    @Benchmark
    fun peekFrame(blackhole: Blackhole) {
        stack.push(frame)
        val result = stack.peekOrNull()
        stack.clear()
        blackhole.consume(result)
    }

    @Benchmark
    fun pushFrame(blackhole: Blackhole) {
        val result = stack.push(frame)
        stack.clear()
        blackhole.consume(result)
    }

    @Benchmark
    fun popFrame(blackhole: Blackhole) {
        stack.push(frame)
        val result = stack.popOrNull()
        stack.clear()
        blackhole.consume(result)
    }

    @Benchmark
    fun peekLabel(blackhole: Blackhole) {
        stack.push(label)
        val result = stack.peekOrNull()
        stack.clear()
        blackhole.consume(result)
    }

    @Benchmark
    fun pushLabel(blackhole: Blackhole) {
        val result = stack.push(label)
        stack.clear()
        blackhole.consume(result)
    }

    @Benchmark
    fun popLabel(blackhole: Blackhole) {
        stack.push(label)
        val result = stack.popOrNull()
        stack.clear()
        blackhole.consume(result)
    }

    @Benchmark
    fun peekValue(blackhole: Blackhole) {
        stack.push(value)
        val result = stack.peekOrNull()
        stack.clear()
        blackhole.consume(result)
    }

    @Benchmark
    fun pushValue(blackhole: Blackhole) {
        val result = stack.push(value)
        stack.clear()
        blackhole.consume(result)
    }

    @Benchmark
    fun popValue(blackhole: Blackhole) {
        stack.push(value)
        val result = stack.popOrNull()
        stack.clear()
        blackhole.consume(result)
    }
}
