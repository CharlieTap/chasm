package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.cir.FunctionIndex
import io.github.charlietap.chasm.cir.MemoryIndex
import io.github.charlietap.chasm.cir.TypeIndex
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.analysis.ModuleAnalysis
import io.github.charlietap.chasm.compiler.backend.analysis.ModuleIndexSpaces
import io.github.charlietap.chasm.compiler.backend.analysis.analyzeModule
import io.github.charlietap.chasm.compiler.backend.analysis.functionBatches
import io.github.charlietap.chasm.compiler.backend.dex.abi.BATCH_FUNCTIONS
import io.github.charlietap.chasm.compiler.backend.dex.abi.BATCH_INSTRUCTIONS
import io.github.charlietap.chasm.compiler.backend.dex.abi.DexReferences
import io.github.charlietap.chasm.compiler.backend.dex.abi.FunctionSignature
import io.github.charlietap.chasm.compiler.backend.dex.abi.SOURCE_FILE
import io.github.charlietap.chasm.compiler.backend.dex.abi.codeClass
import io.github.charlietap.chasm.compiler.backend.dex.abi.functionMethod
import io.github.charlietap.chasm.compiler.backend.dex.abi.signatureClass
import io.github.charlietap.chasm.compiler.backend.dex.abi.typedCall
import io.github.charlietap.chasm.compiler.backend.dex.analysis.CostFamilies
import io.github.charlietap.chasm.compiler.backend.dex.binding.BindingLayout
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.dexter.ClassBuilder
import io.github.charlietap.dexter.ClassType
import io.github.charlietap.dexter.DexFile
import io.github.charlietap.dexter.DexType

internal class ModuleLowering(
    module: Module,
    code: CirModule,
    val options: DexOptions,
) {
    val declarations = ModuleIndexSpaces(module)
    val analysis: ModuleAnalysis = analyzeModule(module, code, declarations)
    val layout = BindingLayout()
    val references = DexReferences()
    val chunkStarts = HashMap<Int, IntArray>()
    val packagePath = options.packageName.replace('.', '/')

    val inlineMemory: MemoryIndex? = MemoryIndex(0).takeIf { options.inlineMemory && declarations.memoryCount == 1 }

    private val signatures: Map<String, String> = analysis.signatureTypes.keys.withIndex().associate { (index, key) ->
        key to signatureClass(packagePath, index)
    }

    val costFamilies = CostFamilies()

    private val functionMethods = arrayOfNulls<MethodRef>(declarations.functionCount)

    val batches: List<List<CirFunction>> = functionBatches(code.functions, BATCH_INSTRUCTIONS, BATCH_FUNCTIONS)

    private val batchOf = HashMap<Int, Int>().apply {
        batches.forEachIndexed { batch, functions -> functions.forEach { function -> put(function.index.index, batch) } }
    }

    private val functionSignatures = arrayOfNulls<FunctionSignature>(declarations.typeCount)

    fun functionSignature(index: TypeIndex): FunctionSignature {
        return functionSignatures[index.index]
            ?: FunctionSignature(declarations.functionType(index)).also { signature -> functionSignatures[index.index] = signature }
    }

    fun signatureClass(index: TypeIndex): String? {
        return signatureClass(functionSignature(index).key)
    }

    fun signatureClass(key: String): String? {
        return signatures[key]
    }

    fun codeClass(
        index: FunctionIndex,
    ): String = codeClass(packagePath, batchOf.getValue(index.index))

    fun functionMethod(
        index: FunctionIndex,
        type: FunctionType,
    ): MethodRef {
        return functionMethods[index.index]
            ?: functionMethod(codeClass(index), index.index, type).also { method -> functionMethods[index.index] = method }
    }

    fun codeClassBuilder(
        file: DexFile,
        owner: String,
    ): ClassBuilder = file.addClass(ClassType(owner), DexType.Object, sourceFile = if (options.stacktraces) SOURCE_FILE else null)

    fun addSignatureInterfaces(
        file: DexFile,
    ) {
        analysis.signatureTypes.forEach { (key, type) ->
            val owner = signatures.getValue(key)
            file.addInterface(ClassType(owner))
                .addAbstractMethod(references.of(typedCall(owner, type)))
        }
    }
}
