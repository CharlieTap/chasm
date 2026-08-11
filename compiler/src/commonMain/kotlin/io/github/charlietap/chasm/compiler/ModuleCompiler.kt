package io.github.charlietap.chasm.compiler

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.compiler.context.CompilerContext
import io.github.charlietap.chasm.compiler.diagnostic.CompilerDiagnostics
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.ModuleTypeResolver
import io.github.charlietap.chasm.type.RTT

fun ModuleCompiler(
    config: RuntimeConfig,
    store: Store,
    module: Module,
    instance: ModuleInstance,
    runtimeTypes: List<RTT>,
    types: ModuleTypeResolver = ModuleTypeResolver(module),
    diagnostics: CompilerDiagnostics? = null,
): Result<Unit, ModuleTrapError> = binding {
    val context = CompilerContext(
        config = config,
        module = module,
        types = types,
        store = store,
        instance = instance,
        runtimeTypes = runtimeTypes,
        diagnostics = diagnostics,
    )

    for (functionIndex in module.functions.indices) {
        val function = module.functions[functionIndex]
        val address = instance.functionAddresses[function.idx.toInt()]
        val functionInstance = store.function(address) as FunctionInstance.WasmFunction
        val entryIp = store.program.size
        val compiled = FunctionCompiler(context, function, store.program).bind()

        functionInstance.callPlan.install(
            entryIp = entryIp,
            frameSlots = compiled.frameSlots,
        )
        functionInstance.function = compiled
    }

    if (config.gcStrategy == GCStrategy.ARENA && context.containsGcInstructions) {
        for (functionIndex in module.functions.indices) {
            val function = module.functions[functionIndex]
            val index = function.idx.toInt()
            if (!context.exportedFunctions[index]) continue

            val address = instance.functionAddresses[index]
            val functionInstance = store.function(address) as FunctionInstance.WasmFunction
            functionInstance.function.collectGarbageAfterInvocation = true
        }
    }
}
