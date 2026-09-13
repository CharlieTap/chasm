package io.github.charlietap.chasm.benchmark.memory

import io.github.charlietap.chasm.config.LinearMemoryConfig
import io.github.charlietap.chasm.memory.copy.LinearMemoryCopier
import io.github.charlietap.chasm.memory.destruct.LinearMemoryDestructor
import io.github.charlietap.chasm.memory.factory.LinearMemoryFactory
import io.github.charlietap.chasm.memory.fill.LinearMemoryFiller
import io.github.charlietap.chasm.memory.read.F32Reader
import io.github.charlietap.chasm.memory.read.F64Reader
import io.github.charlietap.chasm.memory.read.I32Reader
import io.github.charlietap.chasm.memory.read.I64Reader
import io.github.charlietap.chasm.memory.write.F32Writer
import io.github.charlietap.chasm.memory.write.F64Writer
import io.github.charlietap.chasm.memory.write.I32Writer
import io.github.charlietap.chasm.memory.write.I64Writer
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OperationsPerInvocation
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.TearDown
import org.openjdk.jmh.annotations.Warmup
import java.util.concurrent.TimeUnit

private const val ACCESS_BYTES = 16 * 1024 * 1024
private const val ACCESS_PAGES = ACCESS_BYTES / LinearMemory.PAGE_SIZE
private const val ACCESSES = 1_024
private const val ADDRESS_MASK = ACCESS_BYTES / Long.SIZE_BYTES - 1

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 300, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 3, time = 300, timeUnit = TimeUnit.MILLISECONDS)
open class LinearMemoryBenchmark {

    @State(Scope.Benchmark)
    class AccessState {
        lateinit var memory: LinearMemory
        var cursor = 0

        @JvmField
        @Param("0", "1")
        final var byteOffset = 0

        @Setup(Level.Trial)
        fun setup() {
            val pages = LinearMemory.Pages((ACCESS_PAGES + 1).toUInt())
            memory = LinearMemoryFactory(pages)
            LinearMemoryFiller(memory, 0, ACCESS_BYTES + 1, 0x5A, ACCESS_BYTES + 1)
        }

        @TearDown(Level.Trial)
        fun teardown() {
            LinearMemoryDestructor(memory)
        }
    }

    @Benchmark
    @OperationsPerInvocation(ACCESSES)
    open fun i32Load(state: AccessState): Long {
        var cursor = state.cursor
        var sum = 0L
        val memory = state.memory
        val offset = state.byteOffset
        repeat(ACCESSES) {
            cursor = cursor * 1_664_525 + 1_013_904_223
            sum += I32Reader(memory, ((cursor and ADDRESS_MASK) shl 3) + offset)
        }
        state.cursor = cursor
        return sum
    }

    @Benchmark
    @OperationsPerInvocation(ACCESSES)
    open fun i32Store(state: AccessState): Int {
        var cursor = state.cursor
        val memory = state.memory
        val offset = state.byteOffset
        repeat(ACCESSES) {
            cursor = cursor * 1_664_525 + 1_013_904_223
            I32Writer(memory, ((cursor and ADDRESS_MASK) shl 3) + offset, cursor)
        }
        state.cursor = cursor
        return cursor
    }

    @Benchmark
    @OperationsPerInvocation(ACCESSES)
    open fun i64Load(state: AccessState): Long {
        var cursor = state.cursor
        var sum = 0L
        val memory = state.memory
        val offset = state.byteOffset
        repeat(ACCESSES) {
            cursor = cursor * 1_664_525 + 1_013_904_223
            sum += I64Reader(memory, ((cursor and ADDRESS_MASK) shl 3) + offset)
        }
        state.cursor = cursor
        return sum
    }

    @Benchmark
    @OperationsPerInvocation(ACCESSES)
    open fun i64Store(state: AccessState): Int {
        var cursor = state.cursor
        val memory = state.memory
        val offset = state.byteOffset
        repeat(ACCESSES) {
            cursor = cursor * 1_664_525 + 1_013_904_223
            I64Writer(memory, ((cursor and ADDRESS_MASK) shl 3) + offset, cursor.toLong())
        }
        state.cursor = cursor
        return cursor
    }

    @Benchmark
    @OperationsPerInvocation(ACCESSES)
    open fun f32Load(state: AccessState): Float {
        var cursor = state.cursor
        var sum = 0f
        val memory = state.memory
        val offset = state.byteOffset
        repeat(ACCESSES) {
            cursor = cursor * 1_664_525 + 1_013_904_223
            sum += F32Reader(memory, ((cursor and ADDRESS_MASK) shl 3) + offset)
        }
        state.cursor = cursor
        return sum
    }

    @Benchmark
    @OperationsPerInvocation(ACCESSES)
    open fun f32Store(state: AccessState): Int {
        var cursor = state.cursor
        val memory = state.memory
        val offset = state.byteOffset
        repeat(ACCESSES) {
            cursor = cursor * 1_664_525 + 1_013_904_223
            F32Writer(memory, ((cursor and ADDRESS_MASK) shl 3) + offset, Float.fromBits(cursor))
        }
        state.cursor = cursor
        return cursor
    }

    @Benchmark
    @OperationsPerInvocation(ACCESSES)
    open fun f64Load(state: AccessState): Double {
        var cursor = state.cursor
        var sum = 0.0
        val memory = state.memory
        val offset = state.byteOffset
        repeat(ACCESSES) {
            cursor = cursor * 1_664_525 + 1_013_904_223
            sum += F64Reader(memory, ((cursor and ADDRESS_MASK) shl 3) + offset)
        }
        state.cursor = cursor
        return sum
    }

    @Benchmark
    @OperationsPerInvocation(ACCESSES)
    open fun f64Store(state: AccessState): Int {
        var cursor = state.cursor
        val memory = state.memory
        val offset = state.byteOffset
        repeat(ACCESSES) {
            cursor = cursor * 1_664_525 + 1_013_904_223
            F64Writer(memory, ((cursor and ADDRESS_MASK) shl 3) + offset, Double.fromBits(cursor.toLong()))
        }
        state.cursor = cursor
        return cursor
    }

    @State(Scope.Benchmark)
    class BulkState {
        lateinit var source: LinearMemory
        lateinit var destination: LinearMemory

        @JvmField
        @Param("4096", "1048576")
        final var byteCount = 4_096

        @Setup(Level.Trial)
        fun setup() {
            val pages = LinearMemory.Pages(ACCESS_PAGES.toUInt())
            source = LinearMemoryFactory(pages)
            destination = LinearMemoryFactory(pages)
            LinearMemoryFiller(source, 0, ACCESS_BYTES, 0x5A, ACCESS_BYTES)
        }

        @TearDown(Level.Trial)
        fun teardown() {
            LinearMemoryDestructor(source)
            LinearMemoryDestructor(destination)
        }
    }

    @Benchmark
    open fun copy(state: BulkState): Byte {
        LinearMemoryCopier(
            state.source,
            state.destination,
            0,
            0,
            state.byteCount,
            ACCESS_BYTES,
            ACCESS_BYTES,
        )
        return state.destination.readI8(state.byteCount - 1)
    }

    @Benchmark
    open fun move(state: BulkState): Byte {
        state.source.move(0, 1, state.byteCount, state.source)
        return state.source.readI8(state.byteCount)
    }

    @Benchmark
    open fun fill(state: BulkState): Byte {
        LinearMemoryFiller(state.destination, 0, state.byteCount, 0x3C, ACCESS_BYTES)
        return state.destination.readI8(state.byteCount - 1)
    }
}

@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 4)
@Measurement(iterations = 10)
open class LinearMemoryLifecycleBenchmark {

    @State(Scope.Thread)
    class ConstructionState {
        @JvmField
        @Param("16", "256")
        final var initialPages = 16

        @JvmField
        @Param("false", "true")
        final var prefault = false

        var memory: LinearMemory? = null

        @TearDown(Level.Invocation)
        fun teardown() {
            memory?.let(::LinearMemoryDestructor)
            memory = null
        }
    }

    @Benchmark
    open fun construction(state: ConstructionState): Int {
        val memory = LinearMemoryFactory(
            LinearMemory.Pages(state.initialPages.toUInt()),
            config = LinearMemoryConfig(prefault = state.prefault),
        )
        state.memory = memory
        return memory.byteSize
    }

    @State(Scope.Thread)
    class GrowthState {
        @JvmField
        @Param("1", "256")
        final var pagesToAdd = 1

        @JvmField
        @Param("false", "true")
        final var prefault = false

        lateinit var memory: LinearMemory

        @Setup(Level.Invocation)
        fun setup() {
            memory = LinearMemoryFactory(
                LinearMemory.Pages(ACCESS_PAGES.toUInt()),
                config = LinearMemoryConfig(prefault = prefault),
            )
            memory.writeI32(0, 0x12345678)
        }

        @TearDown(Level.Invocation)
        fun teardown() {
            LinearMemoryDestructor(memory)
        }
    }

    @Benchmark
    open fun growth(state: GrowthState): Int {
        state.memory.grow(state.pagesToAdd)
        return state.memory.readI32(0) + state.memory.byteSize
    }
}

@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 4)
@Measurement(iterations = 10)
open class LinearMemoryDeferredPageBenchmark {

    @State(Scope.Thread)
    class ReadState {
        @JvmField
        @Param("1", "16", "256")
        final var pages = 16

        @JvmField
        @Param("false", "true")
        final var prefault = false

        lateinit var memory: LinearMemory

        @Setup(Level.Invocation)
        fun setup() {
            memory = LinearMemoryFactory(
                LinearMemory.Pages(pages.toUInt()),
                config = LinearMemoryConfig(prefault = prefault),
            )
        }

        @TearDown(Level.Invocation)
        fun teardown() {
            LinearMemoryDestructor(memory)
        }
    }

    @Benchmark
    open fun readEvery4KiB(state: ReadState): Int = readEvery4KiB(state.memory)

    @State(Scope.Thread)
    class BulkState {
        @JvmField
        @Param("65536", "1048576", "16777216")
        final var byteCount = 1_048_576

        @JvmField
        @Param("false", "true")
        final var prefault = false

        lateinit var source: LinearMemory
        lateinit var destination: LinearMemory

        @Setup(Level.Invocation)
        fun setup() {
            val pages = LinearMemory.Pages((byteCount * 2 / LinearMemory.PAGE_SIZE).toUInt())
            val config = LinearMemoryConfig(prefault = prefault)
            source = LinearMemoryFactory(pages, config = config)
            destination = LinearMemoryFactory(pages, config = config)
            LinearMemoryFiller(source, 0, byteCount, 0x5A, source.byteSize)
            LinearMemoryFiller(destination, 0, byteCount, 0x5A, destination.byteSize)
        }

        @TearDown(Level.Invocation)
        fun teardown() {
            LinearMemoryDestructor(source)
            LinearMemoryDestructor(destination)
        }
    }

    @Benchmark
    open fun copyIntoPages(state: BulkState): Byte {
        LinearMemoryCopier(
            state.source,
            state.destination,
            0,
            state.byteCount,
            state.byteCount,
            state.source.byteSize,
            state.destination.byteSize,
        )
        return state.destination.readI8(state.byteCount * 2 - 1)
    }

    @Benchmark
    open fun moveIntoPages(state: BulkState): Byte {
        state.destination.move(0, state.byteCount, state.byteCount, state.destination)
        return state.destination.readI8(state.byteCount * 2 - 1)
    }

    @Benchmark
    open fun fillPages(state: BulkState): Byte {
        LinearMemoryFiller(
            state.destination,
            state.byteCount,
            state.byteCount,
            0x6D,
            state.destination.byteSize,
        )
        return state.destination.readI8(state.byteCount * 2 - 1)
    }
}

private fun readEvery4KiB(memory: LinearMemory): Int {
    var sum = 0
    var address = 0
    while (address < memory.byteSize) {
        sum += memory.readI8(address)
        address += 4_096
    }
    return sum
}
