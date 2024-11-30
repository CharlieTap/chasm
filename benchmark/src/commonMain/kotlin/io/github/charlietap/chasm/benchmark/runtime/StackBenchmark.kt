package io.github.charlietap.chasm.benchmark.runtime

import io.github.charlietap.chasm.benchmark.BenchmarkConfig
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.peekLabel
import io.github.charlietap.chasm.executor.runtime.ext.peekValue
import io.github.charlietap.chasm.executor.runtime.ext.popFrame
import io.github.charlietap.chasm.executor.runtime.ext.popLabel
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.label
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.value
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

    private val stack = stack()

    private val frame = frame()
    private val label = label()
    private val value = value()

    @TearDown
    fun cleanup() {
        stack.clear()
    }

    @Benchmark
    fun peekFrame(blackhole: Blackhole) {
        stack.push(frame)
        val result = stack.peekFrame()
        stack.clearFrames()
        blackhole.consume(result)
    }

    @Benchmark
    fun pushFrame(blackhole: Blackhole) {
        val result = stack.push(frame)
        stack.clearFrames()
        blackhole.consume(result)
    }

    @Benchmark
    fun popFrame(blackhole: Blackhole) {
        stack.push(frame)
        val result = stack.popFrame()
        stack.clearFrames()
        blackhole.consume(result)
    }

    @Benchmark
    fun peekLabel(blackhole: Blackhole) {
        stack.push(label)
        val result = stack.peekLabel()
        stack.clearLabels()
        blackhole.consume(result)
    }

    @Benchmark
    fun pushLabel(blackhole: Blackhole) {
        val result = stack.push(label)
        stack.clearLabels()
        blackhole.consume(result)
    }

    @Benchmark
    fun popLabel(blackhole: Blackhole) {
        stack.push(label)
        val result = stack.popLabel()
        stack.clearLabels()
        blackhole.consume(result)
    }

    @Benchmark
    fun peekValue(blackhole: Blackhole) {
        stack.push(value)
        val result = stack.peekValue()
        stack.clearValues()
        blackhole.consume(result)
    }

    @Benchmark
    fun pushValue(blackhole: Blackhole) {
        val result = stack.push(value)
        stack.clearValues()
        blackhole.consume(result)
    }

    @Benchmark
    fun popValue(blackhole: Blackhole) {
        stack.push(value)
        val result = stack.popValue()
        stack.clearValues()
        blackhole.consume(result)
    }
}
