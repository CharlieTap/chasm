package io.github.charlietap.chasm.compiler.backend.dex

import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.OffsetInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.REQUEST_TAIL_OBJECTS
import kotlin.test.Test
import kotlin.test.assertTrue

class RestartTest {

    private val generated = generate(decodeModule(fixture("restart")))

    @Test
    fun `a self tail call jumps back to the start of the function without calling the runtime`() {

        val instructions = instructions(SWAP)

        assertTrue(instructions.any { instruction -> instruction.opcode in JUMPS && (instruction as OffsetInstruction).codeOffset < 0 })
        assertTrue(instructions.none { instruction -> instruction.invokes(REQUEST_TAIL_OBJECTS) })
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
        val JUMPS = setOf(Opcode.GOTO, Opcode.GOTO_16, Opcode.GOTO_32)
    }
}
