package io.github.charlietap.chasm.compiler.backend.chasm

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.Frame
import io.github.charlietap.chasm.cir.FunctionIndex
import io.github.charlietap.chasm.cir.TypeIndex
import io.github.charlietap.chasm.compiler.backend.chasm.link.LinkContext
import io.github.charlietap.chasm.compiler.backend.chasm.link.createLinkContext
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.store.Fuel
import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionLayoutTest {

    @Test
    fun `every instruction keeps its position when checkpoints link`() {
        val layout = FunctionLayout(function(size = 4, checkpoints = intArrayOf(0, 2)), linksCheckpoints = true)

        assertEquals(4, layout.size)
        assertEquals(listOf(0, 1, 2, 3, 4), (0..4).map(layout::offset))
        assertEquals(listOf(0, 1, 2, 3), (0 until 4).map(layout::position))
    }

    @Test
    fun `unlinked checkpoints share the offset of the instruction after them`() {
        val layout = FunctionLayout(function(size = 10, checkpoints = intArrayOf(0, 3, 4, 9)), linksCheckpoints = false)

        assertEquals(6, layout.size)
        assertEquals(listOf(0, 0, 1, 2, 2, 2, 3, 4, 5, 6, 6), (0..10).map(layout::offset))
        assertEquals(listOf(1, 2, 5, 6, 7, 8), (0 until 6).map(layout::position))
    }

    @Test
    fun `offsets outside the function have no position`() {
        val layout = FunctionLayout(function(size = 3, checkpoints = intArrayOf(0)), linksCheckpoints = false)

        assertEquals(-1, layout.position(-1))
        assertEquals(-1, layout.position(2))
    }

    @Test
    fun `every linked offset maps back to its position`() {
        val layout = FunctionLayout(function(size = 12, checkpoints = intArrayOf(0, 1, 5, 6, 7, 11)), linksCheckpoints = false)

        for (offset in 0 until layout.size) {
            assertEquals(offset, layout.offset(layout.position(offset)))
        }
    }

    @Test
    fun `the linker places instructions where the layout says`() {
        val checkpointPatterns = listOf(
            intArrayOf(),
            intArrayOf(0),
            intArrayOf(2, 5),
            intArrayOf(0, 1, 2),
            intArrayOf(0, 3, 4, 9, 12, 13),
        )
        for (linksCheckpoints in listOf(false, true)) {
            for (checkpoints in checkpointPatterns) {
                val function = function(size = 14, checkpoints = checkpoints)
                val layout = FunctionLayout(function, linksCheckpoints)
                val context = linkContext(linksCheckpoints)
                context.beginFunction(entryIp = 100, function = function)

                assertEquals(layout.size, context.programSize(function))
                for (position in 0..function.instructions.size) {
                    assertEquals(100 + layout.offset(position), context.ip(position), "position $position")
                }
            }
        }
    }

    @Test
    fun `checkpoints link only in a store that meters fuel or can be interrupted`() {
        val function = function(size = 6, checkpoints = intArrayOf(0, 3))
        for (meterFuel in listOf(false, true)) {
            for (interruptible in listOf(false, true)) {
                val store = store(config = StoreConfig(meterFuel = meterFuel, interruptible = interruptible))
                val layout = FunctionLayout(function, store)
                val context = createLinkContext(store, moduleInstance(), emptyList())
                context.beginFunction(entryIp = 0, function = function)

                val expectedSize = if (meterFuel || interruptible) 6 else 4
                assertEquals(expectedSize, layout.size, "meterFuel=$meterFuel interruptible=$interruptible")
                assertEquals(expectedSize, context.programSize(function))
                for (position in 0..function.instructions.size) {
                    assertEquals(layout.offset(position), context.ip(position))
                }
            }
        }
    }

    @Test
    fun `a function of only unlinked checkpoints links to nothing`() {
        val layout = FunctionLayout(function(size = 2, checkpoints = intArrayOf(0, 1)), linksCheckpoints = false)

        assertEquals(0, layout.size)
        assertEquals(0, layout.offset(2))
        assertEquals(-1, layout.position(0))
    }

    @Test
    fun `an empty function links to nothing`() {
        val layout = FunctionLayout(function(size = 0, checkpoints = intArrayOf()), linksCheckpoints = false)

        assertEquals(0, layout.size)
        assertEquals(0, layout.offset(0))
    }

    private fun function(
        size: Int,
        checkpoints: IntArray,
    ) = CirFunction(
        index = FunctionIndex(0),
        type = TypeIndex(0),
        frame = Frame(frameSlots = 1, activationHeaderSlot = 0),
        instructions = Array(size) { position ->
            if (position in checkpoints) AdminInstruction.Checkpoint else ControlInstruction.Unreachable
        },
        checkpoints = checkpoints,
    )

    private fun linkContext(
        linksCheckpoints: Boolean,
    ) = LinkContext(
        instance = moduleInstance(),
        runtimeTypes = store().heap.registerRuntimeTypes(emptyList()),
        functionAddresses = IntArray(0),
        functions = emptyArray(),
        tables = emptyArray(),
        memories = emptyArray(),
        globals = emptyArray(),
        elements = emptyArray(),
        data = emptyArray(),
        definedTypes = emptyList(),
        fuel = if (linksCheckpoints) Fuel(metered = true) else null,
        interrupt = null,
    )
}
