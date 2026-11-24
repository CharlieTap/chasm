package io.github.charlietap.chasm.compiler.backend.jvm

import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.cir.FunctionIndex
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.internalName
import io.github.charlietap.chasm.compiler.backend.abi.returnDescriptor
import io.github.charlietap.chasm.compiler.backend.abi.staticParameters
import io.github.charlietap.chasm.compiler.backend.analysis.DataflowSlotKindAnalyzer
import io.github.charlietap.chasm.compiler.backend.analysis.ModuleAnalysis
import io.github.charlietap.chasm.compiler.backend.analysis.ModuleIndexSpaces
import io.github.charlietap.chasm.compiler.backend.analysis.SlotKindAnalyzer
import io.github.charlietap.chasm.compiler.backend.analysis.analyzeModule
import io.github.charlietap.chasm.compiler.backend.analysis.functionBatches
import io.github.charlietap.chasm.compiler.backend.analysis.gc.ConstantExpressions
import io.github.charlietap.chasm.compiler.backend.analysis.gc.ReferenceTests
import io.github.charlietap.chasm.compiler.backend.analysis.gc.STORAGE_FAMILY
import io.github.charlietap.chasm.compiler.backend.analysis.gc.StoragePlans
import io.github.charlietap.chasm.compiler.backend.jvm.abi.BATCH_FUNCTIONS
import io.github.charlietap.chasm.compiler.backend.jvm.abi.BATCH_INSTRUCTIONS
import io.github.charlietap.chasm.compiler.backend.jvm.abi.CLASS_FILE_VERSION
import io.github.charlietap.chasm.compiler.backend.jvm.abi.SOURCE_FILE
import io.github.charlietap.chasm.compiler.backend.jvm.abi.TYPED_CALL
import io.github.charlietap.chasm.compiler.backend.jvm.abi.bindingClassDescriptor
import io.github.charlietap.chasm.compiler.backend.jvm.abi.codeClassDescriptor
import io.github.charlietap.chasm.compiler.backend.jvm.abi.functionMethod
import io.github.charlietap.chasm.compiler.backend.jvm.abi.signatureClassDescriptor
import io.github.charlietap.chasm.compiler.backend.jvm.binding.BindingLayout
import io.github.charlietap.chasm.compiler.backend.jvm.gc.RttTests
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
    val bindingType = bindingClassDescriptor(entryPath)

    val batches: List<List<CirFunction>> = functionBatches(code.functions, BATCH_INSTRUCTIONS, BATCH_FUNCTIONS)

    private val signatures = LinkedHashMap<String, String>()

    val typedMethods = LinkedHashMap<String, MethodRef>()

    init {
        code.functions.forEach { function ->
            val type = declarations.functionType(function.index)
            val signature = signatures.getOrPut(typedCall("", type).descriptor) { signatureClassDescriptor(entryPath, signatures.size) }
            typedMethods.getOrPut(signature) { typedCall(signature, type) }
        }
    }

    fun signature(
        type: FunctionType,
    ): String? = signatures[typedCall("", type).descriptor]

    fun typedCall(
        signature: String,
        type: FunctionType,
    ): MethodRef =
        MethodRef(signature, TYPED_CALL, staticParameters(type, bindingType, OBJECT_TYPE).drop(1), returnDescriptor(type, OBJECT_TYPE))

    private val batchOf = HashMap<Int, Int>().apply {
        batches.forEachIndexed { batch, functions -> functions.forEach { function -> put(function.index.index, batch) } }
    }

    fun functionMethod(
        index: FunctionIndex,
    ): MethodRef =
        functionMethod(codeClassDescriptor(entryPath, batchOf.getValue(index.index)), bindingType, index.index, declarations.functionType(index))

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
