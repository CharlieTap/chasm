package io.github.charlietap.chasm.compiler.backend.dex

import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.WideLiteralInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.compiler.backend.abi.CLEAR_ROOTS
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.TAKE_EXCEPTION
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CatchRootsTest {

    private val module = decodeModule(fixture("eh"))
    private val code = ModuleCompiler(module).getOrThrow { error -> IllegalStateException(error.toString()) }

    @Test
    fun `a chunked catch does not clear a payload slot at or above its stack height`() {

        val catchesReference = code.functions.single { function ->
            function.exceptions?.regions.orEmpty().any { region -> region.catches.any { handler -> handler.exceptionReference } }
        }
        val overlapping = code.copy(functions = code.functions.map { function -> if (function === catchesReference) discardingAll(function) else function })
        val matchers = matcherChunks(ModuleLowerer(module, overlapping, DexOptions(splitInstructions = 1)), catchesReference)
        val stores = matchers.map { instructions -> matcherStores(instructions) }

        assertTrue(stores.isNotEmpty())
        stores.forEach { (cleared, payload) -> assertFalse(payload in cleared) }
    }

    private fun matcherStores(instructions: List<Instruction>): Pair<List<Int>, Int> {

        val clear = instructions.indexOfFirst { instruction -> instruction.invokes(CLEAR_ROOTS) }
        val take = instructions.indexOfFirst { instruction -> instruction.invokes(TAKE_EXCEPTION) }
        val zero = (clear until take).firstOrNull { index ->
            val instruction = instructions[index]
            instruction.opcode in CONST_WIDES && (instruction as WideLiteralInstruction).wideLiteral == 0L
        }
        val cleared = if (zero == null) emptyList() else movesFrom(instructions, (instructions[zero] as OneRegisterInstruction).registerA, zero until take)
        val exception = (instructions[take + 1] as OneRegisterInstruction).registerA
        val payload = movesFrom(instructions, exception, take + 2 until instructions.size).first()

        assertTrue(clear in 0 until take)

        return cleared to payload
    }

    private fun discardingAll(function: CirFunction): CirFunction {

        val exceptions = checkNotNull(function.exceptions)
        val regions = exceptions.regions.map { region ->
            region.copy(catches = region.catches.map { handler -> if (handler.exceptionReference) handler.copy(stackSlots = 0) else handler })
        }

        return function.copy(exceptions = exceptions.copy(regions = regions))
    }

    private fun matcherChunks(
        generated: DexModule,
        function: CirFunction,
    ): List<List<Instruction>> {

        val chunk = "f${function.index.index}\$c"

        return generated.dex.flatMap { bytes -> DexBackedDexFile(Opcodes.forApi(MIN_API), bytes).classes.flatMap { classDef -> classDef.methods } }
            .filter { method -> method.name.startsWith(chunk) }
            .map { method -> method.implementation?.instructions?.toList().orEmpty() }
            .filter { instructions -> instructions.any { instruction -> instruction.invokes(TAKE_EXCEPTION) } }
    }

    private fun movesFrom(
        instructions: List<Instruction>,
        source: Int,
        range: IntRange,
    ): List<Int> {
        return range.map { index -> instructions[index] }
            .filter { instruction -> instruction.opcode in MOVE_WIDES && (instruction as TwoRegisterInstruction).registerB == source }
            .map { instruction -> (instruction as TwoRegisterInstruction).registerA }
    }

    private fun Instruction.invokes(method: MethodRef): Boolean {

        val reference = (this as? ReferenceInstruction)?.reference as? MethodReference ?: return false

        return reference.definingClass == method.owner && reference.name == method.name
    }

    private companion object {
        val CONST_WIDES = setOf(Opcode.CONST_WIDE_16, Opcode.CONST_WIDE_32, Opcode.CONST_WIDE, Opcode.CONST_WIDE_HIGH16)
        val MOVE_WIDES = setOf(Opcode.MOVE_WIDE, Opcode.MOVE_WIDE_FROM16, Opcode.MOVE_WIDE_16)
    }
}
