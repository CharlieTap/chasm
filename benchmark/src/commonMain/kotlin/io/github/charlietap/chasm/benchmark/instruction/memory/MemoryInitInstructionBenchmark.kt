package io.github.charlietap.chasm.benchmark.instruction.memory

import io.github.charlietap.chasm.benchmark.BenchmarkConfig
import io.github.charlietap.chasm.executor.invoker.instruction.memory.MemoryInitExecutor
import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.instance.dataAddress
import io.github.charlietap.chasm.fixture.runtime.instance.dataInstance
import io.github.charlietap.chasm.fixture.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instruction.memoryInitRuntimeInstruction
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.frame
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.unsharedStatus
import io.github.charlietap.chasm.memory.factory.LinearMemoryFactory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
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

    private val context = executionContext(
        cstack = cstack(),
        store = store(),
        instance = moduleInstance(),
    )

    private val memoryInstance = memoryInstance(
        type = memoryType(
            limits = limits(1u),
            shared = unsharedStatus(),
        ),
        data = LinearMemoryFactory(LinearMemory.Pages(1u)),
    )

    private val dataInstance = dataInstance(
        UByteArray(1000) { i -> 117u },
    )

    private val instruction = memoryInitRuntimeInstruction(
        memory = memoryInstance,
        data = dataInstance,
    )

    private val frame = frame(
        instance = context.instance,
    )

    private val srcOffset = 0
    private val destOffset = 0
    private val bytesToCopy = 200

    @Setup
    fun setup() {
        context.apply {
            instance.memAddresses.add(0, memoryAddress(0))
            instance.dataAddresses.add(0, dataAddress(0))
            cstack.push(frame)
            store.memories.add(0, memoryInstance)
            store.data.add(0, dataInstance)
        }
    }

    @TearDown
    fun cleanup() {
        context.cstack.clear()
        context.store.memories.clear()
    }

    @Benchmark
    fun benchmark(blackhole: Blackhole) {
        context.vstack.pushI32(destOffset)
        context.vstack.pushI32(srcOffset)
        context.vstack.pushI32(bytesToCopy)
        val result = MemoryInitExecutor(context, instruction)
        context.vstack.clear()
        blackhole.consume(result)
    }
}
