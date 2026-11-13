package io.github.charlietap.chasm.compiler.frontend

import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.declarations.ModuleTypeResolver

class ModuleRewriter internal constructor(
    private val preserveSource: Boolean,
    private val inlineLeaves: Boolean,
    private val indirectCallDevirtualiser: IndirectCallDevirtualiser,
    private val leafCallInliner: LeafCallInliner,
) {
    constructor(
        preserveSource: Boolean,
        inlineLeaves: Boolean,
    ) : this(
        preserveSource = preserveSource,
        inlineLeaves = inlineLeaves,
        indirectCallDevirtualiser = ::IndirectCallDevirtualiser,
        leafCallInliner = LeafCallInliner(LeafAnalyzer(), FunctionInliner()),
    )

    fun rewrite(
        module: Module,
        types: ModuleTypeResolver,
    ): Module {

        if (preserveSource) return module

        val devirtualised = indirectCallDevirtualiser(module, types)
        if (!inlineLeaves) return devirtualised

        return leafCallInliner.rewrite(devirtualised, types)
    }
}
