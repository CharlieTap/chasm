package io.github.charlietap.chasm.compiler.frontend

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.declarations.ModuleTypeResolver
import io.github.charlietap.chasm.type.AddressType

internal class Arms(
    val indices: IntArray,
    val functions: IntArray,
) {
    val size: Int
        get() = indices.size
}

internal class IndirectCallTargets(
    private val armsBySite: Map<Long, Arms>,
) {
    fun at(
        table: Int,
        type: Int,
    ): Arms? {
        return armsBySite[siteKey(table, type)]
    }
}

internal class IndirectCallScan(
    tableCount: Int,
) {
    val called = BooleanArray(tableCount)
    val opened = BooleanArray(tableCount)
    val sites = HashSet<Long>()

    fun scan(
        module: Module,
        from: Int,
        until: Int,
    ) {
        for (function in from until until) {
            for (instruction in module.functions[function].body.instructions) {
                when (instruction) {
                    is ControlInstruction.CallIndirect -> {
                        val table = instruction.tableIndex.toInt()
                        called[table] = true
                        sites += siteKey(table, instruction.typeIndex.toInt())
                    }
                    is TableInstruction.TableSet -> opened[instruction.tableIdx.toInt()] = true
                    is TableInstruction.TableGrow -> opened[instruction.tableIdx.toInt()] = true
                    is TableInstruction.TableFill -> opened[instruction.tableIdx.toInt()] = true
                    is TableInstruction.TableInit -> opened[instruction.tableIdx.toInt()] = true
                    is TableInstruction.TableCopy -> opened[instruction.destTableIdx.toInt()] = true
                    else -> Unit
                }
            }
        }
    }
}

internal fun indirectCallTables(module: Module): Int? {
    return initiallyOpenTables(module)?.size
}

internal fun indirectCallTargets(
    module: Module,
    types: ModuleTypeResolver,
): IndirectCallTargets? {

    val tableCount = indirectCallTables(module) ?: return null
    val scan = IndirectCallScan(tableCount).also { scan -> scan.scan(module, 0, module.functions.size) }

    return indirectCallTargets(module, types, listOf(scan))
}

internal fun indirectCallTargets(
    module: Module,
    types: ModuleTypeResolver,
    scans: List<IndirectCallScan>,
): IndirectCallTargets? {

    val open = initiallyOpenTables(module) ?: return null
    val called = BooleanArray(open.size)
    val sites = HashSet<Long>()
    for (scan in scans) {
        for (table in open.indices) {
            if (scan.called[table]) called[table] = true
            if (scan.opened[table]) open[table] = true
        }
        sites += scan.sites
    }
    for (table in open.indices) {
        if (!called[table]) {
            open[table] = true
        }
    }

    val tables = tableEntries(module, open) ?: return null
    val armsBySite = HashMap<Long, Arms>()
    for (site in sites) {
        val entries = tables[siteTable(site)] ?: continue
        val type = Index.TypeIndex(siteType(site).toUInt())
        if (types.functionType(type).results.types.size > 1) {
            continue
        }
        val arms = arms(entries, type, types) ?: continue
        armsBySite[site] = arms
    }

    return if (armsBySite.isEmpty()) null else IndirectCallTargets(armsBySite)
}

private fun initiallyOpenTables(module: Module): BooleanArray? {

    val imported = module.imports.count { import -> import.descriptor is Import.Descriptor.Table }
    val open = BooleanArray(imported + module.tables.size)
    open.fill(true, 0, imported)
    module.tables.forEachIndexed { index, table ->
        val type = table.type
        if (type.addressType != AddressType.I32 || type.limits.min > MAX_TABLE_ENTRIES.toULong()) {
            open[imported + index] = true
        }
    }
    module.exports.forEach { export ->
        val descriptor = export.descriptor
        if (descriptor is Export.Descriptor.Table) {
            open[descriptor.tableIndex.toInt()] = true
        }
    }

    return if (open.all { table -> table }) null else open
}

private fun tableEntries(
    module: Module,
    open: BooleanArray,
): Array<IntArray?>? {

    if (open.all { table -> table }) {
        return null
    }

    val imported = open.size - module.tables.size
    val tables = arrayOfNulls<IntArray>(open.size)
    module.tables.forEachIndexed { index, table ->
        if (open[imported + index]) {
            return@forEachIndexed
        }
        tables[imported + index] = IntArray(table.type.limits.min.toInt()).also { entries -> entries.fill(entry(table.initExpression)) }
    }

    var certain = true
    for (segment in module.elementSegments) {
        val mode = segment.mode as? ElementSegment.Mode.Active ?: continue
        val table = mode.tableIndex.toInt()
        if (table < imported) {
            certain = false
            continue
        }
        val offset = (mode.offsetExpr.instructions.singleOrNull() as? NumericInstruction.I32Const)?.value
        if (offset == null) {
            tables[table] = null
            certain = false
            continue
        }
        val end = offset.toUInt().toULong() + segment.initExpressions.size.toULong()
        if (end > module.tables[table - imported].type.limits.min) {
            certain = false
            continue
        }
        val entries = tables[table] ?: continue
        segment.initExpressions.forEachIndexed { index, expression ->
            entries[offset + index] = if (certain) entry(expression) else NO_FUNCTION
        }
    }

    return if (tables.all { entries -> entries == null }) null else tables
}

private fun entry(expression: Expression): Int {
    return (expression.instructions.singleOrNull() as? ReferenceInstruction.RefFunc)?.funcIdx?.toInt() ?: NO_FUNCTION
}

private fun arms(
    entries: IntArray,
    type: Index.TypeIndex,
    types: ModuleTypeResolver,
): Arms? {

    var count = 0
    for (function in entries) {
        if (matches(function, type, types) && ++count > MAX_ARMS) {
            return null
        }
    }
    if (count == 0) {
        return null
    }

    val indices = IntArray(count)
    val functions = IntArray(count)
    var arm = 0
    entries.forEachIndexed { index, function ->
        if (matches(function, type, types)) {
            indices[arm] = index
            functions[arm++] = function
        }
    }
    return Arms(indices, functions)
}

private fun matches(
    function: Int,
    type: Index.TypeIndex,
    types: ModuleTypeResolver,
): Boolean {
    return function != NO_FUNCTION && types.functionTypeIndex(Index.FunctionIndex(function.toUInt())) == type
}

private fun siteKey(
    table: Int,
    type: Int,
): Long {
    return (table.toLong() shl 32) or (type.toLong() and 0xFFFFFFFFL)
}

private fun siteTable(site: Long): Int {
    return (site ushr 32).toInt()
}

private fun siteType(site: Long): Int {
    return site.toInt()
}

private const val NO_FUNCTION = -1

private const val MAX_ARMS = 4

private const val MAX_TABLE_ENTRIES = 1 shl 20
