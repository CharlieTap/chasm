package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.cir.FunctionIndex
import io.github.charlietap.chasm.cir.MemoryIndex
import io.github.charlietap.chasm.cir.TypeIndex
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.analysis.ModuleAnalysis
import io.github.charlietap.chasm.compiler.backend.analysis.ModuleIndexSpaces
import io.github.charlietap.chasm.compiler.backend.analysis.analyzeModule
import io.github.charlietap.chasm.compiler.backend.analysis.functionBatches
import io.github.charlietap.chasm.compiler.backend.analysis.gc.ReferenceTests
import io.github.charlietap.chasm.compiler.backend.analysis.gc.ShapeLayouts
import io.github.charlietap.chasm.compiler.backend.analysis.gc.ShapeSizes
import io.github.charlietap.chasm.compiler.backend.dex.abi.BATCH_FUNCTIONS
import io.github.charlietap.chasm.compiler.backend.dex.abi.BATCH_INSTRUCTIONS
import io.github.charlietap.chasm.compiler.backend.dex.abi.DexReferences
import io.github.charlietap.chasm.compiler.backend.dex.abi.FunctionSignature
import io.github.charlietap.chasm.compiler.backend.dex.abi.Invocation
import io.github.charlietap.chasm.compiler.backend.dex.abi.codeClass
import io.github.charlietap.chasm.compiler.backend.dex.abi.functionMethod
import io.github.charlietap.chasm.compiler.backend.dex.abi.signatureClass
import io.github.charlietap.chasm.compiler.backend.dex.binding.BindingLayout
import io.github.charlietap.chasm.compiler.backend.dex.gc.StructuralShapeNames
import io.github.charlietap.chasm.runtime.execution.ARRAY_OVERHEAD

internal class ModulePlan(
    val module: Module,
    code: CirModule,
    val options: DexOptions,
    val declarations: ModuleIndexSpaces = ModuleIndexSpaces(module),
    val analysis: ModuleAnalysis = analyzeModule(code, declarations, OBJECT_TYPE),
) {

    val referenceDescriptor = OBJECT_TYPE
    val gc = ShapeLayouts(module.definedTypes, StructuralShapeNames())
    val shapeSizes = ShapeSizes(ARRAY_OVERHEAD)
    val referenceTests = ReferenceTests(module.definedTypes)

    val layout = BindingLayout(declarations, analysis)
    val packagePath = options.packageName.replace('.', '/')
    val inlineMemory: MemoryIndex? = MemoryIndex(0).takeIf { options.inlineMemory && declarations.memoryCount == 1 }
    val functionCount = declarations.functionCount
    val functions: List<CirFunction> = code.functions
    val batches: List<List<CirFunction>> = functionBatches(code.functions, BATCH_INSTRUCTIONS, BATCH_FUNCTIONS)

    private val signatures: Map<String, String> = analysis.signatureTypes.keys.withIndex().associate { (index, key) ->
        key to signatureClass(packagePath, index)
    }

    private val batchOf = IntArray(functionCount) { NO_BATCH }.also { batchOf ->
        batches.forEachIndexed { batch, functions -> functions.forEach { function -> batchOf[function.index.index] = batch } }
    }

    private val functionSignatures = Array(declarations.typeCount) { type ->
        val index = TypeIndex(type)
        if (declarations.isFunctionType(index)) FunctionSignature(declarations.functionType(index), referenceDescriptor) else null
    }

    private val functionMethods = arrayOfNulls<MethodRef>(functionCount)
    private val functionInvocations = arrayOfNulls<Invocation>(functionCount)

    init {

        val references = DexReferences()
        for (function in code.functions) {
            val index = function.index.index
            val method = functionMethod(codeClass(packagePath, batchOf[index]), index, declarations.functionType(function.type), referenceDescriptor)
            functionMethods[index] = method
            functionInvocations[index] = references.invocation(method)
        }
    }

    fun functionSignature(index: TypeIndex): FunctionSignature {
        return checkNotNull(functionSignatures[index.index]) { "type ${index.index} is not a function type" }
    }

    fun signatureClass(key: String): String? {
        return signatures[key]
    }

    fun codeClass(index: FunctionIndex): String {
        return codeClass(packagePath, batchOf[index.index])
    }

    fun functionMethod(index: FunctionIndex): MethodRef {
        return checkNotNull(functionMethods[index.index]) { "function ${index.index} has no method" }
    }

    fun functionInvocation(index: FunctionIndex): Invocation {
        return checkNotNull(functionInvocations[index.index]) { "function ${index.index} has no method" }
    }

    fun batchWork(batch: Int): Int {
        return batches[batch].sumOf { function -> function.instructions.size + FUNCTION_WORK }
    }

    private companion object {
        const val NO_BATCH = -1
    }
}

internal const val FUNCTION_WORK = 64
