package io.github.charlietap.chasm.validator.context

import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Module

internal interface RefsContext {
    val refs: Set<Index.FunctionIndex>
}

internal data class RefsContextImpl(
    val module: Module,
) : RefsContext {

    private fun exportFunctionReferencesIndices() = module.exports.mapNotNullTo(mutableSetOf()) { export ->
        when (val descriptor = export.descriptor) {
            is Export.Descriptor.Function -> descriptor.functionIndex
            is Export.Descriptor.Global,
            is Export.Descriptor.Memory,
            is Export.Descriptor.Table,
            is Export.Descriptor.Tag,
            -> null
        }
    }

    private fun globalFunctionReferences() = module.globals.flatMap { global ->
        global.initExpression.instructions.filterIsInstance<ReferenceInstruction.RefFunc>()
    }

    private fun tableFunctionReferences() = module.tables.flatMap { table ->
        table.initExpression.instructions.filterIsInstance<ReferenceInstruction.RefFunc>()
    }

    private fun dataSegmentFunctionReferences() = module.dataSegments.flatMap { dataSegment ->
        when (val mode = dataSegment.mode) {
            is DataSegment.Mode.Active -> mode.offset.instructions.filterIsInstance<ReferenceInstruction.RefFunc>()
            is DataSegment.Mode.Passive -> emptyList()
        }
    }

    private fun elementSegmentFunctionReferences() = module.elementSegments.flatMap { elementSegment ->

        val references = elementSegment.initExpressions.flatMap { expression ->
            expression.instructions.filterIsInstance<ReferenceInstruction.RefFunc>()
        }

        val modeReferences = when (val mode = elementSegment.mode) {
            is ElementSegment.Mode.Active -> references + mode.offsetExpr.instructions.filterIsInstance<ReferenceInstruction.RefFunc>()
            is ElementSegment.Mode.Declarative -> null
            is ElementSegment.Mode.Passive -> null
        } ?: emptyList()

        references + modeReferences
    }

    override val refs by lazy {

        val references = globalFunctionReferences() + tableFunctionReferences() + dataSegmentFunctionReferences() + elementSegmentFunctionReferences()
        val indices = references.mapTo(mutableSetOf(), ReferenceInstruction.RefFunc::funcIdx)

        indices + exportFunctionReferencesIndices()
    }
}
