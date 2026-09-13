package io.github.charlietap.chasm.compiler.kotlin

import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction

/** Regions end at guest calls and returns, preserving the runtime call convention. */
internal fun generateRegions(
    firstIp: Int,
    instructions: List<LinkedInstruction>,
    functionEntryIps: IntArray,
    maxInstructions: Int,
): KotlinProgramSource {
    val calls = instructions.map(::executorCall)
    val values = instructions.map(::valueInstruction)
    val branches = instructions.map(::branch)
    val copies = instructions.map(::copies)
    val functionEntries = functionEntryIps.mapTo(mutableSetOf()) { it - firstIp }
    require(functionEntries.all { it in instructions.indices }) { "Invalid generated function entry" }
    val entries = functionEntries.toMutableSet()

    fun eligible(index: Int) = calls[index] != null || branches[index] != null
    instructions.indices.forEach { index ->
        branches[index]?.targets?.forEach {
            require(it - firstIp in instructions.indices) { "Invalid generated branch target" }
            entries.add(it - firstIp)
        }
        if (branches[index] != null || !eligible(index)) entries.add(index + 1)
    }
    val groups = mutableListOf<KotlinSourceGroup>()
    var index = 0
    while (index < instructions.size) {
        if (!eligible(index)) {
            index++
            continue
        }
        val start = index
        var cost = 0
        do {
            cost += when {
                branches[index] != null -> 80
                calls[index]?.instructionType?.startsWith("MemoryInstruction") == true -> 200
                values[index] != null || copies[index] != null -> 40
                else -> 1200
            }
            index++
        } while (index < instructions.size && eligible(index) && index !in functionEntries && index - start < maxInstructions && cost < 3000)
        val end = index
        val blockEntries = (listOf(start) + entries.filter { it > start && it < end }).sorted()
        val blocks = blockEntries.mapIndexed { blockIndex, entry -> KotlinBlock(entry, blockEntries.getOrElse(blockIndex + 1) { end }) }
        val name = "Generated${groups.size}"
        groups.add(KotlinSourceGroup(name, regionSource(name, firstIp, blocks, instructions, calls, values, branches, copies), blocks))
    }
    val generated = instructions.indices.count(::eligible)
    return KotlinProgramSource(
        groups,
        instructions.size,
        generated,
        instructions.size - generated,
        values.count { it != null } + copies.count { it != null },
    )
}

private fun regionSource(
    name: String,
    firstIp: Int,
    blocks: List<KotlinBlock>,
    instructions: List<LinkedInstruction>,
    calls: List<ExecutorCall?>,
    values: List<KotlinValueInstruction?>,
    branches: List<KotlinBranch?>,
    copies: List<KotlinCopies?>,
): String = buildString {
    val range = blocks.first().startOffset until blocks.last().endOffset
    val slots = mutableSetOf<Int>()
    val modified = mutableSetOf<Int>()

    fun inputs(inputs: List<KotlinValueInput>) {
        inputs.filterIsInstance<KotlinValueInput.Slot>().forEach { slots.add(it.slot) }
    }

    fun writes(writes: List<Int>) {
        slots.addAll(writes)
        modified.addAll(writes)
    }
    for (index in range) {
        values[index]?.let { value ->
            inputs(value.inputs)
            value.destinationSlot?.let { writes(listOf(it)) }
        }
        copies[index]?.let { copy ->
            inputs(copy.sources)
            writes(copy.destinations)
        }
        branches[index]?.let { branch ->
            branch.condition?.let { inputs(it.inputs) }
            branch.copies?.let { copy ->
                inputs(copy.sources)
                writes(copy.destinations)
            }
        }
    }
    require(slots.all { it >= 0 }) { "Generated regions require nonnegative frame slots" }
    val orderedSlots = slots.sorted()
    val orderedModified = modified.sorted()

    fun save(indent: String) = orderedModified.forEach { appendLine("${indent}vstack.setFrameSlot($it, r$it)") }

    fun reload(indent: String) = orderedSlots.forEach { appendLine("${indent}r$it = vstack.getFrameSlot($it)") }

    fun input(input: KotlinValueInput, index: Int): String = when (input) {
        is KotlinValueInput.Slot -> "r${input.slot}"
        is KotlinValueInput.Literal -> input.type.encode(input.source)
        is KotlinValueInput.Field -> input.type.encode("i$index.${input.field}")
    }

    fun emitCopies(copy: KotlinCopies, index: Int, indent: String) {
        if (copy.sequential) {
            copy.sources.zip(copy.destinations).forEach { (source, destination) -> appendLine("${indent}r$destination = ${input(source, index)}") }
        } else {
            copy.sources.forEachIndexed { operandIndex, source -> appendLine("${indent}val copy${index}_$operandIndex = ${input(source, index)}") }
            copy.destinations.forEachIndexed { operandIndex, destination -> appendLine("${indent}r$destination = copy${index}_$operandIndex") }
        }
    }
    appendLine("@file:Suppress(\"UNUSED_PARAMETER\", \"VARIABLE_WITH_REDUNDANT_INITIALIZER\")")
    appendLine("package $GENERATED_PACKAGE")
    appendLine("import io.github.charlietap.chasm.compiler.kotlin.KotlinGeneratedInstruction")
    appendLine("import io.github.charlietap.chasm.runtime.instruction.*")
    appendLine("import io.github.charlietap.chasm.runtime.execution.ExecutionContext")
    appendLine("import io.github.charlietap.chasm.runtime.stack.ValueStack")
    appendLine("import io.github.charlietap.chasm.executor.invoker.ext.*")
    appendLine("class $name(bindings: Array<LinkedInstruction>, private val baseIp: Int) : KotlinGeneratedInstruction() {")
    for (index in range) {
        val type = calls[index]?.instructionType ?: "AdminInstruction.${instructions[index]::class.simpleName}"
        appendLine("    private val i$index = bindings[$index] as $type")
    }
    appendLine("    override fun invoke(vstack: ValueStack, context: ExecutionContext, nextIp: Int): Int = region(vstack, context, nextIp - baseIp - 1) {}")
    appendLine("    override fun invokeCounted(vstack: ValueStack, context: ExecutionContext, nextIp: Int, onBlock: (Int) -> Unit): Int = region(vstack, context, nextIp - baseIp - 1, onBlock)")
    // Kotlin inlines two separate bodies. The measured invoke body contains no
    // counter calls, conditionals, or per-block instruction dispatch.
    appendLine("    private inline fun region(vstack: ValueStack, context: ExecutionContext, entry: Int, onBlock: (Int) -> Unit): Int {")
    orderedSlots.forEach { appendLine("        var r$it = vstack.getFrameSlot($it)") }
    appendLine("        var pc = entry")
    appendLine("        execution@ while (true) {")
    appendLine("            when (pc) {")
    for (block in blocks) {
        appendLine("                ${block.startOffset} -> {")
        appendLine("                    onBlock(${block.size})")
        for (index in block.startOffset until block.endOffset) {
            val value = values[index]
            val copy = copies[index]
            val branch = branches[index]
            when {
                value != null -> {
                    val expression = value.expression(index) { "r$it" }
                    appendLine("                    ${value.destinationSlot?.let { "r$it = " } ?: ""}$expression")
                }
                copy != null -> emitCopies(copy, index, "                    ")
                branch != null -> {
                    val condition = branch.condition?.expression(index) { "r$it" }
                    if (branch.table) {
                        appendLine("                    val selector = ($condition).toInt()")
                        appendLine("                    pc = if (selector >= 0 && selector < ${branch.targets.lastIndex}) i$index.targetIps[selector] - baseIp else ${branch.targets.last() - firstIp}")
                    } else if (condition == null) {
                        branch.copies?.let { emitCopies(it, index, "                    ") }
                        appendLine("                    pc = ${branch.targets.single() - firstIp}")
                    } else {
                        appendLine("                    if (($condition) ${if (branch.branchOnMatch) "!=" else "=="} 0L) {")
                        branch.copies?.let { emitCopies(it, index, "                        ") }
                        appendLine("                        pc = ${branch.targets.single() - firstIp}")
                        appendLine("                    } else {")
                        appendLine("                        pc = ${index + 1}")
                        appendLine("                    }")
                    }
                }
                else -> {
                    save("                    ")
                    appendLine("                    ${checkNotNull(calls[index]).function}(vstack, context, i$index)")
                    reload("                    ")
                }
            }
        }
        if (branches[block.endOffset - 1] == null) appendLine("                    pc = ${block.endOffset}")
        appendLine("                }")
    }
    appendLine("                else -> break@execution")
    appendLine("            }")
    appendLine("        }")
    save("        ")
    appendLine("        return baseIp + pc")
    appendLine("    }")
    appendLine("}")
}
