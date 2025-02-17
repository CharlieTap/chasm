package io.github.charlietap.chasm.benchmark.instruction.memory

import io.github.charlietap.chasm.benchmark.BenchmarkConfig
import io.github.charlietap.chasm.executor.invoker.instruction.memory.MemoryFillExecutor
import io.github.charlietap.chasm.executor.memory.factory.LinearMemoryFactory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import io.github.charlietap.chasm.fixture.executor.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.executor.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instruction.memoryFillRuntimeInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.ir.type.limits
import io.github.charlietap.chasm.fixture.ir.type.memoryType
import io.github.charlietap.chasm.fixture.ir.type.unsharedStatus
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
class MemoryFillInstructionBenchmark {

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

    private val instruction = memoryFillRuntimeInstruction(
        memory = memoryInstance,
    )

    private val frame = frame(
        instance = context.instance,
    )

    private val offset = 0
    private val fillValue = 117
    private val bytesToFill = 200

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
        context.vstack.pushI32(offset)
        context.vstack.pushI32(fillValue)
        context.vstack.pushI32(bytesToFill)
        val result = MemoryFillExecutor(context, instruction)
        context.vstack.clear()
        blackhole.consume(result)
    }
}
