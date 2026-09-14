package io.github.charlietap.chasm.compiler.kotlin

import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction

/** Regions end at guest calls and returns, preserving the runtime call convention. */
internal fun generateRegions(
    firstIp: Int,
    instructions: List<LinkedInstruction>,
    functionEntryIps: IntArray,
    maxInstructions: Int,
    resumeFunctions: Boolean = false,
    additionalEntryIps: IntArray = intArrayOf(),
    structureControl: Boolean = false,
    typedValues: Boolean = false,
): KotlinProgramSource {
    val calls = instructions.map(::executorCall)
    val values = instructions.map(::valueInstruction)
    val branches = instructions.map(::branch)
    val copies = instructions.map(::copies)
    val functionEntries = functionEntryIps.mapTo(mutableSetOf()) { it - firstIp }
    require(functionEntries.all { it in instructions.indices }) { "Invalid generated function entry" }
    val entries = functionEntries.toMutableSet()
    additionalEntryIps.forEach {
        require(it - firstIp in instructions.indices) { "Invalid generated handler entry" }
        entries.add(it - firstIp)
    }

    fun eligible(index: Int) = calls[index] != null || branches[index] != null
    instructions.indices.forEach { index ->
        controlTargets(instructions[index]).forEach {
            require(it - firstIp in instructions.indices) { "Invalid generated branch target" }
            entries.add(it - firstIp)
        }
        if (branches[index] != null || !eligible(index)) entries.add(index + 1)
    }

    fun instructionCost(index: Int) = when {
        branches[index] != null -> 80
        calls[index]?.instructionType?.startsWith("MemoryInstruction") == true -> 200
        values[index] != null || copies[index] != null -> 40
        !eligible(index) -> 0
        else -> 1200
    }
    val regions = mutableListOf<List<KotlinBlock>>()
    var index = 0
    while (index < instructions.size) {
        if (!eligible(index)) {
            index++
            continue
        }
        val start = index
        var cost = 0
        do {
            cost += instructionCost(index)
            index++
        } while (index < instructions.size && eligible(index) && index !in functionEntries && index - start < maxInstructions && cost < 3000)
        val end = index
        val blockEntries = (listOf(start) + entries.filter { it > start && it < end }).sorted()
        val blocks = blockEntries.mapIndexed { blockIndex, entry -> KotlinBlock(entry, blockEntries.getOrElse(blockIndex + 1) { end }) }
        regions.add(blocks)
    }
    val predecessors = mutableMapOf<Int, MutableList<Int>>()
    if (structureControl) {
        instructions.forEachIndexed { source, instruction ->
            controlTargets(instruction).forEach { target -> predecessors.getOrPut(target - firstIp) { mutableListOf() }.add(source) }
        }
    }
    val forcedEntries = functionEntries + additionalEntryIps.map { it - firstIp }
    var structuredLoops = 0
    var structuredBlocks = 0
    var linearBodies = 0
    var nativeI32Slots = 0
    var nativeF32Slots = 0
    var nativeF64Slots = 0
    var rawSlots = 0
    var mixedSlots = 0
    val groups = mutableListOf<KotlinSourceGroup>()
    var resumableFunctions = 0
    var fallbackFunctions = 0
    val orderedFunctions = functionEntries.sorted()

    fun emit(name: String, blocks: List<KotlinBlock>) {
        val layout = regionValueLayout(blocks.flatMap { (it.startOffset until it.endOffset).toList() }, values, branches, copies, typedValues)
        nativeI32Slots += layout.nativeTypes.count { it.value == KotlinValueType.I32 }
        nativeF32Slots += layout.nativeTypes.count { it.value == KotlinValueType.F32 }
        nativeF64Slots += layout.nativeTypes.count { it.value == KotlinValueType.F64 }
        rawSlots += layout.slots.size - layout.nativeTypes.size
        mixedSlots += layout.mixedSlotCount
        val plan = if (structureControl) structuredControl(blocks, branches, firstIp, predecessors, forcedEntries) else null
        structuredLoops += plan?.loops?.size ?: 0
        structuredBlocks += plan?.loops?.sumOf { it.blocks.size } ?: 0
        if (plan?.linearExit != null) linearBodies++
        groups.add(KotlinSourceGroup(name, regionSource(name, firstIp, blocks, instructions, calls, values, branches, copies, plan, layout), blocks, plan?.entryOffsets ?: blocks.map { it.startOffset }))
    }
    orderedFunctions.forEachIndexed { functionIndex, start ->
        val end = orderedFunctions.getOrElse(functionIndex + 1) { instructions.size }
        val functionRegions = regions.filter { it.first().startOffset in start until end }
        if (functionRegions.isEmpty()) return@forEachIndexed
        // Keep a complete function body where its emitted work remains bounded.
        // Large functions retain the previous region tier rather than producing
        // a monolithic JVM method that cannot be optimized effectively.
        if (resumeFunctions && end - start <= maxInstructions * 2 && (start until end).sumOf(::instructionCost) <= 6000) {
            emit("GeneratedFunction$functionIndex", functionRegions.flatten())
            resumableFunctions++
        } else {
            functionRegions.forEachIndexed { regionIndex, blocks -> emit("GeneratedFunction${functionIndex}Region$regionIndex", blocks) }
            if (resumeFunctions) fallbackFunctions++
        }
    }
    val generated = instructions.indices.count(::eligible)
    return KotlinProgramSource(
        groups,
        instructions.size,
        generated,
        instructions.size - generated,
        values.count { it != null } + copies.count { it != null },
        resumableFunctions,
        fallbackFunctions,
        structuredLoops,
        structuredBlocks,
        linearBodies,
        nativeI32Slots,
        nativeF32Slots,
        nativeF64Slots,
        rawSlots,
        mixedSlots,
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
    plan: KotlinStructuredPlan?,
    layout: KotlinRegionValueLayout,
): String = buildString {
    // Calls and returns are holes in a resumable body. Their original
    // dispatchers stay installed; reaching one exits to the runtime and the
    // saved successor IP re-enters this body after the callee returns.
    val range = blocks.flatMap { (it.startOffset until it.endOffset).toList() }
    val locals = KotlinRegionValues(this, layout)

    fun save(indent: String) = locals.save(indent)

    fun reload(indent: String) = locals.reload(indent)

    fun input(input: KotlinValueInput, index: Int): String = when (input) {
        is KotlinValueInput.Slot -> locals.word(input.slot)
        is KotlinValueInput.Literal -> input.type.encode(input.source)
        is KotlinValueInput.Field -> input.type.encode("i$index.${input.field}")
    }

    fun emitCopies(copy: KotlinCopies, index: Int, indent: String) {
        if (copy.sequential) {
            copy.sources.zip(copy.destinations).forEach { (source, destination) -> locals.writeWord(destination, input(source, index), indent) }
        } else {
            copy.sources.forEachIndexed { operandIndex, source -> appendLine("${indent}val copy${index}_$operandIndex = ${input(source, index)}") }
            copy.destinations.forEachIndexed { operandIndex, destination -> locals.writeWord(destination, "copy${index}_$operandIndex", indent) }
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
    locals.declarations("        ")

    fun emitBlock(block: KotlinBlock, indent: String, transition: (Int, String) -> Unit) {
        appendLine("${indent}onBlock(${block.size})")
        for (index in block.startOffset until block.endOffset) {
            val value = values[index]
            val copy = copies[index]
            val branch = branches[index]
            when {
                value != null -> {
                    locals.emit(index, value, indent)
                }
                copy != null -> emitCopies(copy, index, indent)
                branch != null -> {
                    val condition = branch.condition?.let { it.resultType.encode(it.operationExpression(index, locals::read)) }
                    if (branch.table) {
                        appendLine("${indent}val selector = ($condition).toInt()")
                        appendLine("${indent}pc = if (selector >= 0 && selector < ${branch.targets.lastIndex}) i$index.targetIps[selector] - baseIp else ${branch.targets.last() - firstIp}")
                    } else if (condition == null) {
                        branch.copies?.let { emitCopies(it, index, indent) }
                        transition(branch.targets.single() - firstIp, indent)
                    } else {
                        appendLine("${indent}if (($condition) ${if (branch.branchOnMatch) "!=" else "=="} 0L) {")
                        branch.copies?.let { emitCopies(it, index, "$indent    ") }
                        transition(branch.targets.single() - firstIp, "$indent    ")
                        appendLine("$indent} else {")
                        transition(index + 1, "$indent    ")
                        appendLine("$indent}")
                    }
                }
                else -> {
                    save(indent)
                    appendLine("$indent${checkNotNull(calls[index]).function}(vstack, context, i$index)")
                    reload(indent)
                }
            }
        }
        if (branches[block.endOffset - 1] == null) transition(block.endOffset, indent)
    }

    fun emitLoop(loop: KotlinStructuredLoop, indent: String, linear: Boolean) {
        appendLine("${indent}loop${loop.start}@ while (true) {")
        for (block in loop.blocks) {
            emitBlock(block, "$indent    ") { target, lineIndent ->
                when {
                    target == loop.start -> appendLine("${lineIndent}continue@loop${loop.start}")
                    target == block.endOffset && target < loop.end -> Unit
                    else -> {
                        if (!linear) appendLine("${lineIndent}pc = $target")
                        appendLine("${lineIndent}break@loop${loop.start}")
                    }
                }
            }
        }
        appendLine("$indent}")
    }
    if (plan?.linearExit != null) {
        appendLine("        require(entry == ${blocks.first().startOffset}) { \"Invalid structured entry\" }")
        for (node in plan.nodes) {
            if (node.loop != null) {
                emitLoop(node.loop, "        ", true)
            } else {
                emitBlock(node.blocks.single(), "        ") { _, _ -> }
            }
        }
    } else {
        appendLine("        var pc = entry")
        appendLine("        execution@ while (true) {")
        appendLine("            when (pc) {")
        val nodes = plan?.nodes ?: blocks.map { KotlinStructuredNode(listOf(it)) }
        for (node in nodes) {
            appendLine("                ${node.start} -> {")
            if (node.loop != null) {
                emitLoop(node.loop, "                    ", false)
            } else {
                emitBlock(node.blocks.single(), "                    ") { target, indent -> appendLine("${indent}pc = $target") }
            }
            appendLine("                }")
        }
        appendLine("                else -> break@execution")
        appendLine("            }")
        appendLine("        }")
    }
    save("        ")
    appendLine("        return baseIp + ${plan?.linearExit?.toString() ?: "pc"}")
    appendLine("    }")
    appendLine("}")
}
