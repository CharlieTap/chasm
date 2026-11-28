package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.cir.FunctionIndex
import io.github.charlietap.chasm.cir.MemoryIndex
import io.github.charlietap.chasm.cir.TypeIndex
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.typedCall
import io.github.charlietap.chasm.compiler.backend.analysis.ModuleAnalysis
import io.github.charlietap.chasm.compiler.backend.analysis.ModuleIndexSpaces
import io.github.charlietap.chasm.compiler.backend.analysis.gc.ReferenceTests
import io.github.charlietap.chasm.compiler.backend.analysis.gc.RttTests
import io.github.charlietap.chasm.compiler.backend.analysis.gc.StoragePlans
import io.github.charlietap.chasm.compiler.backend.analysis.gc.StructSizes
import io.github.charlietap.chasm.compiler.backend.dex.abi.DexReferences
import io.github.charlietap.chasm.compiler.backend.dex.abi.FunctionSignature
import io.github.charlietap.chasm.compiler.backend.dex.abi.Invocation
import io.github.charlietap.chasm.compiler.backend.dex.abi.SOURCE_FILE
import io.github.charlietap.chasm.compiler.backend.dex.analysis.CostFamilies
import io.github.charlietap.chasm.compiler.backend.dex.binding.BatchConstants
import io.github.charlietap.chasm.compiler.backend.dex.binding.BindingLayout
import io.github.charlietap.dexter.ClassBuilder
import io.github.charlietap.dexter.ClassType
import io.github.charlietap.dexter.DexFile
import io.github.charlietap.dexter.DexType

internal class ModuleLowering(
    val plan: ModulePlan,
    val chunkStarts: Array<IntArray?> = arrayOfNulls(plan.functionCount),
) {

    constructor(
        module: Module,
        code: CirModule,
        options: DexOptions,
    ) : this(ModulePlan(module, code, options))

    val options: DexOptions get() = plan.options
    val declarations: ModuleIndexSpaces get() = plan.declarations
    val analysis: ModuleAnalysis get() = plan.analysis
    val layout: BindingLayout get() = plan.layout
    val referenceDescriptor: String get() = plan.referenceDescriptor
    val storagePlans: StoragePlans get() = plan.storagePlans
    val structSizes: StructSizes get() = plan.structSizes
    val rttTests: RttTests get() = plan.rttTests
    val referenceTests: ReferenceTests get() = plan.referenceTests
    val inlineMemory: MemoryIndex? get() = plan.inlineMemory
    val batches: List<List<CirFunction>> get() = plan.batches

    val references = DexReferences()
    val costFamilies = CostFamilies()
    val constants = BatchConstants()
    val workspace = LoweringWorkspace()
    val usedObjects = arrayOfNulls<Resource>(plan.layout.objectCount)
    val usedValues = arrayOfNulls<Resource>(plan.layout.valueCount)
    var emittedByteArrayBase = false

    fun objectSlot(resource: Resource): Int {
        val slot = plan.layout.objectSlot(resource)
        if (usedObjects[slot] == null) usedObjects[slot] = resource
        return slot
    }

    fun valueSlot(resource: Resource): Int {
        val slot = plan.layout.valueSlot(resource)
        if (usedValues[slot] == null) usedValues[slot] = resource
        return slot
    }

    fun functionSignature(index: TypeIndex): FunctionSignature {
        return plan.functionSignature(index)
    }

    fun signatureClass(index: TypeIndex): String? {
        return plan.signatureClass(plan.functionSignature(index).key)
    }

    fun signatureClass(key: String): String? {
        return plan.signatureClass(key)
    }

    fun codeClass(index: FunctionIndex): String {
        return plan.codeClass(index)
    }

    fun functionMethod(index: FunctionIndex): MethodRef {
        return plan.functionMethod(index)
    }

    fun functionInvocation(index: FunctionIndex): Invocation {
        return plan.functionInvocation(index)
    }

    fun codeClassBuilder(
        file: DexFile,
        owner: String,
    ): ClassBuilder {
        return file.addClass(ClassType(owner), DexType.Object, sourceFile = if (options.stacktraces) SOURCE_FILE else null)
    }

    fun addSignatureInterfaces(file: DexFile) {
        analysis.signatureTypes.forEach { (key, type) ->
            val owner = checkNotNull(plan.signatureClass(key))
            file.addInterface(ClassType(owner))
                .addAbstractMethod(references.of(typedCall(owner, type, referenceDescriptor)))
        }
    }
}
