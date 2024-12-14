package io.github.charlietap.chasm.benchmark.instruction.memory

import io.github.charlietap.chasm.benchmark.BenchmarkConfig
import io.github.charlietap.chasm.executor.invoker.instruction.memory.MemoryCopyExecutor
import io.github.charlietap.chasm.executor.memory.factory.LinearMemoryFactory
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import io.github.charlietap.chasm.fixture.ast.module.memoryIndex
import io.github.charlietap.chasm.fixture.ast.type.limits
import io.github.charlietap.chasm.fixture.ast.type.memoryType
import io.github.charlietap.chasm.fixture.ast.type.unsharedStatus
import io.github.charlietap.chasm.fixture.executor.runtime.frame
import io.github.charlietap.chasm.fixture.executor.runtime.frameState
import io.github.charlietap.chasm.fixture.executor.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instruction.memoryCopyRuntimeInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.value
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
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
class MemoryCopyInstructionBenchmark {

    private val context = ExecutionContext(
        stack = stack(),
        store = store(),
        instance = moduleInstance(),
    )

    private val instruction = memoryCopyRuntimeInstruction(
        srcMemoryIndex = memoryIndex(0u),
        dstMemoryIndex = memoryIndex(0u),
    )

    private val memoryInstance = memoryInstance(
        type = memoryType(
            limits = limits(1u),
            shared = unsharedStatus(),
        ),
        data = LinearMemoryFactory(LinearMemory.Pages(1u)),
    )

    private val frame = frame(
        state = frameState(
            moduleInstance = context.instance,
        ),
    )

    private val srcOffset = value(i32(0))
    private val dstOffset = value(i32(0))
    private val bytesToCopy = value(i32(200))

    @Setup
    fun setup() {
        context.apply {
            instance.memAddresses.add(0, memoryAddress(0))
            stack.push(frame)
            store.memories.add(0, memoryInstance)
        }
    }

    @TearDown
    fun cleanup() {
        context.stack.clear()
        context.store.memories.clear()
    }

    @Benchmark
    fun benchmark(blackhole: Blackhole) {
        context.stack.push(dstOffset)
        context.stack.push(srcOffset)
        context.stack.push(bytesToCopy)
        val result = MemoryCopyExecutor(context, instruction)
        context.stack.clearValues()
        blackhole.consume(result)
    }
}
