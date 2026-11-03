package io.github.charlietap.chasm.compiler.backend.dex.analysis

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.AggregateInstruction
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_COPY
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_NEW_FIXED
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_SET
import io.github.charlietap.chasm.compiler.backend.abi.BRIDGE_CALL_INDIRECT
import io.github.charlietap.chasm.compiler.backend.abi.BRIDGE_CALL_REF
import io.github.charlietap.chasm.compiler.backend.abi.STRUCT_SET
import io.github.charlietap.chasm.compiler.backend.abi.TAIL_CALL_INDIRECT
import io.github.charlietap.chasm.compiler.backend.abi.descriptorWords
import io.github.charlietap.chasm.compiler.backend.analysis.form.callSite
import io.github.charlietap.chasm.compiler.backend.analysis.form.helperCall
import io.github.charlietap.chasm.compiler.backend.dex.ModuleLowering
import io.github.charlietap.chasm.compiler.backend.dex.abi.staticParameters
import io.github.charlietap.chasm.type.FunctionType

internal class CallWindow(
    val callBufferWords: Int,
    val windowWords: Int,
    val temporaries: Int,
)

internal fun analyzeCallWindow(
    module: ModuleLowering,
    function: CirFunction,
    typedSignatures: Array<String?>,
): CallWindow {
    val declarations = module.declarations
    val instructions = function.instructions
    var callBufferWords = 0
    var helperWords = 0
    var directWords = 0
    var temporaries = 0
    instructions.forEachIndexed { position, instruction ->
        val call = (instruction as? ControlInstruction)?.callSite(declarations)
        if (call != null) callBufferWords = maxOf(callBufferWords, call.arguments.size, call.type.results.types.size, 1)
        when (instruction) {
            is AggregateInstruction.StructNewS ->
                callBufferWords = maxOf(callBufferWords, declarations.structFields(instruction.type).size, 1)
            is AggregateInstruction.ArrayNewFixedS -> callBufferWords = maxOf(callBufferWords, instruction.size, 1)
            else -> Unit
        }
        val direct = when (instruction) {
            is ControlInstruction.Call -> !declarations.isImported(instruction.function)
            else -> typedSignatures[position] != null
        }
        if (direct && call != null) directWords = maxOf(directWords, argumentWords(call.type))
        val words = when (instruction) {
            is ControlInstruction.CallIndirectI, is ControlInstruction.CallIndirectS -> BRIDGE_CALL_INDIRECT.words
            is ControlInstruction.ReturnCallIndirectI, is ControlInstruction.ReturnCallIndirectS -> TAIL_CALL_INDIRECT.words
            is ControlInstruction.CallRefS -> BRIDGE_CALL_REF.words
            is AggregateInstruction.StructSetI, is AggregateInstruction.StructSetS -> STRUCT_SET.words
            is AggregateInstruction.ArraySetIi, is AggregateInstruction.ArraySetIs,
            is AggregateInstruction.ArraySetSi, is AggregateInstruction.ArraySetSs,
            -> ARRAY_SET.words
            is AggregateInstruction.ArrayNewFixedS -> ARRAY_NEW_FIXED.words
            else -> if (instruction.isArrayCopy()) ARRAY_COPY.words else instruction.helperCall()?.words ?: 0
        }
        helperWords = maxOf(helperWords, words)
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

private fun argumentWords(
    type: FunctionType,
): Int = staticParameters(type).drop(3).sumOf(::descriptorWords)
