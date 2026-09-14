package io.github.charlietap.chasm.compiler.kotlin

import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction

enum class KotlinGenerationTier {
    BLOCKS,
    BLOCK_LOCALS,
    REGIONS,
    RESUMABLE,
}

/** Source generation has no JVM dependencies; only compilation/loading is platform specific. */
class KotlinSourceGenerator(
    private val maxBlockInstructions: Int = 16,
    private val maxClassInstructions: Int = 192,
    private val tier: KotlinGenerationTier = KotlinGenerationTier.RESUMABLE,
    private val maxRegionInstructions: Int = 96,
) {
    init {
        require(maxBlockInstructions > 0 && maxClassInstructions >= maxBlockInstructions)
        require(maxRegionInstructions > 0)
    }

    fun generate(
        firstIp: Int,
        instructions: List<LinkedInstruction>,
        functionEntryIps: IntArray,
        additionalEntryIps: IntArray = intArrayOf(),
    ): KotlinProgramSource {
        if (tier == KotlinGenerationTier.REGIONS || tier == KotlinGenerationTier.RESUMABLE) {
            instructions.forEach { instruction ->
                require(executorCall(instruction) != null || isControlBoundary(instruction)) {
                    "No Kotlin executor for ${instruction::class.simpleName}"
                }
            }
            return generateRegions(firstIp, instructions, functionEntryIps, maxRegionInstructions, tier == KotlinGenerationTier.RESUMABLE, additionalEntryIps)
        }
        val entries = functionEntryIps.mapTo(mutableSetOf()) { it - firstIp }
        additionalEntryIps.forEach { entries.add(it - firstIp) }
        instructions.forEach { instruction -> controlTargets(instruction).forEach { entries.add(it - firstIp) } }
        require(entries.all { it in instructions.indices }) { "Invalid generated block entry" }

        val calls = instructions.map { instruction ->
            val call = executorCall(instruction)
            require(call != null || isControlBoundary(instruction)) {
                "No Kotlin executor for ${instruction::class.simpleName}"
            }
            call
        }
        val blocks = mutableListOf<KotlinBlock>()
        var index = 0
        while (index < instructions.size) {
            if (calls[index] == null) {
                index++
                continue
            }
            val start = index++
            while (
                index < instructions.size && calls[index] != null &&
                index !in entries && index - start < maxBlockInstructions
            ) {
                index++
            }
            blocks.add(KotlinBlock(start, index))
        }

        val groups = mutableListOf<KotlinSourceGroup>()
        var pending = mutableListOf<KotlinBlock>()
        var pendingSize = 0

        fun flush() {
            if (pending.isEmpty()) return
            val name = "Generated${groups.size}"
            groups.add(KotlinSourceGroup(name, source(name, pending, calls, instructions), pending.toList()))
            pending = mutableListOf()
            pendingSize = 0
        }
        for (block in blocks) {
            if (pendingSize + block.size > maxClassInstructions) flush()
            pending.add(block)
            pendingSize += block.size
        }
        flush()

        return KotlinProgramSource(
            groups = groups,
            instructionCount = instructions.size,
            generatedInstructionCount = calls.count { it != null },
            controlInstructionCount = calls.count { it == null },
            promotedInstructionCount = if (tier != KotlinGenerationTier.BLOCKS) instructions.count { valueInstruction(it) != null } else 0,
        )
    }

    private fun source(name: String, blocks: List<KotlinBlock>, calls: List<ExecutorCall?>, instructions: List<LinkedInstruction>): String = buildString {
        appendLine("@file:Suppress(\"UNUSED_PARAMETER\")")
        appendLine("package $GENERATED_PACKAGE")
        appendLine("import io.github.charlietap.chasm.runtime.instruction.*")
        appendLine("import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction")
        appendLine("import io.github.charlietap.chasm.runtime.execution.ExecutionContext")
        appendLine("import io.github.charlietap.chasm.runtime.stack.ValueStack")
        appendLine("import io.github.charlietap.chasm.executor.invoker.ext.*")
        appendLine("class $name(bindings: Array<LinkedInstruction>, private val baseIp: Int) : DispatchableInstruction() {")
        for (block in blocks) {
            for (index in block.startOffset until block.endOffset) {
                val call = checkNotNull(calls[index])
                appendLine("    private val i$index = bindings[$index] as ${call.instructionType}")
            }
        }
        appendLine("    override fun invoke(vstack: ValueStack, context: ExecutionContext, nextIp: Int): Int {")
        appendLine("        return when (nextIp - baseIp - 1) {")
        for (block in blocks) {
            appendLine("            ${block.startOffset} -> block${block.startOffset}(vstack, context)")
        }
        appendLine("            else -> error(\"Invalid generated entry IP: \" + (nextIp - 1))")
        appendLine("        }")
        appendLine("    }")
        // Keep each body independently compilable by the JVM JIT. A bounded
        // class can still have an invoke method larger than HotSpot's limit
        // when every inline executor is expanded inside one when expression.
        for (block in blocks) {
            appendLine("    private fun block${block.startOffset}(vstack: ValueStack, context: ExecutionContext): Int {")
            val values = KotlinBlockValues(this)
            for (index in block.startOffset until block.endOffset) {
                val value = if (tier != KotlinGenerationTier.BLOCKS) valueInstruction(instructions[index]) else null
                if (value != null) {
                    values.emit(index, value)
                } else {
                    // Unpromoted helpers observe the canonical guest frame.
                    // They may change slots, memory, globals or reference roots.
                    values.flush()
                    appendLine("        ${checkNotNull(calls[index]).function}(vstack, context, i$index)")
                    values.invalidate()
                }
            }
            values.flush()
            appendLine("        return baseIp + ${block.endOffset}")
            appendLine("    }")
        }
        appendLine("}")
    }
}

private fun isControlBoundary(instruction: LinkedInstruction): Boolean = when (instruction) {
    is AdminInstruction.EndFunction,
    is AdminInstruction.Jump,
    is AdminInstruction.JumpCopies,
    is AdminInstruction.JumpIfI,
    is AdminInstruction.JumpIfS,
    is AdminInstruction.JumpIfZeroI,
    is AdminInstruction.JumpIfZeroS,
    is AdminInstruction.JumpIfCopyI,
    is AdminInstruction.JumpIfCopyS,
    is AdminInstruction.JumpIfCondition,
    is AdminInstruction.JumpIfConditionMismatch,
    is AdminInstruction.JumpTableS,
    is AdminInstruction.JumpOnNullI,
    is AdminInstruction.JumpOnNullS,
    is AdminInstruction.JumpOnNonNullI,
    is AdminInstruction.JumpOnNonNullS,
    is AdminInstruction.JumpOnCastI,
    is AdminInstruction.JumpOnCastS,
    is AdminInstruction.JumpOnCastFailI,
    is AdminInstruction.JumpOnCastFailS,
    is ControlInstruction,
    -> true
    else -> false
}

internal data class ExecutorCall(val instructionType: String, val function: String)

data class KotlinBlock(val startOffset: Int, val endOffset: Int) {
    val size: Int get() = endOffset - startOffset
}

data class KotlinSourceGroup(val className: String, val source: String, val blocks: List<KotlinBlock>)

data class KotlinProgramSource(
    val groups: List<KotlinSourceGroup>,
    val instructionCount: Int,
    val generatedInstructionCount: Int,
    val controlInstructionCount: Int,
    val promotedInstructionCount: Int = 0,
    val resumableFunctionCount: Int = 0,
    val regionFallbackFunctionCount: Int = 0,
) {
    val blockCount: Int get() = groups.sumOf { it.blocks.size }
}

const val GENERATED_PACKAGE = "io.github.charlietap.chasm.compiler.kotlin.generated"

/** Includes retained reference branches as well as branches lowered into Kotlin. */
internal fun controlTargets(instruction: LinkedInstruction): IntArray = when (instruction) {
    is AdminInstruction.Jump -> intArrayOf(instruction.targetIp)
    is AdminInstruction.JumpCopies -> intArrayOf(instruction.targetIp)
    is AdminInstruction.JumpIfI -> intArrayOf(instruction.targetIp)
    is AdminInstruction.JumpIfS -> intArrayOf(instruction.targetIp)
    is AdminInstruction.JumpIfZeroI -> intArrayOf(instruction.targetIp)
    is AdminInstruction.JumpIfZeroS -> intArrayOf(instruction.targetIp)
    is AdminInstruction.JumpIfCopyI -> intArrayOf(instruction.targetIp)
    is AdminInstruction.JumpIfCopyS -> intArrayOf(instruction.targetIp)
    is AdminInstruction.JumpIfCondition -> intArrayOf(instruction.targetIp)
    is AdminInstruction.JumpIfConditionMismatch -> intArrayOf(instruction.targetIp)
    is AdminInstruction.JumpOnNullI -> intArrayOf(instruction.targetIp)
    is AdminInstruction.JumpOnNullS -> intArrayOf(instruction.targetIp)
    is AdminInstruction.JumpOnNonNullI -> intArrayOf(instruction.targetIp)
    is AdminInstruction.JumpOnNonNullS -> intArrayOf(instruction.targetIp)
    is AdminInstruction.JumpOnCastI -> intArrayOf(instruction.targetIp)
    is AdminInstruction.JumpOnCastS -> intArrayOf(instruction.targetIp)
    is AdminInstruction.JumpOnCastFailI -> intArrayOf(instruction.targetIp)
    is AdminInstruction.JumpOnCastFailS -> intArrayOf(instruction.targetIp)
    is AdminInstruction.JumpTableS -> instruction.targetIps
    else -> intArrayOf()
}
