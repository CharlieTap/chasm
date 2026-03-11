@file:OptIn(UnsafeResultValueAccess::class)

package io.github.charlietap.chasm.predecoder.instruction.memoryfused

import com.github.michaelbull.result.annotation.UnsafeResultValueAccess
import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.MemorySuperInstruction
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.loadCache
import io.github.charlietap.chasm.predecoder.storeCache
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.stack.ValueStack
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MemorySuperInstructionPredecoderTest {

    @Test
    fun `predecodes memory size into frame slots without using caches`() {
        val memories = mutableListOf(
            memoryInstance(
                type = memoryType(
                    limits = limits(min = 3u),
                ),
            ),
        )
        val context = predecodingContext(memories)
        val instruction = MemorySuperInstruction.MemorySize(
            destination = FusedDestination.FrameSlot(0),
            memoryIndex = Index.MemoryIndex(0),
        )

        val dispatchable = MemorySuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(1)
        }

        execute(dispatchable, context, vstack)

        assertEquals(3L, vstack.getFrameSlot(0))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    private fun predecodingContext(
        memories: MutableList<MemoryInstance>,
    ): PredecodingContext {
        val instance = moduleInstance(
            memAddresses = memories.indices
                .map(::memoryAddress)
                .toMutableList(),
        )
        val store = store(memories = memories)

        return PredecodingContext(
            instance = instance,
            store = store,
            instructionCache = hashMapOf(),
            types = mutableListOf(),
        )
    }

    private fun execute(
        dispatchable: DispatchableInstruction,
        context: PredecodingContext,
        vstack: ValueStack,
    ) {
        val cstack = cstack()
        val executionContext = executionContext(
            cstack = cstack,
            vstack = vstack,
            store = context.store,
            instance = context.instance,
        )

        dispatchable(vstack, cstack, context.store, executionContext)
    }
}
