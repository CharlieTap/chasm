package io.github.charlietap.chasm.compiler

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.compiler.context.CompilerContext
import io.github.charlietap.chasm.compiler.context.FunctionCompilerWorkspace
import io.github.charlietap.chasm.compiler.context.createCompilerContext
import io.github.charlietap.chasm.compiler.diagnostic.CompilerDiagnostics
import io.github.charlietap.chasm.executor.invoker.dispatch.controlfused.LinkWasmCallDispatchers
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.parallel.ParallelTaskScope
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.function.classifyLocalInitialization
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.ModuleTypeResolver
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap

suspend fun ParallelModuleCompiler(
    store: Store,
    module: Module,
    instance: ModuleInstance,
    runtimeTypes: RuntimeTypeMap,
    types: ModuleTypeResolver = ModuleTypeResolver(module),
    diagnostics: CompilerDiagnostics? = null,
    taskExecutor: ParallelTaskExecutor,
): Result<Unit, ModuleTrapError> {
    val strategy = if (diagnostics == null) {
        selectCompilationStrategy(module.functions, CompilationMode.AUTO)
    } else {
        CompilationStrategy.Serial
    }
    val assignments = when (strategy) {
        CompilationStrategy.Serial -> {
            return ModuleCompiler(store, module, instance, runtimeTypes, types, diagnostics)
        }
        is CompilationStrategy.Parallel -> strategy.assignments
    }

    val context = createCompilerContext(
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
        val firstModuleIp = store.program.size
        val successfulCompilations = arrayOfNulls<FunctionCompilation>(compilations.size)
        for (index in compilations.indices) {
            successfulCompilations[index] = checkNotNull(compilations[index]).result.bind()
        }

        for (index in compilations.indices) {
            val compilation = checkNotNull(successfulCompilations[index])
            val function = module.functions[index]
            val functionInstance = context.functions[function.idx.toInt()] as FunctionInstance.WasmFunction
            val entryIp = compilation.program.appendTo(store.program)
            val compiledFunction = compilation.function

            val callStrategy = functionInstance.callStrategy
            callStrategy.frameSlots = compiledFunction.frameSlots
            callStrategy.localInitialization = classifyLocalInitialization(compiledFunction.localInitialValues)
            callStrategy.entryIp = entryIp
        }
        LinkWasmCallDispatchers(store.program, firstModuleIp)
    }
}

private class IndexedFunctionCompilation(
    val functionIndex: Int,
    val result: Result<FunctionCompilation, ModuleTrapError>,
)
