package comparison

import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.factory.DefinedTypeFactory
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
import java.util.SplittableRandom
import java.util.concurrent.TimeUnit

private const val ALLOCATIONS = 1_024
private const val OBJECTS = 65_536
private const val FIELDS = 8
private const val ACCESSES = OBJECTS
private const val TRAVERSAL = 4_096

data class RegisteredStruct(
    val store: Store,
    val type: RTT,
    val payload: LongArray,
)

private fun registeredStruct(fields: Int): RegisteredStruct {
    val fieldType = FieldType(
        StorageType.Value(ValueType.Number(NumberType.I64)),
        Mutability.Var,
    )
    val types: List<DefinedType> = DefinedTypeFactory(
        listOf(
            RecursiveType(
                listOf(
                    SubType.Final(
                        emptyList(),
                        CompositeType.Struct(StructType(List(fields) { fieldType })),
                    ),
                ),
                RecursiveType.State.SYNTAX,
            ),
        ),
    )
    val store = Store()
    return RegisteredStruct(
        store,
        store.heap.registerRuntimeTypes(types)[0],
        LongArray(fields) { it.toLong() },
    )
}

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 300, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 300, timeUnit = TimeUnit.MILLISECONDS)
open class NewGcComparisonBenchmark {

    @State(Scope.Thread)
    open class AllocationState {
        @JvmField
        @Param("4", "32")
        final var fields = 4

        lateinit var registered: RegisteredStruct

        @Setup(Level.Trial)
        fun setup() {
            registered = registeredStruct(fields)
        }

        @TearDown(Level.Invocation)
        fun recycle() {
            registered.store.heap.collectGarbage(registered.store)
        }
    }

    @Benchmark
    @OperationsPerInvocation(ALLOCATIONS)
    open fun structAllocation(state: AllocationState): Long {
        var checksum = 0L
        repeat(ALLOCATIONS) {
            checksum += state.registered.store.heap.allocateStruct(
                state.registered.type,
                state.registered.payload,
            )
        }
        return checksum
    }

    @State(Scope.Benchmark)
    open class AccessState {
        lateinit var registered: RegisteredStruct
        lateinit var randomReferences: LongArray
        var firstReference = 0L

        @Setup(Level.Trial)
        fun setup() {
            registered = registeredStruct(FIELDS)
            val references = LongArray(OBJECTS)
            repeat(OBJECTS) { objectIndex ->
                references[objectIndex] = registered.store.heap.allocateStruct(
                    registered.type,
                    registered.payload,
                )
                registered.store.heap.setStructFieldTrusted(
                    references[objectIndex],
                    3,
                    (objectIndex.toLong() shl 32) or objectIndex.toLong(),
                )
            }
            repeat(OBJECTS) { objectIndex ->
                registered.store.heap.setStructFieldTrusted(
                    references[objectIndex],
                    0,
                    references[(objectIndex + 1) and (OBJECTS - 1)],
                )
            }
            firstReference = references[0]
            val random = SplittableRandom(42)
            randomReferences = LongArray(ACCESSES) { references[random.nextInt(OBJECTS)] }
        }
    }

    @Benchmark
    @OperationsPerInvocation(ACCESSES)
    open fun randomGet(state: AccessState): Long {
        var sum = 0L
        for (reference in state.randomReferences) {
            sum += state.registered.store.heap.getStructFieldTrusted(reference, 3)
        }
        return sum
    }

    @Benchmark
    @OperationsPerInvocation(ACCESSES)
    open fun randomSet(state: AccessState): Long {
        var value = 0L
        for (reference in state.randomReferences) {
            state.registered.store.heap.setStructFieldTrusted(reference, 3, ++value)
        }
        return value
    }

    @Benchmark
    @OperationsPerInvocation(TRAVERSAL)
    open fun nestedTraversal(state: AccessState): Long {
        var reference = state.firstReference
        repeat(TRAVERSAL) {
            reference = state.registered.store.heap.getStructFieldTrusted(reference, 0)
        }
        return reference
    }

    @State(Scope.Thread)
    open class CollectionState {
        @JvmField
        @Param("10000", "100000")
        final var objects = 10_000

        @JvmField
        @Param("0", "10", "100")
        final var livePercent = 0

        lateinit var store: Store
        lateinit var roots: ValueStack

        @Setup(Level.Invocation)
        fun setup() {
            val registered = registeredStruct(4)
            store = registered.store
            val live = objects * livePercent / 100
            roots = ValueStack(maxOf(32, live))
            repeat(objects) { objectIndex ->
                val payload = registered.payload
                payload[0] = (objectIndex.toLong() shl 16) or 0x55L
                val reference = store.heap.allocateStruct(registered.type, payload)
                if (objectIndex < live) roots.push(reference)
            }
        }
    }

    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 3)
    @Measurement(iterations = 10)
    @Benchmark
    open fun collect(state: CollectionState): Long {
        state.store.heap.collectGarbage(state.store, state.roots)
        return state.store.heap.allocatedGuestBytes()
    }
}
