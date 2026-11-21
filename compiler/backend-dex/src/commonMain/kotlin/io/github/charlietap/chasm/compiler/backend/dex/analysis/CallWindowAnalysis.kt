package io.github.charlietap.chasm.compiler.backend.dex.analysis

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.AggregateInstruction
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_COPY
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_NEW_FIXED
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_SET
import io.github.charlietap.chasm.compiler.backend.abi.CALL_INDIRECT_OBJECTS
import io.github.charlietap.chasm.compiler.backend.abi.CALL_REFERENCE_OBJECTS
import io.github.charlietap.chasm.compiler.backend.abi.REQUEST_TAIL_INDIRECT_OBJECTS
import io.github.charlietap.chasm.compiler.backend.abi.STRUCT_SET
import io.github.charlietap.chasm.compiler.backend.abi.TAIL_CALL_INDIRECT
import io.github.charlietap.chasm.compiler.backend.analysis.form.InstructionForms
import io.github.charlietap.chasm.compiler.backend.dex.ModuleLowering

internal class CallWindow(
    val callBufferWords: Int,
    val windowWords: Int,
    val temporaries: Int,
)

internal fun analyzeCallWindow(
    module: ModuleLowering,
    forms: InstructionForms,
    typedSignatures: Array<String?>,
): CallWindow {
    val declarations = module.declarations
    val instructions = forms.function.instructions
    var callBufferWords = 0
    var helperWords = 0
    var directWords = 0
    var temporaries = 0
    instructions.forEachIndexed { position, instruction ->

        val call = forms.callSite(position)
        val direct = when (instruction) {
            is ControlInstruction.Call -> !declarations.isImported(instruction.function)
            else -> typedSignatures[position] != null
        }

        val bridges = instruction !is ControlInstruction.Call || !direct
        if (call != null && bridges) {
            callBufferWords = maxOf(callBufferWords, call.operands.values.size, call.type.results.types.size, 1)
        }

        if (direct && call != null) {
            directWords = maxOf(directWords, module.functionSignature(call.typeIndex).argumentWords)
        }

        val words = when (instruction) {
            is ControlInstruction.CallIndirectI, is ControlInstruction.CallIndirectS -> CALL_INDIRECT_OBJECTS.words
            is ControlInstruction.ReturnCallIndirectI, is ControlInstruction.ReturnCallIndirectS -> TAIL_CALL_INDIRECT.words
            is ControlInstruction.CallRefS -> CALL_REFERENCE_OBJECTS.words
            is AggregateInstruction.StructSetI, is AggregateInstruction.StructSetS -> STRUCT_SET.words
            is AggregateInstruction.ArraySetIi, is AggregateInstruction.ArraySetIs,
            is AggregateInstruction.ArraySetSi, is AggregateInstruction.ArraySetSs,
            -> ARRAY_SET.words
            is AggregateInstruction.ArrayNewFixedS -> ARRAY_NEW_FIXED.words
            else -> if (instruction.isArrayCopy()) ARRAY_COPY.words else forms.helperCall(position)?.words ?: 0
        }
        helperWords = maxOf(helperWords, words)
        if (call != null && bridges) {
            helperWords = maxOf(helperWords, CALL_INDIRECT_OBJECTS.words)
        }
        if (instruction is ControlInstruction.ReturnCallIndirectI || instruction is ControlInstruction.ReturnCallIndirectS) {
            helperWords = maxOf(helperWords, REQUEST_TAIL_INDIRECT_OBJECTS.words + 1)
        }

        val transfer = when (instruction) {
            is AdminInstruction.CopySlots -> instruction.sourceSlots.size
            is AdminInstruction.JumpCopies -> instruction.operands.size
            is ControlInstruction.ReturnCall -> instruction.operands.wasm.size
            else -> 0
        }
        temporaries = maxOf(temporaries, transfer)
    }
    val windowWords = maxOf(3 + directWords, if (helperWords > 0) 1 + helperWords else 0)
    return CallWindow(callBufferWords, windowWords, temporaries)
}
