package io.github.charlietap.chasm.compiler.backend.jvm

import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.cir.FunctionIndex
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.internalName
import io.github.charlietap.chasm.compiler.backend.abi.returnDescriptor
import io.github.charlietap.chasm.compiler.backend.abi.staticParameters
import io.github.charlietap.chasm.compiler.backend.analysis.ModuleAnalysis
import io.github.charlietap.chasm.compiler.backend.analysis.ModuleIndexSpaces
import io.github.charlietap.chasm.compiler.backend.analysis.analyzeModule
import io.github.charlietap.chasm.compiler.backend.analysis.functionBatches
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
import io.github.charlietap.chasm.compiler.backend.jvm.lower.InlineMemory
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
    val analysis: ModuleAnalysis = analyzeModule(module, code, declarations)
    val layout = BindingLayout()

    val inlineMemory: InlineMemory? = when (options.memory) {
        MemoryLowering.HELPERS -> null
        MemoryLowering.BYTE_ARRAY -> InlineMemory.BYTES
        MemoryLowering.UNSAFE -> InlineMemory.UNSAFE
    }.takeIf { declarations.memoryCount > 0 }
    val packagePath = options.packageName.replace('.', '/')
    val bindingType = bindingClassDescriptor(packagePath)

    val batches: List<List<CirFunction>> = functionBatches(code.functions, BATCH_INSTRUCTIONS, BATCH_FUNCTIONS)

    private val signatures = LinkedHashMap<String, String>()

    val typedMethods = LinkedHashMap<String, MethodRef>()

    init {
        code.functions.forEach { function ->
            val type = declarations.functionType(function.index)
            val signature = signatures.getOrPut(typedCall("", type).descriptor) { signatureClassDescriptor(packagePath, signatures.size) }
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
        MethodRef(signature, TYPED_CALL, staticParameters(type, bindingType).drop(1), returnDescriptor(type))

    private val batchOf = HashMap<Int, Int>().apply {
        batches.forEachIndexed { batch, functions -> functions.forEach { function -> put(function.index.index, batch) } }
    }

    fun functionMethod(
        index: FunctionIndex,
    ): MethodRef =
        functionMethod(codeClassDescriptor(packagePath, batchOf.getValue(index.index)), bindingType, index.index, declarations.functionType(index))

    fun methodsClass(
        owner: String,
        public: Boolean,
        methods: List<MethodNode>,
    ): ByteArray = ClassWriter(ClassWriter.COMPUTE_MAXS).apply {
        val access = (if (public) Opcodes.ACC_PUBLIC else 0) or Opcodes.ACC_FINAL or Opcodes.ACC_SUPER
        visit(CLASS_FILE_VERSION, access, internalName(owner), null, OBJECT, null)
        if (options.lineNumbers) visitSource(SOURCE_FILE, null)
        methods.forEach { method -> method.accept(this) }
        visitEnd()
    }.toByteArray()

    companion object {
        const val OBJECT = "java/lang/Object"
    }
}
