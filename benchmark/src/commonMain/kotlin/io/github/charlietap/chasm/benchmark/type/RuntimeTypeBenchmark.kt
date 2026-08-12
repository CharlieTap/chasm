package io.github.charlietap.chasm.benchmark.type

import io.github.charlietap.chasm.benchmark.BenchmarkConfig
import io.github.charlietap.chasm.benchmark.StabilizedBenchmark
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.RTT
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.factory.DefinedTypeFactory
import io.github.charlietap.chasm.type.factory.RTTFactory
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
import kotlinx.benchmark.Warmup

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@Warmup(iterations = BenchmarkConfig.WARMUP_ITERATIONS, time = BenchmarkConfig.ITERATION_TIME)
@Measurement(iterations = BenchmarkConfig.MEASUREMENT_ITERATIONS, time = BenchmarkConfig.ITERATION_TIME)
class RuntimeTypeBenchmark : StabilizedBenchmark() {

    @Param("1", "8", "64")
    var chainDepth: Int = 0

    private lateinit var equivalentTypes: List<DefinedType>
    private lateinit var currentRoot: RTT
    private lateinit var currentLeaf: RTT
    private lateinit var currentMiss: RTT
    private lateinit var currentCache: MutableMap<DefinedType, RTT>
    private lateinit var displayRoot: DisplayRTT
    private lateinit var displayLeaf: DisplayRTT
    private lateinit var displayMiss: DisplayRTT

    @Setup
    fun setup() {
        val originalTypes = definedTypeChain(chainDepth)
        equivalentTypes = definedTypeChain(chainDepth)
        currentCache = mutableMapOf()
        val currentTypes = originalTypes.map { type -> RTTFactory(type, currentCache) }
        currentTypes.forEach(RTT::hydrate)
        currentRoot = currentTypes.first()
        currentLeaf = currentTypes.last()
        currentMiss = RTTFactory(definedTypeChain(1, paramCount = 1).single(), currentCache)

        val displayTypes = displayTypeChain(chainDepth)
        displayRoot = displayTypes.first()
        displayLeaf = displayTypes.last()
        displayMiss = displayTypeChain(1).single()
    }

    @Benchmark
    fun currentRootSubtypeCheck(blackhole: Blackhole) {
        blackhole.consume(currentLeaf === currentRoot || currentLeaf.superTypes.any { it === currentRoot })
    }

    @Benchmark
    fun displayRootSubtypeCheck(blackhole: Blackhole) {
        blackhole.consume(displayLeaf.matches(displayRoot))
    }

    @Benchmark
    fun currentSubtypeMiss(blackhole: Blackhole) {
        blackhole.consume(currentLeaf === currentMiss || currentLeaf.superTypes.any { it === currentMiss })
    }

    @Benchmark
    fun displaySubtypeMiss(blackhole: Blackhole) {
        blackhole.consume(displayLeaf.matches(displayMiss))
    }

    @Benchmark
    fun currentEquivalentTypeHash(blackhole: Blackhole) {
        blackhole.consume(equivalentTypes.last().hashCode())
    }

    @Benchmark
    fun currentEquivalentModuleCacheLookup(blackhole: Blackhole) {
        for (type in equivalentTypes) {
            blackhole.consume(RTTFactory(type, currentCache))
        }
    }

    private class DisplayRTT(
        val depth: Int,
        val superTypes: Array<DisplayRTT>,
    ) {
        fun matches(expected: DisplayRTT): Boolean {
            if (this === expected) return true
            if (expected.depth >= depth) return false
            return superTypes[depth - expected.depth - 1] === expected
        }
    }

    private companion object {
        fun definedTypeChain(
            depth: Int,
            paramCount: Int = 0,
        ): List<DefinedType> {
            val params = ResultType(List(paramCount) { ValueType.Number(NumberType.I32) })
            val recursiveTypes = List(depth) { index ->
                RecursiveType(
                    subTypes = listOf(
                        SubType.Open(
                            superTypes = if (index == 0) {
                                emptyList()
                            } else {
                                listOf(ConcreteHeapType.TypeIndex(index - 1))
                            },
                            compositeType = CompositeType.Function(
                                FunctionType(params = params, results = ResultType(emptyList())),
                            ),
                        ),
                    ),
                    state = RecursiveType.State.SYNTAX,
                )
            }
            return DefinedTypeFactory(recursiveTypes)
        }

        fun displayTypeChain(depth: Int): List<DisplayRTT> {
            val types = ArrayList<DisplayRTT>(depth)
            repeat(depth) { index ->
                val ancestors = Array(index) { ancestorIndex -> types[index - ancestorIndex - 1] }
                types += DisplayRTT(index, ancestors)
            }
            return types
        }
    }
}
