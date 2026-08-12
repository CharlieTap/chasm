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

internal class RefsContextImpl : RefsContext {

    private val functionReferences = mutableSetOf<Index.FunctionIndex>()

    override val refs: Set<Index.FunctionIndex>
        get() = functionReferences

    internal fun reset(module: Module) {
        functionReferences.clear()
        collectReferences(module)
    }

    internal fun clear() {
        functionReferences.clear()
    }

    private fun collectReferences(module: Module) {
        module.exports.forEach { export ->
            val descriptor = export.descriptor
            if (descriptor is Export.Descriptor.Function) {
                functionReferences += descriptor.functionIndex
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

    private fun collectReferences(instructions: List<Instruction>) {
        instructions.forEach { instruction ->
            if (instruction is ReferenceInstruction.RefFunc) {
                functionReferences += instruction.funcIdx
            }
        }
    }
}
