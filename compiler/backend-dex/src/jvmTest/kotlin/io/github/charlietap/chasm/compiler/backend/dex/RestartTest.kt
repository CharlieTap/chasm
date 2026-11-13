package io.github.charlietap.chasm.compiler.backend.dex

import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.NarrowLiteralInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ThreeRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import io.github.charlietap.chasm.compiler.backend.abi.CLEAR_ROOTS
import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RestartTest {

    private val generated = generate(decodeModule(fixture("restart")))

    @Test
    fun `a self tail call copies its parameters unpublished then republishes each one its entry needs once`() {

        val instructions = instructions(SWAP)
        val clear = instructions.indexOfFirst { instruction -> instruction.invokes(CLEAR_ROOTS) }
        assertTrue(clear >= 0)
        val jump = (clear until instructions.size).first { index -> instructions[index].opcode in JUMPS }

        assertEquals(listOf(A_ROOT, B_ROOT), publishedRoots(instructions, clear, jump).sorted())
    }

    private fun publishedRoots(
        instructions: List<Instruction>,
        from: Int,
        to: Int,
    ): List<Int> {

        val fetch = instructions.indices.first { index ->
            instructions[index].invokes(FRAME) && instructions[index + 1].opcode == Opcode.MOVE_RESULT_OBJECT
        }
        val activation = (instructions[fetch + 1] as OneRegisterInstruction).registerA

        return (from until to).filter { index ->
            val instruction = instructions[index]
            instruction.opcode == Opcode.APUT_WIDE && (instruction as ThreeRegisterInstruction).registerB == activation
        }.map { index ->
            val offset = instructions[index - 1]
            assertEquals(Opcode.ADD_INT_LIT8, offset.opcode)
            (offset as NarrowLiteralInstruction).narrowLiteral
        }
    }

    private fun instructions(function: Int): List<Instruction> {
        return generated.dex.flatMap { bytes ->
            DexBackedDexFile(Opcodes.forApi(MIN_API), bytes).classes.flatMap { classDef -> classDef.methods }
        }.single { method -> method.name == "f$function" }.implementation?.instructions?.toList().orEmpty()
    }

    private fun Instruction.invokes(method: MethodRef): Boolean {

        val reference = (this as? ReferenceInstruction)?.reference as? MethodReference ?: return false

        return reference.definingClass == method.owner && reference.name == method.name
    }

    private companion object {
        const val SWAP = 1
        const val A_ROOT = 0
        const val B_ROOT = 1
        val JUMPS = setOf(Opcode.GOTO, Opcode.GOTO_16, Opcode.GOTO_32)
    }
}
