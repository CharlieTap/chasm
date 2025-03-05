package io.github.charlietap.chasm.benchmark.instruction.memory

import io.github.charlietap.chasm.benchmark.BenchmarkConfig
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.F32LoadExecutor
import io.github.charlietap.chasm.executor.memory.factory.LinearMemoryFactory
import io.github.charlietap.chasm.fixture.executor.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.executor.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instruction.f32LoadRuntimeInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.instruction.runtimeMemArg
import io.github.charlietap.chasm.fixture.executor.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.unsharedStatus
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
class F32LoadInstructionBenchmark {

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

    private val instruction = f32LoadRuntimeInstruction(
        memory = memoryInstance,
        memArg = runtimeMemArg(0, 0),
    )

    private val frame = frame(
        instance = context.instance,
    )

    private val baseAddress = 0

    @Setup
    fun setup() {
        context.apply {
            instance.memAddresses.add(0, memoryAddress(0))
            cstack.push(frame)
            store.memories.add(0, memoryInstance)
        }
    }

    @TearDown
    fun cleanup() {
        context.cstack.clear()
        context.store.memories.clear()
    }

    @Benchmark
    fun benchmark(blackhole: Blackhole) {
        context.vstack.pushI32(baseAddress)
        val result = F32LoadExecutor(context, instruction)
        context.vstack.clear()
        blackhole.consume(result)
    }
}
