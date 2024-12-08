package io.github.charlietap.chasm.benchmark.instruction.memory

import io.github.charlietap.chasm.benchmark.BenchmarkConfig
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.instruction.ExecutionInstructionExecutor
import io.github.charlietap.chasm.executor.memory.factory.LinearMemoryFactory
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.frameState
import io.github.charlietap.chasm.fixture.instance.dataAddress
import io.github.charlietap.chasm.fixture.instance.dataInstance
import io.github.charlietap.chasm.fixture.instance.memoryAddress
import io.github.charlietap.chasm.fixture.instance.memoryInstance
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.instruction.memoryInitInstruction
import io.github.charlietap.chasm.fixture.instruction.moduleInstruction
import io.github.charlietap.chasm.fixture.module.dataIndex
import io.github.charlietap.chasm.fixture.module.memoryIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.unsharedStatus
import io.github.charlietap.chasm.fixture.value
import io.github.charlietap.chasm.fixture.value.i32
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
@Warmup(iterations = BenchmarkConfig.WARMUP_ITERATIONS, time = BenchmarkConfig.ITERATION_TIME)
@Measurement(iterations = BenchmarkConfig.MEASUREMENT_ITERATIONS, time = BenchmarkConfig.ITERATION_TIME)
class MemoryInitInstructionBenchmark {

    private val context = ExecutionContext(
        stack = stack(),
        store = store(),
        instance = moduleInstance(),
    )

    private val instruction = moduleInstruction(
        memoryInitInstruction(
            memoryIndex = memoryIndex(0u),
            dataIdx = dataIndex(0u),
        ),
    )

    private val memoryInstance = memoryInstance(
        type = memoryType(
            limits = limits(1u),
            shared = unsharedStatus(),
        ),
        data = LinearMemoryFactory(1),
    )

    private val dataInstance = dataInstance(
        UByteArray(1000) { i -> 117u },
    )

    private val frame = frame(
        state = frameState(
            moduleInstance = context.instance,
        ),
    )

    private val srcOffset = value(i32(0))
    private val destOffset = value(i32(0))
    private val bytesToCopy = value(i32(200))

    @Setup
    fun setup() {
        context.apply {
            instance.memAddresses.add(0, memoryAddress(0))
            instance.dataAddresses.add(0, dataAddress(0))
            stack.push(frame)
            store.memories.add(0, memoryInstance)
            store.data.add(0, dataInstance)
        }
    }

    @TearDown
    fun cleanup() {
        context.stack.clear()
        context.store.memories.clear()
    }

    @Benchmark
    fun benchmark(blackhole: Blackhole) {
        context.stack.push(destOffset)
        context.stack.push(srcOffset)
        context.stack.push(bytesToCopy)
        val result = ExecutionInstructionExecutor(context, instruction)
        context.stack.clearValues()
        blackhole.consume(result)
    }
}
