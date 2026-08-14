package io.github.charlietap.chasm.compiler

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.compiler.context.CompilerContext
import io.github.charlietap.chasm.compiler.context.FunctionCompilerWorkspace
import io.github.charlietap.chasm.compiler.context.createCompilerContext
import io.github.charlietap.chasm.compiler.diagnostic.CompilerDiagnostics
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.parallel.ParallelTaskScope
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.function.Expression
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.ModuleTypeResolver
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap

suspend fun ParallelModuleCompiler(
    config: RuntimeConfig,
    store: Store,
    module: Module,
    instance: ModuleInstance,
    runtimeTypes: RuntimeTypeMap,
    types: ModuleTypeResolver = ModuleTypeResolver(module),
    diagnostics: CompilerDiagnostics? = null,
    taskExecutor: ParallelTaskExecutor,
): Result<Unit, ModuleTrapError> {
    val plan = if (diagnostics == null) {
        CompilationPlanner(module.functions, CompilationMode.AUTO)
    } else {
        CompilationPlan.Serial
    }
    val assignments = when (plan) {
        CompilationPlan.Serial -> {
            return ModuleCompiler(config, store, module, instance, runtimeTypes, types, diagnostics)
        }
        is CompilationPlan.Parallel -> plan.assignments
    }

    val context = createCompilerContext(
        config = config,
        module = module,
        types = types,
        store = store,
        instance = instance,
        runtimeTypes = runtimeTypes,
    )
    val compilationTasks: List<ParallelTaskScope.() -> Array<IndexedFunctionCompilation>> =
        assignments.map { assignment ->
            {
                val workspace = FunctionCompilerWorkspace()
                Array(assignment.size) { assignmentIndex ->
                    ensureActive()
                    val functionIndex = assignment[assignmentIndex]
                    IndexedFunctionCompilation(
                        functionIndex = functionIndex,
                        result = FunctionCompiler(context, module.functions[functionIndex], workspace),
                    )
                }
            }
        }
    val compilationGroups = taskExecutor.execute(compilationTasks)
    val compilations = arrayOfNulls<IndexedFunctionCompilation>(module.functions.size)
    for (groupIndex in compilationGroups.indices) {
        val group = compilationGroups[groupIndex]
        for (compilationIndex in group.indices) {
            val compilation = group[compilationIndex]
            compilations[compilation.functionIndex] = compilation
        }
    }

    return binding {
        val successfulCompilations = arrayOfNulls<FunctionCompilation>(compilations.size)
        for (index in compilations.indices) {
            successfulCompilations[index] = checkNotNull(compilations[index]).result.bind()
        }

        var containsGcInstructions = false
        for (index in compilations.indices) {
            val compilation = checkNotNull(successfulCompilations[index])
            val function = module.functions[index]
            val functionInstance = context.functions[function.idx.toInt()] as FunctionInstance.WasmFunction
            val entryIp = compilation.program.appendTo(store.program)
            val compiledFunction = compilation.function
            compiledFunction.body = Expression(entryIp)

            functionInstance.callPlan.install(
                entryIp = entryIp,
                frameSlots = compiledFunction.frameSlots,
            )
            functionInstance.function = compiledFunction
            containsGcInstructions = containsGcInstructions || compilation.containsGcInstructions
        }

        if (config.gcStrategy == GCStrategy.ARENA && containsGcInstructions) {
            markExportedFunctionsForGarbageCollection(context, module)
        }
    }
}

private fun markExportedFunctionsForGarbageCollection(
    context: CompilerContext,
    module: Module,
) {
    for (functionIndex in module.functions.indices) {
        val function = module.functions[functionIndex]
        val index = function.idx.toInt()
        if (!context.exportedFunctions[index]) continue

        val functionInstance = context.functions[index] as FunctionInstance.WasmFunction
        functionInstance.function.collectGarbageAfterInvocation = true
    }
}

private class IndexedFunctionCompilation(
    val functionIndex: Int,
    val result: Result<FunctionCompilation, ModuleTrapError>,
)
