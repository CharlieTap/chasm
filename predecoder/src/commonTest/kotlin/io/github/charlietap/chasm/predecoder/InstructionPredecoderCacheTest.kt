@file:OptIn(UnsafeResultValueAccess::class)

package io.github.charlietap.chasm.predecoder

import com.github.michaelbull.result.annotation.UnsafeResultValueAccess
import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.FusedVariableInstruction
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack
import kotlin.test.Test
import kotlin.test.assertEquals

class InstructionPredecoderCacheTest {

    @Test
    fun `cache keys context-sensitive instructions by function interface layout`() {
        val instruction = FusedVariableInstruction.LocalSet(
            operand = FusedOperand.FrameSlot(19),
            localIdx = Index.LocalIndex(1),
        )
        val instructionCache = hashMapOf<InstructionCacheKey, DispatchableInstruction>()
        val baseContext = PredecodingContext(
            instance = moduleInstance(),
            store = store(),
            instructionCache = instructionCache,
            types = mutableListOf(),
        )

        val zeroResultContext = baseContext.copy(
            functionParamCount = 0,
            functionResultCount = 0,
        )
        val oneResultContext = baseContext.copy(
            functionParamCount = 0,
            functionResultCount = 1,
        )

        val zeroResultDispatchable = InstructionPredecoder(zeroResultContext, instruction).value
        val oneResultDispatchable = InstructionPredecoder(oneResultContext, instruction).value

        assertEquals(2, instructionCache.size)

        execute(
            zeroResultDispatchable,
            zeroResultContext,
            ValueStack().apply {
                reserveFrame(24)
                setFrameSlot(19, 7L)
            },
        ).also { vstack ->
            assertEquals(7L, vstack.getFrameSlot(1))
        }

        execute(
            oneResultDispatchable,
            oneResultContext,
            ValueStack().apply {
                reserveFrame(24)
                setFrameSlot(19, 9L)
            },
        ).also { vstack ->
            assertEquals(9L, vstack.getFrameSlot(2))
        }
    }

    private fun execute(
        dispatchable: DispatchableInstruction,
        context: PredecodingContext,
        vstack: ValueStack,
    ): ValueStack {
        val cstack = cstack()
        val executionContext = executionContext(
            cstack = cstack,
            vstack = vstack,
            store = context.store,
            instance = context.instance,
        )

        dispatchable(vstack, cstack, context.store, executionContext)
        return vstack
    }
}
