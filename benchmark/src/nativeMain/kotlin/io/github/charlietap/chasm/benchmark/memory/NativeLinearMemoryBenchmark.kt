package io.github.charlietap.chasm.benchmark.memory

import io.github.charlietap.chasm.benchmark.BenchmarkConfig
import io.github.charlietap.chasm.benchmark.StabilizedBenchmark
import io.github.charlietap.chasm.config.LinearMemoryConfig
import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.memory.copy.LinearMemoryCopier
import io.github.charlietap.chasm.memory.destruct.LinearMemoryDestructor
import io.github.charlietap.chasm.memory.ext.copyInto
import io.github.charlietap.chasm.memory.ext.toDoubleLittleEndian
import io.github.charlietap.chasm.memory.ext.toFloatLittleEndian
import io.github.charlietap.chasm.memory.ext.toIntLittleEndian
import io.github.charlietap.chasm.memory.ext.toLongLittleEndian
import io.github.charlietap.chasm.memory.ext.toShortLittleEndian
import io.github.charlietap.chasm.memory.factory.LinearMemoryFactory
import io.github.charlietap.chasm.memory.fill.LinearMemoryFiller
import io.github.charlietap.chasm.memory.init.LinearMemoryInitialiser
import io.github.charlietap.chasm.memory.read.F32Reader
import io.github.charlietap.chasm.memory.read.F64Reader
import io.github.charlietap.chasm.memory.read.I3216UReader
import io.github.charlietap.chasm.memory.read.I328UReader
import io.github.charlietap.chasm.memory.read.I32Reader
import io.github.charlietap.chasm.memory.read.I64Reader
import io.github.charlietap.chasm.memory.write.F32Writer
import io.github.charlietap.chasm.memory.write.F64Writer
import io.github.charlietap.chasm.memory.write.I32ToI16Writer
import io.github.charlietap.chasm.memory.write.I32ToI8Writer
import io.github.charlietap.chasm.memory.write.I32Writer
import io.github.charlietap.chasm.memory.write.I64Writer
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Measurement
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.benchmark.TearDown
import kotlinx.benchmark.Warmup

private const val ACCESS_BYTES = 16 * 1024 * 1024
private const val ACCESS_PAGES = ACCESS_BYTES / LinearMemory.PAGE_SIZE
private const val ACCESSES = 1_024
private const val ADDRESS_MASK = ACCESS_BYTES / Long.SIZE_BYTES - 1
private const val MAPPED = "mapped"
private const val BYTE_ARRAY = "byteArray"

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@Warmup(iterations = BenchmarkConfig.WARMUP_ITERATIONS, time = BenchmarkConfig.ITERATION_TIME)
@Measurement(iterations = BenchmarkConfig.MEASUREMENT_ITERATIONS, time = BenchmarkConfig.ITERATION_TIME)
class NativeLinearMemoryBenchmark : StabilizedBenchmark() {

    @Param(MAPPED, BYTE_ARRAY)
    var backend: String = ""

    @Param("0", "1")
    var byteOffset: Int = 0

    private lateinit var memory: LinearMemory

    @Setup
    fun setup() {
        val pages = LinearMemory.Pages((ACCESS_PAGES + 1).toUInt())
        memory = when (backend) {
            MAPPED -> LinearMemoryFactory(pages, pages)
            BYTE_ARRAY -> ByteArrayLinearMemory(pages)
            else -> error("Unknown backend: $backend")
        }
        memory.fill(0, 0x5A, ACCESS_BYTES + 1)
    }

    @TearDown
    fun teardown() {
        if (backend == MAPPED) LinearMemoryDestructor(memory)
    }

    @Benchmark
    fun i8Load(blackhole: Blackhole) {
        var cursor = 0
        var sum = 0L
        if (backend == MAPPED) {
            repeat(ACCESSES) {
                cursor = nextCursor(cursor)
                sum += I328UReader(memory, address(cursor, byteOffset))
            }
        } else {
            repeat(ACCESSES) {
                cursor = nextCursor(cursor)
                sum += (memory as ByteArrayLinearMemory)
                    .memory[address(cursor, byteOffset)]
                    .toUByte()
                    .toLong()
            }
        }
        blackhole.consume(sum)
    }

    @Benchmark
    fun i16Load(blackhole: Blackhole) {
        var cursor = 0
        var sum = 0L
        if (backend == MAPPED) {
            repeat(ACCESSES) {
                cursor = nextCursor(cursor)
                sum += I3216UReader(memory, address(cursor, byteOffset))
            }
        } else {
            repeat(ACCESSES) {
                cursor = nextCursor(cursor)
                sum += (memory as ByteArrayLinearMemory)
                    .memory
                    .toShortLittleEndian(address(cursor, byteOffset))
                    .toUShort()
                    .toLong()
            }
        }
        blackhole.consume(sum)
    }

    @Benchmark
    fun i32Load(blackhole: Blackhole) {
        var cursor = 0
        var sum = 0L
        if (backend == MAPPED) {
            repeat(ACCESSES) {
                cursor = nextCursor(cursor)
                sum += I32Reader(memory, address(cursor, byteOffset))
            }
        } else {
            repeat(ACCESSES) {
                cursor = nextCursor(cursor)
                sum += (memory as ByteArrayLinearMemory)
                    .memory
                    .toIntLittleEndian(address(cursor, byteOffset))
            }
        }
        blackhole.consume(sum)
    }

    @Benchmark
    fun i64Load(blackhole: Blackhole) {
        var cursor = 0
        var sum = 0L
        if (backend == MAPPED) {
            repeat(ACCESSES) {
                cursor = nextCursor(cursor)
                sum = sum xor I64Reader(memory, address(cursor, byteOffset))
            }
        } else {
            repeat(ACCESSES) {
                cursor = nextCursor(cursor)
                sum = sum xor (memory as ByteArrayLinearMemory)
                    .memory
                    .toLongLittleEndian(address(cursor, byteOffset))
            }
        }
        blackhole.consume(sum)
    }

    @Benchmark
    fun f32Load(blackhole: Blackhole) {
        var cursor = 0
        var bits = 0
        if (backend == MAPPED) {
            repeat(ACCESSES) {
                cursor = nextCursor(cursor)
                bits = bits xor F32Reader(memory, address(cursor, byteOffset)).toRawBits()
            }
        } else {
            repeat(ACCESSES) {
                cursor = nextCursor(cursor)
                val address = address(cursor, byteOffset)
                bits = bits xor (memory as ByteArrayLinearMemory)
                    .memory
                    .sliceArray(address until address + Float.SIZE_BYTES)
                    .toFloatLittleEndian()
                    .toRawBits()
            }
        }
        blackhole.consume(bits)
    }

    @Benchmark
    fun f64Load(blackhole: Blackhole) {
        var cursor = 0
        var bits = 0L
        if (backend == MAPPED) {
            repeat(ACCESSES) {
                cursor = nextCursor(cursor)
                bits = bits xor F64Reader(memory, address(cursor, byteOffset)).toRawBits()
            }
        } else {
            repeat(ACCESSES) {
                cursor = nextCursor(cursor)
                val address = address(cursor, byteOffset)
                bits = bits xor (memory as ByteArrayLinearMemory)
                    .memory
                    .sliceArray(address until address + Double.SIZE_BYTES)
                    .toDoubleLittleEndian()
                    .toRawBits()
            }
        }
        blackhole.consume(bits)
    }

    @Benchmark
    fun i8Store(blackhole: Blackhole) {
        var cursor = 0
        if (backend == MAPPED) {
            repeat(ACCESSES) {
                cursor = nextCursor(cursor)
                I32ToI8Writer(memory, address(cursor, byteOffset), cursor)
            }
        } else {
            repeat(ACCESSES) {
                cursor = nextCursor(cursor)
                (memory as ByteArrayLinearMemory).memory[address(cursor, byteOffset)] = cursor.toByte()
            }
        }
        blackhole.consume(cursor)
    }

    @Benchmark
    fun i16Store(blackhole: Blackhole) {
        var cursor = 0
        if (backend == MAPPED) {
            repeat(ACCESSES) {
                cursor = nextCursor(cursor)
                I32ToI16Writer(memory, address(cursor, byteOffset), cursor)
            }
        } else {
            repeat(ACCESSES) {
                cursor = nextCursor(cursor)
                cursor.copyInto(
                    (memory as ByteArrayLinearMemory).memory,
                    address(cursor, byteOffset),
                    size = Short.SIZE_BYTES,
                )
            }
        }
        blackhole.consume(cursor)
    }

    @Benchmark
    fun i32Store(blackhole: Blackhole) {
        var cursor = 0
        if (backend == MAPPED) {
            repeat(ACCESSES) {
                cursor = nextCursor(cursor)
                I32Writer(memory, address(cursor, byteOffset), cursor)
            }
        } else {
            repeat(ACCESSES) {
                cursor = nextCursor(cursor)
                cursor.copyInto(
                    (memory as ByteArrayLinearMemory).memory,
                    address(cursor, byteOffset),
                )
            }
        }
        blackhole.consume(cursor)
    }

    @Benchmark
    fun i64Store(blackhole: Blackhole) {
        var cursor = 0
        if (backend == MAPPED) {
            repeat(ACCESSES) {
                cursor = nextCursor(cursor)
                I64Writer(memory, address(cursor, byteOffset), cursor.toLong())
            }
        } else {
            repeat(ACCESSES) {
                cursor = nextCursor(cursor)
                cursor.toLong().copyInto(
                    (memory as ByteArrayLinearMemory).memory,
                    address(cursor, byteOffset),
                )
            }
        }
        blackhole.consume(cursor)
    }

    @Benchmark
    fun f32Store(blackhole: Blackhole) {
        var cursor = 0
        if (backend == MAPPED) {
            repeat(ACCESSES) {
                cursor = nextCursor(cursor)
                F32Writer(memory, address(cursor, byteOffset), Float.fromBits(cursor))
            }
        } else {
            repeat(ACCESSES) {
                cursor = nextCursor(cursor)
                Float.fromBits(cursor).copyInto(
                    (memory as ByteArrayLinearMemory).memory,
                    address(cursor, byteOffset),
                )
            }
        }
        blackhole.consume(cursor)
    }

    @Benchmark
    fun f64Store(blackhole: Blackhole) {
        var cursor = 0
        if (backend == MAPPED) {
            repeat(ACCESSES) {
                cursor = nextCursor(cursor)
                F64Writer(memory, address(cursor, byteOffset), Double.fromBits(cursor.toLong()))
            }
        } else {
            repeat(ACCESSES) {
                cursor = nextCursor(cursor)
                Double.fromBits(cursor.toLong()).copyInto(
                    (memory as ByteArrayLinearMemory).memory,
                    address(cursor, byteOffset),
                )
            }
        }
        blackhole.consume(cursor)
    }
}

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@Warmup(iterations = BenchmarkConfig.WARMUP_ITERATIONS, time = BenchmarkConfig.ITERATION_TIME)
@Measurement(iterations = BenchmarkConfig.MEASUREMENT_ITERATIONS, time = BenchmarkConfig.ITERATION_TIME)
class NativeLinearMemoryBulkBenchmark : StabilizedBenchmark() {

    @Param(MAPPED, BYTE_ARRAY)
    var backend: String = ""

    @Param("4096", "1048576")
    var byteCount: Int = 0

    private lateinit var source: LinearMemory
    private lateinit var destination: LinearMemory
    private var data = UByteArray(0)

    @Setup
    fun setup() {
        val pages = LinearMemory.Pages(ACCESS_PAGES.toUInt())
        source = memory(pages)
        destination = memory(pages)
        source.fill(0, 0x5A, ACCESS_BYTES)
        data = UByteArray(byteCount) { it.toUByte() }
    }

    @TearDown
    fun teardown() {
        if (backend == MAPPED) {
            LinearMemoryDestructor(source)
            LinearMemoryDestructor(destination)
        }
    }

    @Benchmark
    fun copy(blackhole: Blackhole) {
        if (backend == MAPPED) {
            LinearMemoryCopier(source, destination, 0, 0, byteCount, ACCESS_BYTES, ACCESS_BYTES)
        } else {
            (source as ByteArrayLinearMemory).memory.copyInto(
                destination = (destination as ByteArrayLinearMemory).memory,
                destinationOffset = 0,
                startIndex = 0,
                endIndex = byteCount,
            )
        }
        blackhole.consume(destination.readI8(byteCount - 1))
    }

    @Benchmark
    fun move(blackhole: Blackhole) {
        if (backend == MAPPED) {
            LinearMemoryCopier(source, source, 0, 1, byteCount, ACCESS_BYTES, ACCESS_BYTES)
        } else {
            val array = (source as ByteArrayLinearMemory).memory
            array.copyInto(array, destinationOffset = 1, startIndex = 0, endIndex = byteCount)
        }
        blackhole.consume(source.readI8(byteCount))
    }

    @Benchmark
    fun fill(blackhole: Blackhole) {
        if (backend == MAPPED) {
            LinearMemoryFiller(destination, 0, byteCount, 0x3C, ACCESS_BYTES)
        } else {
            (destination as ByteArrayLinearMemory).memory.fill(0x3C, 0, byteCount)
        }
        blackhole.consume(destination.readI8(byteCount - 1))
    }

    @Benchmark
    fun dataInitialisation(blackhole: Blackhole) {
        if (backend == MAPPED) {
            LinearMemoryInitialiser(data, destination, 0, 0, byteCount, byteCount, ACCESS_BYTES)
        } else {
            data.asByteArray().copyInto(
                destination = (destination as ByteArrayLinearMemory).memory,
                destinationOffset = 0,
                startIndex = 0,
                endIndex = byteCount,
            )
        }
        blackhole.consume(destination.readI8(byteCount - 1))
    }

    private fun memory(pages: LinearMemory.Pages): LinearMemory = when (backend) {
        MAPPED -> LinearMemoryFactory(pages, pages)
        BYTE_ARRAY -> ByteArrayLinearMemory(pages)
        else -> error("Unknown backend: $backend")
    }
}

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = BenchmarkConfig.WARMUP_ITERATIONS, time = BenchmarkConfig.ITERATION_TIME)
@Measurement(iterations = BenchmarkConfig.MEASUREMENT_ITERATIONS, time = BenchmarkConfig.ITERATION_TIME)
class NativeLinearMemoryLifecycleBenchmark : StabilizedBenchmark() {

    @Param(MAPPED, BYTE_ARRAY)
    var backend: String = ""

    @Param("false", "true")
    var prefault: Boolean = false

    @Param("1", "256")
    var pagesToAdd: Int = 0

    @Benchmark
    fun construction(blackhole: Blackhole) {
        val pages = LinearMemory.Pages(16u)
        val memory = when (backend) {
            MAPPED -> LinearMemoryFactory(
                pages,
                pages,
                LinearMemoryConfig(prefault = prefault),
            )
            BYTE_ARRAY -> ByteArrayLinearMemory(pages)
            else -> error("Unknown backend: $backend")
        }
        blackhole.consume(memory.byteSize)
        if (backend == MAPPED) LinearMemoryDestructor(memory)
    }

    @Benchmark
    fun growth(blackhole: Blackhole) {
        val initialPages = LinearMemory.Pages(16u)
        val maximumPages = LinearMemory.Pages((16 + pagesToAdd).toUInt())
        val memory = when (backend) {
            MAPPED -> LinearMemoryFactory(
                initialPages,
                maximumPages,
                LinearMemoryConfig(prefault = prefault),
            )
            BYTE_ARRAY -> ByteArrayLinearMemory(initialPages)
            else -> error("Unknown backend: $backend")
        }
        memory.writeI32(0, 0x12345678)
        memory.grow(pagesToAdd)
        blackhole.consume(memory.readI32(0) + memory.byteSize)
        if (backend == MAPPED) LinearMemoryDestructor(memory)
    }
}

private fun nextCursor(cursor: Int): Int = cursor * 1_664_525 + 1_013_904_223

private fun address(cursor: Int, offset: Int): Int = ((cursor and ADDRESS_MASK) shl 3) + offset
