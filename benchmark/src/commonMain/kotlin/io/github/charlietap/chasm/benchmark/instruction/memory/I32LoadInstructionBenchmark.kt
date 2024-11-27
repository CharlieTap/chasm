package io.github.charlietap.chasm.benchmark.instruction.memory

import io.github.charlietap.chasm.benchmark.BenchmarkConfig
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.instruction.ExecutionInstructionExecutor
import io.github.charlietap.chasm.executor.memory.factory.LinearMemoryFactoryImpl
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.frameState
import io.github.charlietap.chasm.fixture.instance.memoryAddress
import io.github.charlietap.chasm.fixture.instance.memoryInstance
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.instruction.i32LoadInstruction
import io.github.charlietap.chasm.fixture.instruction.memArg
import io.github.charlietap.chasm.fixture.instruction.moduleInstruction
import io.github.charlietap.chasm.fixture.module.memoryIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.unsharedStatus
import io.github.charlietap.chasm.fixture.value
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
import kotlinx.benchmark.TearDown
import kotlinx.benchmark.Warmup

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@Warmup(iterations = BenchmarkConfig.WARMUP_ITERATIONS, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@Measurement(iterations = BenchmarkConfig.MEASUREMENT_ITERATIONS, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
class I32LoadInstructionBenchmark {

    private val context = ExecutionContext(
        stack = stack(),
        store = store(),
        instance = moduleInstance(),
    )

    private val instruction = moduleInstruction(
        i32LoadInstruction(
            memoryIndex = memoryIndex(0u),
            memArg = memArg(0u, 0u),
        ),
    )

    @Setup()
    fun setup() {

        val memoryInstance = memoryInstance(
            type = memoryType(
                limits = limits(1u),
                shared = unsharedStatus(),
            ),
            data = LinearMemoryFactoryImpl(1, null),
        )

        val frame = frame(
            state = frameState(
                moduleInstance = context.instance,
            ),
        )

        context.apply {
            instance.memAddresses.add(0, memoryAddress(0))
            stack.push(frame)
            stack.push(value(NumberValue.I32(0)))
            store.memories.add(0, memoryInstance)
        }
    }

    @TearDown
    fun cleanup() {
        context.stack.empty()
        context.store.memories.clear()
    }

    @Benchmark
    fun benchmark(blackhole: Blackhole) {
        val result = ExecutionInstructionExecutor(context, instruction)
        blackhole.consume(result)
    }
}
