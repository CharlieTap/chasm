package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.config.MemoryLimits
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.pages
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import io.github.charlietap.chasm.config.Limits as RuntimeLimits

class DexLinkingTest {

    @Test
    fun twoModulesDefiningTheSameStructTypeShareOneRtt() {

        val linked = linked()
        val point = linked.produce("point", 1, 2)

        assertEquals(1, linked.consume("is_point", point))
        assertEquals(0, linked.consume("is_point", linked.produce("square", 4, 5)))
    }

    @Test
    fun aStructMadeByOneModuleIsReadByAnother() {

        val linked = linked()

        assertEquals(2, linked.consume("point_y", linked.produce("point", 1, 2)))
    }

    @Test
    fun refCastAcrossModulesFollowsTheSubtypeChain() {

        val linked = linked()

        assertEquals(4, linked.consume("shape_sides", linked.produce("square", 4, 5)))
    }

    @Test
    fun refCastFailsForAnUnrelatedTypeWithTheSameStorageClass() {

        val linked = linked()

        val result = invoke(linked.consumer, "flags_first", listOf(linked.produce("point", 1, 2)))

        assertTrapped(TrapReason.CAST_FAILURE, result)
    }

    @Test
    fun refTestOfAFinalTypeComparesTheRtt() {

        val linked = linked()

        assertEquals(1, linked.consume("is_sealed", linked.produce("sealed", 9)))
        assertEquals(0, linked.consume("is_sealed", linked.produce("point", 1, 2)))
    }

    @Test
    fun otherLimitsAreUnlinkable() {

        val producer = dexModule(PRODUCER)
        val limited = dexModule(CONSUMER, RuntimeConfig(limits = RuntimeLimits(memory = MemoryLimits(linearMemory = 4.pages))))
        val store = store(producer)
        val imports = producerImports(store, producer)

        val result = runBlocking { instance(store, limited, imports) }

        val error = assertIs<ExecutionError.Unlinkable>(assertIs<ChasmResult.Error<*>>(result).error)
        assertTrue("linearMemory" in error.message, error.message)
    }

    @Test
    fun anUntracedModuleJoinsATracedStore() {

        val linked = linked(producerConfig = TRACED, consumerConfig = RuntimeConfig())

        assertEquals(1, linked.consume("is_point", linked.produce("point", 1, 2)))
    }

    @Test
    fun aTrapCrossingTwoModulesReportsEachModulesFrames() {

        val linked = linked(producerConfig = TRACED, consumerConfig = TRACED)

        val trap = assertIs<ExecutionError.Trapped>(assertIs<ChasmResult.Error<*>>(invoke(linked.consumer, "run")).error).trap
        val frames = assertNotNull(trap.stackTrace).frames
            .map { frame -> Triple(frame.moduleName, frame.functionIndex, frame.functionName) }

        assertEquals(TrapReason.UNREACHABLE, trap.reason)
        assertEquals(listOf(Triple("producer", 3, "boom"), Triple("consumer", 6, "run")), frames)
    }

    private fun linked(
        producerConfig: RuntimeConfig = RuntimeConfig(),
        consumerConfig: RuntimeConfig = RuntimeConfig(),
    ): Linked {

        val producerModule = dexModule(PRODUCER, producerConfig)
        val consumerModule = dexModule(CONSUMER, consumerConfig)
        val store = store(producerModule)
        val producer = runBlocking { instance(store, producerModule) }.expect("producer")
        val boom = Import("producer", "boom", checkNotNull(exports(producer).function("boom")))
        val consumer = runBlocking { instance(store, consumerModule, listOf(boom)) }.expect("consumer")

        return Linked(producer, consumer)
    }

    private fun producerImports(
        store: Store,
        producerModule: Module,
    ): List<Import> {

        val producer = runBlocking { instance(store, producerModule) }.expect("producer")

        return listOf(Import("producer", "boom", checkNotNull(exports(producer).function("boom"))))
    }

    private fun assertTrapped(
        reason: TrapReason,
        result: ChasmResult<*, *>,
    ) {

        val error = assertIs<ChasmResult.Error<*>>(result).error

        assertEquals(reason, assertIs<ExecutionError.Trapped>(error).trap.reason)
    }

    private class Linked(
        val producer: Instance,
        val consumer: Instance,
    ) {

        fun produce(
            name: String,
            vararg arguments: Int,
        ): ExecutionValue {
            return invoke(producer, name, arguments.map { argument -> I32(argument) }).expect(name).single()
        }

        fun consume(
            name: String,
            value: ExecutionValue,
        ): Int {
            return (invoke(consumer, name, listOf(value)).expect(name).single() as I32).value
        }
    }

    private companion object {
        const val PRODUCER = "linking_producer"
        const val CONSUMER = "linking_consumer"
        val TRACED = RuntimeConfig(stacktraces = true)
    }
}
