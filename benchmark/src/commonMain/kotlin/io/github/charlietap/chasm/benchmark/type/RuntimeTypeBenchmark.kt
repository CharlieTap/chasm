package io.github.charlietap.chasm.benchmark.type

import io.github.charlietap.chasm.benchmark.BenchmarkConfig
import io.github.charlietap.chasm.benchmark.StabilizedBenchmark
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.runtime.type.RuntimeTypeRegistry
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.factory.DefinedTypeFactory
import io.github.charlietap.chasm.type.rolling.DefinedTypeUnroller
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

    private lateinit var originalTypes: List<DefinedType>
    private lateinit var equivalentTypes: List<DefinedType>
    private lateinit var currentRoot: BaselineRTT
    private lateinit var currentLeaf: BaselineRTT
    private lateinit var currentMiss: BaselineRTT
    private lateinit var currentCache: MutableMap<DefinedType, BaselineRTT>
    private lateinit var originalParents: List<DefinedType?>
    private lateinit var displayRoot: DisplayRTT
    private lateinit var displayLeaf: DisplayRTT
    private lateinit var displayMiss: DisplayRTT
    private lateinit var registry: RuntimeTypeRegistry
    private var registryRoot = RTT(-1)
    private var registryLeaf = RTT(-1)
    private var registryMiss = RTT(-1)

    @Setup
    fun setup() {
        originalTypes = definedTypeChain(chainDepth)
        equivalentTypes = definedTypeChain(chainDepth)
        originalParents = originalTypes.map(::baselineParent)
        currentCache = mutableMapOf()
        val currentTypes = originalTypes.mapIndexed { index, type ->
            baselineRuntimeType(type, originalParents[index], currentCache)
        }
        currentTypes.forEach(BaselineRTT::hydrate)
        currentRoot = currentTypes.first()
        currentLeaf = currentTypes.last()
        currentMiss = baselineRuntimeType(
            type = definedTypeChain(1, paramCount = 1).single(),
            parent = null,
            cache = currentCache,
        )

        val displayTypes = displayTypeChain(chainDepth)
        displayRoot = displayTypes.first()
        displayLeaf = displayTypes.last()
        displayMiss = displayTypeChain(1).single()

        registry = RuntimeTypeRegistry()
        val registryTypes = registry.register(originalTypes)
        registryRoot = registryTypes[0]
        registryLeaf = registryTypes[registryTypes.size - 1]
        registryMiss = registry.register(definedTypeChain(1, paramCount = 1))[0]
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
    fun registryRootSubtypeCheck(blackhole: Blackhole) {
        blackhole.consume(registry.matches(registryLeaf, registryRoot))
    }

    @Benchmark
    fun registrySubtypeMiss(blackhole: Blackhole) {
        blackhole.consume(registry.matches(registryLeaf, registryMiss))
    }

    @Benchmark
    fun currentEquivalentTypeHash(blackhole: Blackhole) {
        blackhole.consume(equivalentTypes.last().hashCode())
    }

    @Benchmark
    fun currentEquivalentModuleCacheLookup(blackhole: Blackhole) {
        for (type in equivalentTypes) {
            blackhole.consume(baselineRuntimeType(type, originalParents[type.typeIndex], currentCache))
        }
    }

    @Benchmark
    fun currentEquivalentModuleTypeAllocation(blackhole: Blackhole) {
        blackhole.consume(
            equivalentTypes.map { type ->
                baselineRuntimeType(type, originalParents[type.typeIndex], currentCache)
            },
        )
    }

    @Benchmark
    fun currentColdModuleTypeAllocation(blackhole: Blackhole) {
        val cache = mutableMapOf<DefinedType, BaselineRTT>()
        val runtimeTypes = originalTypes.mapIndexed { index, type ->
            baselineRuntimeType(type, originalParents[index], cache)
        }
        runtimeTypes.forEach(BaselineRTT::hydrate)
        blackhole.consume(runtimeTypes)
    }

    @Benchmark
    fun registryEquivalentModuleTypeAllocation(blackhole: Blackhole) {
        blackhole.consume(registry.register(equivalentTypes))
    }

    @Benchmark
    fun registryColdModuleTypeAllocation(blackhole: Blackhole) {
        blackhole.consume(RuntimeTypeRegistry().register(originalTypes))
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

    private class BaselineRTT(
        val type: DefinedType,
        val parent: DefinedType?,
        val cache: Map<DefinedType, BaselineRTT>,
    ) {
        val superTypes by lazy {
            var superType = parent
            buildList {
                while (true) {
                    val current = superType ?: break
                    val rtt = cache.getValue(current)
                    add(rtt)
                    superType = rtt.parent
                }
            }
        }

        fun hydrate() {
            check(superTypes.isNotEmpty() || superTypes.isEmpty())
        }
    }

    private companion object {
        fun baselineRuntimeType(
            type: DefinedType,
            parent: DefinedType?,
            cache: MutableMap<DefinedType, BaselineRTT>,
        ): BaselineRTT = cache.getOrPut(type) { BaselineRTT(type, parent, cache) }

        fun baselineParent(type: DefinedType): DefinedType? =
            (DefinedTypeUnroller(type).superTypes.firstOrNull() as? ConcreteHeapType.Defined)?.definedType

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
