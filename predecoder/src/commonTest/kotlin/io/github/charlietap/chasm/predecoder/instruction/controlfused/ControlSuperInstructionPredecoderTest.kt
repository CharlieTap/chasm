@file:OptIn(com.github.michaelbull.result.annotation.UnsafeResultValueAccess::class)

package io.github.charlietap.chasm.predecoder.instruction.controlfused

import io.github.charlietap.chasm.fixture.ir.instruction.emptyBlockType
import io.github.charlietap.chasm.fixture.ir.module.functionIndex
import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.frame
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.ir.instruction.ControlSuperInstruction
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.predecoder.PredecodingContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ControlSuperInstructionPredecoderTest {

    @Test
    fun `predecodes wasm calls into program entry addresses`() {
        val module = moduleInstance(functionAddresses = mutableListOf(functionAddress(0)))
        val context = PredecodingContext(
            instance = module,
            store = store(
                functions = mutableListOf(
                    wasmFunctionInstance(
                        module = module,
                        function = runtimeFunction(body = runtimeExpression(43)),
                    ),
                ),
            ),
            instructionCache = hashMapOf(),
            runtimeTypes = mutableListOf(),
        )
        val dispatchable = ControlSuperInstructionPredecoder(
            context,
            ControlSuperInstruction.Call(emptyList(), functionIndex(0)),
        ).value
        val vstack = vstack()
        val cstack = cstack(frames = listOf(frame(instance = module)))

        val entryIp = dispatchable(
            vstack,
            cstack,
            context.store,
            executionContext(cstack, vstack, context.store, module),
            17,
        )

        assertEquals(43, entryIp)
        assertEquals(17, cstack.peekFrame().returnIp)
    }

    @Test
    fun `host calls continue at the following instruction`() {
        val module = moduleInstance(functionAddresses = mutableListOf(functionAddress(0)))
        val context = PredecodingContext(
            instance = module,
            store = store(functions = mutableListOf(hostFunctionInstance())),
            instructionCache = hashMapOf(),
            runtimeTypes = mutableListOf(),
        )
        val dispatchable = ControlSuperInstructionPredecoder(
            context,
            ControlSuperInstruction.Call(emptyList(), functionIndex(0)),
        ).value
        val vstack = vstack()
        val cstack = cstack(frames = listOf(frame(instance = module)))

        val nextIp = dispatchable(
            vstack,
            cstack,
            context.store,
            executionContext(cstack, vstack, context.store, module),
            17,
        )

        assertEquals(17, nextIp)
    }

    @Test
    fun `rejects structured control after jump lowering`() {
        val context = PredecodingContext(moduleInstance(), store(), hashMapOf(), mutableListOf())

        assertFailsWith<IllegalStateException> {
            ControlSuperInstructionPredecoder(
                context,
                ControlSuperInstruction.If(FusedOperand.FrameSlot(0), emptyBlockType()),
            )
        }
    }
}
