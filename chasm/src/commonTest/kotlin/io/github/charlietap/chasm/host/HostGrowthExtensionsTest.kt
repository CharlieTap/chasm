package io.github.charlietap.chasm.host

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.config.StoreLimits
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.memory.memoryByteSize
import io.github.charlietap.chasm.embedding.memory.sizeMemory
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.embedding.table.readTable
import io.github.charlietap.chasm.embedding.table.sizeTable
import io.github.charlietap.chasm.embedding.table.writeTable
import io.github.charlietap.chasm.fixture.type.functionType
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class HostGrowthExtensionsTest {

    @Test
    fun `grows memory from withMemory receiver`() = runTest {
        val growth = mutableListOf<Int>()
        val byteSizes = mutableListOf<Int>()
        var receiver: Memory? = null
        val hostFunction = HostFunction { _, _ ->
            withMemory(1) {
                growth += grow(1)
                growth += grow(0)
                receiver = this
                byteSizes += byteSize
            }
        }
        val instance = growthInstance(FILE_HOST_GROWTH, StoreConfig(), hostFunction)

        invoke(instance, "run").expect("the host function grows the memory")

        val exports = exports(instance)
        val target = exports.memory("target")!!
        val decoy = exports.memory("decoy")!!
        assertEquals(listOf(1, 2), growth)
        assertEquals(listOf(PAGE_SIZE * 2), byteSizes)
        assertEquals(target, receiver)
        assertEquals(2, sizeMemory(target))
        assertEquals(PAGE_SIZE * 2, memoryByteSize(target))
        assertEquals(PAGE_SIZE, memoryByteSize(decoy))
    }

    @Test
    fun `grows table from withTable receiver`() = runTest {
        val growth = mutableListOf<Int>()
        val sizes = mutableListOf<Int>()
        val hostFunction = HostFunction { _, _ ->
            withTable(0) {
                growth += grow(2, ELEVEN)
                sizes += size
            }
        }
        val instance = growthInstance(FILE_HOST_GROWTH, StoreConfig(), hostFunction)
        val table = exports(instance).table("table")!!
        writeTable(table, 0, SEVEN)
        writeTable(table, 1, NINE)

        invoke(instance, "run").expect("the host function grows the table")

        assertEquals(listOf(2), growth)
        assertEquals(listOf(4), sizes)
        assertEquals(listOf(SEVEN, NINE, ELEVEN, ELEVEN), List(sizeTable(table)) { index -> readTable(table, index) })
    }

    @Test
    fun `host memory growth past the store limit returns minus one even when guest growth traps`() = runTest {
        val growth = mutableListOf<Int>()
        val byteSizes = mutableListOf<Int>()
        val hostFunction = HostFunction { _, _ ->
            withMemory(0) {
                growth += grow(1)
                growth += grow(1)
                byteSizes += byteSize
            }
        }
        val config = StoreConfig(limits = StoreLimits(memoryPages = 2, trapOnGrowFailure = true))
        val instance = growthInstance(FILE_HOST_GROWTH_LIMITS, config, hostFunction)

        invoke(instance, "run").expect("the host function grows the memory")

        val memory = exports(instance).memory("memory")!!
        assertEquals(listOf(1, -1), growth)
        assertEquals(listOf(PAGE_SIZE * 2), byteSizes)
        assertEquals(2, sizeMemory(memory))
    }

    @Test
    fun `host table growth past the store limit returns minus one and keeps the elements`() = runTest {
        val growth = mutableListOf<Int>()
        val sizes = mutableListOf<Int>()
        val hostFunction = HostFunction { _, _ ->
            withTable(0) {
                growth += grow(2, ELEVEN)
                growth += grow(1, ELEVEN)
                sizes += size
            }
        }
        val config = StoreConfig(limits = StoreLimits(tableElements = 3, trapOnGrowFailure = true))
        val instance = growthInstance(FILE_HOST_GROWTH_LIMITS, config, hostFunction)
        val table = exports(instance).table("table")!!
        writeTable(table, 0, SEVEN)
        writeTable(table, 1, NINE)

        invoke(instance, "run").expect("the host function grows the table")

        assertEquals(listOf(-1, 2), growth)
        assertEquals(listOf(3), sizes)
        assertEquals(listOf(SEVEN, NINE, ELEVEN), List(sizeTable(table)) { index -> readTable(table, index) })
    }

    private suspend fun growthInstance(
        file: String,
        config: StoreConfig,
        hostFunction: HostFunction,
    ): Instance {
        val store = store(config)
        val module = module(Resource(file).readBytes()).expect("the module is valid")
        val import = Import("env", "grow", function(store, functionType(), hostFunction))

        return instance(store, module, listOf(import)).expect("the module instantiates")
    }

    private companion object {
        const val FILE_HOST_GROWTH = "host/host_growth.wasm"
        const val FILE_HOST_GROWTH_LIMITS = "host/host_growth_limits.wasm"
        const val PAGE_SIZE = 65_536
        const val SEVEN = "seven"
        const val NINE = "nine"
        const val ELEVEN = "eleven"
    }
}
