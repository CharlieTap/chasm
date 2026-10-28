package io.github.charlietap.chasm.compiler.frontend

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiler.frontend.context.FunctionCompilerWorkspace
import io.github.charlietap.chasm.compiler.frontend.context.createCompilerContext
import io.github.charlietap.chasm.declarations.ModuleTypeResolver

typealias ModuleCompiler = (Module, ModuleTypeResolver) -> Result<CirModule, CompilationError>

fun ModuleCompiler(
    module: Module,
    types: ModuleTypeResolver = module.typeResolver(),
): Result<CirModule, CompilationError> = ModuleCompiler(
    module = module,
    types = types,
    functionCompiler = ::FunctionCompiler,
)

internal inline fun ModuleCompiler(
    module: Module,
    types: ModuleTypeResolver,
    crossinline functionCompiler: FunctionCompiler,
): Result<CirModule, CompilationError> = binding {
    val context = createCompilerContext(module, types)
    val workspace = FunctionCompilerWorkspace()
    val functions = arrayOfNulls<CirFunction>(module.functions.size)
    for (index in module.functions.indices) {
        functions[index] = functionCompiler(context, module.functions[index], workspace).bind()
    }
    @Suppress("UNCHECKED_CAST")
    CirModule(functions = (functions as Array<CirFunction>).asList())
}
