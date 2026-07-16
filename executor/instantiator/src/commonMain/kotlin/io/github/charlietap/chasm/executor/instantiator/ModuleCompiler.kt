package io.github.charlietap.chasm.executor.instantiator

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.instantiator.compat.CompatibilityChecker
import io.github.charlietap.chasm.ir.factory.ModuleFactory
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.ast.module.Module as ASTModule
import io.github.charlietap.chasm.compiler.Compiler as IRCompiler

typealias ModuleCompiler = (RuntimeConfig, ASTModule) -> Result<CompiledModule, ModuleTrapError>

fun ModuleCompiler(
    config: RuntimeConfig,
    module: ASTModule,
): Result<CompiledModule, ModuleTrapError> =
    ModuleCompiler(
        config = config,
        module = module,
        compatibilityChecker = ::CompatibilityChecker,
        moduleFactory = ::ModuleFactory,
        compiler = ::IRCompiler,
    )

internal inline fun ModuleCompiler(
    config: RuntimeConfig,
    module: ASTModule,
    crossinline compatibilityChecker: CompatibilityChecker,
    crossinline moduleFactory: ModuleFactory,
    crossinline compiler: IRCompiler,
): Result<CompiledModule, ModuleTrapError> = binding {
    compatibilityChecker(module).bind()

    CompiledModule(
        module = compiler(config, moduleFactory(module)),
        sourceModule = module,
    )
}
