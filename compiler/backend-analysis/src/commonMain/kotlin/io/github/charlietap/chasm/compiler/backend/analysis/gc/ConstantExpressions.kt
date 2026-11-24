package io.github.charlietap.chasm.compiler.backend.analysis.gc

import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.type.ValueType

class ConstantExpressions(
    private val module: Module,
) {
    private val globalTypes = module.imports.mapNotNull { import -> (import.descriptor as? Import.Descriptor.Global)?.type?.valueType } +
        module.globals.map { global -> global.type.valueType }

    val globals: List<Int> = module.globals.indices.filter { index ->
        val global = module.globals[index]
        global.type.valueType is ValueType.Reference && needsCode(global.initExpression)
    }

    val tables: List<Int> = module.tables.indices.filter { index -> needsCode(module.tables[index].initExpression) }

    val elements: List<Int> = module.elementSegments.indices.filter { index -> module.elementSegments[index].initExpressions.any(::needsCode) }

    val needed: Boolean = globals.isNotEmpty() || tables.isNotEmpty() || elements.isNotEmpty()

    private fun needsCode(expression: Expression): Boolean {
        return expression.instructions.any { instruction ->
            when (instruction) {
                is AggregateInstruction.StructNew,
                is AggregateInstruction.StructNewDefault,
                is AggregateInstruction.ArrayNew,
                is AggregateInstruction.ArrayNewDefault,
                is AggregateInstruction.ArrayNewFixed,
                AggregateInstruction.RefI31,
                -> true
                is VariableInstruction.GlobalGet -> globalTypes[instruction.globalIdx.idx.toInt()] is ValueType.Reference
                else -> false
            }
        }
    }
}
