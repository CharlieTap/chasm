package io.github.charlietap.chasm.compiler.backend.jvm

import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.cir.FunctionIndex
import io.github.charlietap.chasm.compiled.format.UNPINNED_OFFSET
import io.github.charlietap.chasm.compiler.backend.abi.EntryShape
import io.github.charlietap.chasm.compiler.backend.abi.GeneratedClassNames
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.ModuleSettings
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.internalName
import io.github.charlietap.chasm.compiler.backend.abi.typedCall
import io.github.charlietap.chasm.compiler.backend.analysis.DataflowSlotKindAnalyzer
import io.github.charlietap.chasm.compiler.backend.analysis.ModuleAnalysis
import io.github.charlietap.chasm.compiler.backend.analysis.ModuleIndexSpaces
import io.github.charlietap.chasm.compiler.backend.analysis.SlotKindAnalyzer
import io.github.charlietap.chasm.compiler.backend.analysis.analyzeModule
import io.github.charlietap.chasm.compiler.backend.analysis.functionBatches
import io.github.charlietap.chasm.compiler.backend.analysis.gc.CanonicalFunctionTypes
import io.github.charlietap.chasm.compiler.backend.analysis.gc.ConstantExpressions
import io.github.charlietap.chasm.compiler.backend.analysis.gc.ReferenceTests
import io.github.charlietap.chasm.compiler.backend.analysis.gc.RttKeys
import io.github.charlietap.chasm.compiler.backend.analysis.gc.RttTests
import io.github.charlietap.chasm.compiler.backend.analysis.gc.STORAGE_FAMILY
import io.github.charlietap.chasm.compiler.backend.analysis.gc.StoragePlans
import io.github.charlietap.chasm.compiler.backend.analysis.gc.StructLanes
import io.github.charlietap.chasm.compiler.backend.jvm.abi.BATCH_FUNCTIONS
import io.github.charlietap.chasm.compiler.backend.jvm.abi.BATCH_INSTRUCTIONS
import io.github.charlietap.chasm.compiler.backend.jvm.abi.CLASS_FILE_VERSION
import io.github.charlietap.chasm.compiler.backend.jvm.abi.SOURCE_FILE
import io.github.charlietap.chasm.compiler.backend.jvm.abi.bindingClassDescriptor
import io.github.charlietap.chasm.compiler.backend.jvm.abi.functionMethod
import io.github.charlietap.chasm.compiler.backend.jvm.binding.BindingLayout
import io.github.charlietap.chasm.compiler.backend.jvm.lower.InlineMemory
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FunctionType
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.MethodNode

internal class ModuleLowering(
    module: Module,
    code: CirModule,
    val options: JvmOptions,
) {
    val declarations = ModuleIndexSpaces(module)
    val analysis: ModuleAnalysis = analyzeModule(code, declarations, OBJECT_TYPE)
    val layout = BindingLayout()

    val definedTypes: List<DefinedType> = module.definedTypes
    val storagePlans = StoragePlans(module.definedTypes, STORAGE_FAMILY)
    val structLanes = StructLanes(storagePlans, module.definedTypes)
    val settings = ModuleSettings(options.runtime, options.target, UNPINNED_OFFSET)
    val constants = ConstantExpressions(module)
    val referenceTests = ReferenceTests(module.definedTypes)
    val rttTests = RttTests(module.definedTypes)
    val slotKindAnalyzer: SlotKindAnalyzer = DataflowSlotKindAnalyzer()

    val inlineMemory: InlineMemory? = when (options.memory) {
        MemoryLowering.HELPERS -> null
        MemoryLowering.BYTE_ARRAY -> InlineMemory.BYTES
        MemoryLowering.BYTE_BUFFER -> InlineMemory.BUFFER
        MemoryLowering.UNSAFE -> InlineMemory.UNSAFE
    }.takeIf { declarations.memoryCount > 0 }
    val entryPath = options.entryClass.replace('.', '/')
    val names = GeneratedClassNames(entryPath)
    val bindingType = bindingClassDescriptor(entryPath)

    val batches: List<List<CirFunction>> = functionBatches(code.functions, BATCH_INSTRUCTIONS, BATCH_FUNCTIONS)

    private val signatures = LinkedHashMap<String, String>()

    val typedMethods = LinkedHashMap<String, MethodRef>()

    init {
        code.functions.forEach { function ->
            val type = declarations.functionType(function.index)
            val signature = signatures.getOrPut(typedCall("", type).descriptor) { names.signature(signatures.size) }
            typedMethods.getOrPut(signature) { typedCall(signature, type) }
        }
    }

    fun signature(
        type: FunctionType,
    ): String? = signatures[typedCall("", type).descriptor]

    fun signature(shape: EntryShape): String {
        return signatures.getValue(shape.typedCall("").descriptor)
    }

    fun entryShape(index: FunctionIndex): EntryShape {
        return EntryShape(declarations.functionType(index), OBJECT_TYPE)
    }

    private val canonicalFunctionTypes = CanonicalFunctionTypes(RttKeys(module.definedTypes))

    private val functionTypeKeys = arrayOfNulls<String>(declarations.typeCount)

    fun functionTypeKey(index: FunctionIndex): String {

        val typeIndex = declarations.functionTypeIndex(index)
        val cachedKey = functionTypeKeys[typeIndex.index]
        if (cachedKey != null) {
            return cachedKey
        }

        val key = canonicalFunctionTypes.key(declarations.functionType(typeIndex))
        functionTypeKeys[typeIndex.index] = key

        return key
    }

    fun typedCall(
        signature: String,
        type: FunctionType,
    ): MethodRef {
        return typedCall(signature, type, OBJECT_TYPE)
    }

    private val batchOf = HashMap<Int, Int>().apply {
        batches.forEachIndexed { batch, functions -> functions.forEach { function -> put(function.index.index, batch) } }
    }

    fun functionMethod(
        index: FunctionIndex,
    ): MethodRef =
        functionMethod(names.code(batchOf.getValue(index.index)), bindingType, index.index, declarations.functionType(index))

    fun methodsClass(
        owner: String,
        public: Boolean,
        methods: List<MethodNode>,
    ): ByteArray = ClassWriter(ClassWriter.COMPUTE_MAXS).apply {
        val access = (if (public) Opcodes.ACC_PUBLIC else 0) or Opcodes.ACC_FINAL or Opcodes.ACC_SUPER
        visit(CLASS_FILE_VERSION, access, internalName(owner), null, OBJECT, null)
        if (options.stacktraces) visitSource(SOURCE_FILE, null)
        methods.forEach { method -> method.accept(this) }
        visitEnd()
    }.toByteArray()

    companion object {
        const val OBJECT = "java/lang/Object"
    }
}
