package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.GCThreshold
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.collect
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.readI32
import io.github.charlietap.chasm.host.readReference
import io.github.charlietap.chasm.host.writeReference
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.ValueType
import kotlinx.coroutines.test.TestResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class LiveStackRootsTest {

    @Test
    fun `references held only by an outer frame survive a collection in a reentrant call`() = assertOuterFrameSurvives("across_reentry")

    @Test
    fun `references held only by the outermost frame survive a collection two reentrant calls deep`() =
        assertOuterFrameSurvives("across_double_reentry")

    @Test
    fun `a collection from a host function keeps the references of every outer frame`() = assertOuterFrameSurvives("across_host_collect")

    private fun assertOuterFrameSurvives(export: String): TestResult {
        return integrationTest(FIXTURE) {
            STRATEGIES.forEach { strategy ->

                val store = engine.store()
                val scenario = OuterFrameScenario(store)
                val runner = runner(
                    store = store,
                    imports = scenario.imports,
                    config = RuntimeConfig(gcStrategy = strategy, gcThreshold = GCThreshold.KB(0)),
                )
                scenario.instance = runner.instance().expect("instantiates")

                runner.invoke(export).expect(export)

                assertSame(SENTINEL, scenario.checkedExtern, "${engine.name()} $strategy")
                assertEquals(42, scenario.checkedField, "${engine.name()} $strategy")
            }
        }
    }

    private class OuterFrameScenario(
        private val store: Store,
    ) {

        lateinit var instance: Instance
        var checkedExtern: Any? = null
        var checkedField: Int = 0

        private val make = HostFunction { parameters, results ->
            val value = if (parameters.readI32(0) == SENTINEL_KEY) SENTINEL else Any()
            results.writeReference(0, value)
        }

        private val reenter = HostFunction { _, _ ->
            invoke(instance, "churn").expect("churn")
        }

        private val reenterMiddle = HostFunction { _, _ ->
            invoke(instance, "middle").expect("middle")
        }

        private val collectGarbage = HostFunction { _, _ ->
            collect(store)
        }

        private val check = HostFunction { parameters, _ ->
            checkedExtern = parameters.readReference(0)
            checkedField = parameters.readI32(1)
        }

        val imports = listOf(
            Import("env", "make", function(store, functionType(listOf(I32), listOf(EXTERN)), make)),
            Import("env", "reenter", function(store, functionType(emptyList(), emptyList()), reenter)),
            Import("env", "reenter_middle", function(store, functionType(emptyList(), emptyList()), reenterMiddle)),
            Import("env", "collect", function(store, functionType(emptyList(), emptyList()), collectGarbage)),
            Import("env", "check", function(store, functionType(listOf(EXTERN, I32), emptyList()), check)),
        )
    }

    private companion object {
        const val FIXTURE = "live_stack_roots"
        const val SENTINEL_KEY = 7

        val SENTINEL = Any()
        val STRATEGIES = listOf(GCStrategy.TRADITIONAL, GCStrategy.ARENA)
        val I32 = ValueType.Number(NumberType.I32)
        val EXTERN = ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Extern))

        fun functionType(
            parameters: List<ValueType>,
            results: List<ValueType>,
        ): FunctionType {
            return FunctionType(ResultType(parameters), ResultType(results))
        }
    }
}
