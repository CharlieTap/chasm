package io.github.charlietap.chasm.compiler.frontend

import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.declarations.ModuleTypeResolver

typealias ModuleRewriter = (Module, ModuleTypeResolver) -> Module

fun ModuleRewriter(
    module: Module,
    types: ModuleTypeResolver,
    preserveSource: Boolean,
): Module = ModuleRewriter(
    module = module,
    types = types,
    preserveSource = preserveSource,
    indirectCallDevirtualiser = ::IndirectCallDevirtualiser,
)

internal inline fun ModuleRewriter(
    module: Module,
    types: ModuleTypeResolver,
    preserveSource: Boolean,
    crossinline indirectCallDevirtualiser: IndirectCallDevirtualiser,
): Module {
    if (preserveSource) return module
    return indirectCallDevirtualiser(module, types)
}
