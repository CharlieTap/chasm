package io.github.charlietap.chasm.compiler.frontend

import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.declarations.ModuleTypeResolver

class ModuleRewriter internal constructor(
    private val preserveSource: Boolean,
    private val inlineLeaves: Boolean,
    private val leafCallInliner: LeafCallInliner,
) {
    constructor(
        preserveSource: Boolean,
        inlineLeaves: Boolean,
    ) : this(
        preserveSource = preserveSource,
        inlineLeaves = inlineLeaves,
        leafCallInliner = LeafCallInliner(LeafAnalyzer(), FunctionInliner()),
    )

    fun rewrite(
        module: Module,
        types: ModuleTypeResolver,
    ): Module {

        if (preserveSource || !inlineLeaves) {
            return module
        }

        return leafCallInliner.rewrite(module, types)
    }
}
