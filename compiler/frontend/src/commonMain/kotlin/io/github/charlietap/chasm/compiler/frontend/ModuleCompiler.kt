package io.github.charlietap.chasm.compiler.frontend

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiler.frontend.context.FunctionCompilerWorkspace
import io.github.charlietap.chasm.compiler.frontend.context.createCompilerContext
import io.github.charlietap.chasm.declarations.ModuleTypeResolver

@InternalChasmApi
typealias ModuleCompiler = (Module, ModuleTypeResolver) -> Result<CirModule, CompilationError>

@InternalChasmApi
fun ModuleCompiler(
    module: Module,
    types: ModuleTypeResolver = module.typeResolver(),
): Result<CirModule, CompilationError> {
    return ModuleCompiler(
        module = module,
        types = types,
        devirtualiseIndirectCalls = false,
        functionCompiler = ::FunctionCompiler,
    )
}

@InternalChasmApi
fun ModuleCompiler(
    module: Module,
    types: ModuleTypeResolver,
    recordProvenance: Boolean,
    devirtualiseIndirectCalls: Boolean = false,
): Result<CirModule, CompilationError> {
    return ModuleCompiler(
        module = module,
        types = types,
        devirtualiseIndirectCalls = devirtualiseIndirectCalls,
        functionCompiler = { context, function, workspace -> FunctionCompiler(context, function, workspace, recordProvenance) },
    )
}

internal inline fun ModuleCompiler(
    module: Module,
    types: ModuleTypeResolver,
    devirtualiseIndirectCalls: Boolean,
    crossinline functionCompiler: FunctionCompiler,
): Result<CirModule, CompilationError> = binding {
    val context = createCompilerContext(module, types, devirtualiseIndirectCalls)
    val workspace = FunctionCompilerWorkspace()
    val functions = arrayOfNulls<CirFunction>(module.functions.size)
    for (index in module.functions.indices) {
        functions[index] = functionCompiler(context, module.functions[index], workspace).bind()
    }
    @Suppress("UNCHECKED_CAST")
    CirModule(functions = (functions as Array<CirFunction>).asList())
}
