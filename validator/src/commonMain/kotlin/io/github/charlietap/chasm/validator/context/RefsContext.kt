package io.github.charlietap.chasm.validator.context

import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Module

internal interface RefsContext {
    val refs: Set<Index.FunctionIndex>
}

internal fun collectDeclaredFunctionReferences(module: Module): Set<Index.FunctionIndex> {
    return buildSet {
        module.exports.forEach { export ->
            val descriptor = export.descriptor
            if (descriptor is Export.Descriptor.Function) {
                add(descriptor.functionIndex)
            }
        }

        module.globals.forEach { global ->
            collectReferences(global.initExpression.instructions)
        }
        module.tables.forEach { table ->
            collectReferences(table.initExpression.instructions)
        }
        module.dataSegments.forEach { dataSegment ->
            val mode = dataSegment.mode
            if (mode is DataSegment.Mode.Active) {
                collectReferences(mode.offset.instructions)
            }
        }
        module.elementSegments.forEach { elementSegment ->
            elementSegment.initExpressions.forEach { expression ->
                collectReferences(expression.instructions)
            }
            val mode = elementSegment.mode
            if (mode is ElementSegment.Mode.Active) {
                collectReferences(mode.offsetExpr.instructions)
            }
        }
    }
}

private fun MutableSet<Index.FunctionIndex>.collectReferences(instructions: List<Instruction>) {
    instructions.forEach { instruction ->
        if (instruction is ReferenceInstruction.RefFunc) {
            add(instruction.funcIdx)
        }
    }
}
