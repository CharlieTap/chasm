@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.compiler.backend.jvm

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.internal.COMPILED_RUNTIME_ABI
import io.github.charlietap.chasm.aot.internal.UNSET
import io.github.charlietap.chasm.aot.internal.UNSET_LONG
import io.github.charlietap.chasm.aot.internal.gcStrategyCode
import io.github.charlietap.chasm.aot.internal.gcThresholdKind
import io.github.charlietap.chasm.aot.internal.gcThresholdValue
import io.github.charlietap.chasm.aot.internal.trapSitesResource
import io.github.charlietap.chasm.compiler.backend.abi.CODE_INSTALLATION
import io.github.charlietap.chasm.compiler.backend.abi.INSTALL
import io.github.charlietap.chasm.compiler.backend.abi.internalName
import io.github.charlietap.chasm.compiler.backend.jvm.abi.BUILD_DECLARATIONS
import io.github.charlietap.chasm.compiler.backend.jvm.abi.CLASS_FILE_VERSION
import io.github.charlietap.chasm.compiler.backend.jvm.abi.COMPILED_CODE
import io.github.charlietap.chasm.compiler.backend.jvm.abi.COMPILED_MODULE
import io.github.charlietap.chasm.compiler.backend.jvm.abi.COMPILED_MODULE_FACTORY
import io.github.charlietap.chasm.compiler.backend.jvm.abi.DATA_SOURCE
import io.github.charlietap.chasm.compiler.backend.jvm.abi.DATA_SOURCE_TYPE
import io.github.charlietap.chasm.compiler.backend.jvm.abi.INSTALLS_PER_METHOD
import io.github.charlietap.chasm.compiler.backend.jvm.abi.MAX_FANOUT
import io.github.charlietap.chasm.compiler.backend.jvm.abi.META_GC_HEAP_BYTES
import io.github.charlietap.chasm.compiler.backend.jvm.abi.META_GC_STRATEGY
import io.github.charlietap.chasm.compiler.backend.jvm.abi.META_GC_THRESHOLD_KIND
import io.github.charlietap.chasm.compiler.backend.jvm.abi.META_GC_THRESHOLD_VALUE
import io.github.charlietap.chasm.compiler.backend.jvm.abi.META_INITIAL_FUEL
import io.github.charlietap.chasm.compiler.backend.jvm.abi.META_INTERRUPTIBLE
import io.github.charlietap.chasm.compiler.backend.jvm.abi.META_MEMORY_PAGES
import io.github.charlietap.chasm.compiler.backend.jvm.abi.META_METER_FUEL
import io.github.charlietap.chasm.compiler.backend.jvm.abi.META_STACKTRACES
import io.github.charlietap.chasm.compiler.backend.jvm.abi.META_STACK_BYTES
import io.github.charlietap.chasm.compiler.backend.jvm.abi.META_TABLE_ELEMENTS
import io.github.charlietap.chasm.compiler.backend.jvm.abi.META_TRAP_ON_GROW_FAILURE
import io.github.charlietap.chasm.compiler.backend.jvm.abi.MODULE_DECLARATIONS
import io.github.charlietap.chasm.compiler.backend.jvm.abi.RUNTIME_ABI
import io.github.charlietap.chasm.compiler.backend.jvm.abi.TRAP_SITES_RESOURCE
import io.github.charlietap.chasm.compiler.backend.jvm.abi.installClassDescriptor
import io.github.charlietap.chasm.compiler.backend.jvm.declarations.DECLARATIONS_BUILDER
import io.github.charlietap.chasm.compiler.backend.jvm.declarations.DECLARE
import io.github.charlietap.chasm.compiler.backend.jvm.declarations.DECLARE_DESCRIPTOR
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

internal typealias ModuleClassLowerer = (ModuleLowering, String, List<String>, String?, List<InstalledEntry>, JvmOutput) -> Unit

internal class InstalledEntry(
    val function: Int,
    val entry: String,
    val id: Int,
)

internal fun ModuleClassLowerer(
    module: ModuleLowering,
    moduleDescriptor: String,
    declarationClasses: List<String>,
    dataResourcePath: String?,
    entries: List<InstalledEntry>,
    output: JvmOutput,
) {
    val writer = supportClassWriter()
    val owner = internalName(moduleDescriptor)
    val binding = module.bindingType
    writer.visit(
        CLASS_FILE_VERSION,
        Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL or Opcodes.ACC_SUPER,
        owner,
        null,
        ModuleLowering.OBJECT,
        arrayOf(internalName(COMPILED_CODE)),
    )
    writer.visitField(Opcodes.ACC_PUBLIC or Opcodes.ACC_STATIC or Opcodes.ACC_FINAL, INSTANCE_FIELD, COMPILED_MODULE, null, null).visitEnd()

    writer.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null).apply {
        visitCode()
        visitTypeInsn(Opcodes.NEW, owner)
        visitInsn(Opcodes.DUP)
        visitMethodInsn(Opcodes.INVOKESPECIAL, owner, "<init>", "()V", false)
        visitMethodInsn(
            Opcodes.INVOKESTATIC,
            internalName(COMPILED_MODULE_FACTORY.owner),
            COMPILED_MODULE_FACTORY.name,
            COMPILED_MODULE_FACTORY.descriptor,
            false,
        )
        visitFieldInsn(Opcodes.PUTSTATIC, owner, INSTANCE_FIELD, COMPILED_MODULE)
        visitInsn(Opcodes.RETURN)
        visitMaxs(0, 0)
        visitEnd()
    }

    writer.visitMethod(Opcodes.ACC_PRIVATE, "<init>", "()V", null, null).apply {
        visitCode()
        visitVarInsn(Opcodes.ALOAD, 0)
        visitMethodInsn(Opcodes.INVOKESPECIAL, ModuleLowering.OBJECT, "<init>", "()V", false)
        visitInsn(Opcodes.RETURN)
        visitMaxs(0, 0)
        visitEnd()
    }

    writer.visitMethod(Opcodes.ACC_PUBLIC, BUILD_DECLARATIONS, "()$MODULE_DECLARATIONS", null, null).apply {
        visitCode()
        visitTypeInsn(Opcodes.NEW, DECLARATIONS_BUILDER)
        visitInsn(Opcodes.DUP)
        visitMethodInsn(Opcodes.INVOKESPECIAL, DECLARATIONS_BUILDER, "<init>", "()V", false)
        visitVarInsn(Opcodes.ASTORE, 1)
        declarationClasses.forEach { declaring ->
            visitVarInsn(Opcodes.ALOAD, 1)
            visitMethodInsn(Opcodes.INVOKESTATIC, declaring, DECLARE, DECLARE_DESCRIPTOR, false)
        }
        visitVarInsn(Opcodes.ALOAD, 1)
        if (dataResourcePath != null) {
            visitLdcInsn(Type.getObjectType(owner))
            visitLdcInsn(dataResourcePath)
            visitMethodInsn(Opcodes.INVOKESTATIC, internalName(DATA_SOURCE.owner), DATA_SOURCE.name, DATA_SOURCE.descriptor, false)
        } else {
            visitInsn(Opcodes.ACONST_NULL)
        }
        visitMethodInsn(Opcodes.INVOKEVIRTUAL, DECLARATIONS_BUILDER, "build", "($DATA_SOURCE_TYPE)$MODULE_DECLARATIONS", false)
        visitInsn(Opcodes.ARETURN)
        visitMaxs(0, 0)
        visitEnd()
    }

    // The path is written here because a shrinker may rename the classes it is derived from.
    writer.visitMethod(Opcodes.ACC_PUBLIC, TRAP_SITES_RESOURCE, "()Ljava/lang/String;", null, null).apply {
        visitCode()
        if (module.options.stacktraces) visitLdcInsn(trapSitesResource(module.packagePath)) else visitInsn(Opcodes.ACONST_NULL)
        visitInsn(Opcodes.ARETURN)
        visitMaxs(0, 0)
        visitEnd()
    }

    writeMetadata(writer, module.options)

    val chunks = entries.chunked(INSTALLS_PER_METHOD)
    val chunkClasses = chunks.indices.map { chunk -> installClassDescriptor(module.packagePath, chunk) }
    val chunkDescriptor = "($CODE_INSTALLATION$binding)V"
    var installers = chunkClasses.map(::internalName)
    var groups = 0
    while (installers.size > MAX_FANOUT) {
        installers = installers.chunked(MAX_FANOUT).map { members ->
            val group = "${module.packagePath}/InstallGroup${groups++}"
            output.classFile(group, installGroupClass(group, members, chunkDescriptor))
            group
        }
    }
    writer.visitMethod(Opcodes.ACC_PUBLIC, "install", "($CODE_INSTALLATION)V", null, null).apply {
        visitCode()
        visitTypeInsn(Opcodes.NEW, internalName(binding))
        visitInsn(Opcodes.DUP)
        visitVarInsn(Opcodes.ALOAD, 1)
        visitMethodInsn(Opcodes.INVOKESPECIAL, internalName(binding), "<init>", "($CODE_INSTALLATION)V", false)
        visitVarInsn(Opcodes.ASTORE, 2)
        installers.forEach { installer ->
            visitVarInsn(Opcodes.ALOAD, 1)
            visitVarInsn(Opcodes.ALOAD, 2)
            visitMethodInsn(Opcodes.INVOKESTATIC, installer, INSTALL_CHUNK, chunkDescriptor, false)
        }
        visitInsn(Opcodes.RETURN)
        visitMaxs(0, 0)
        visitEnd()
    }
    writer.visitEnd()
    output.classFile(owner, writer.toByteArray())

    chunks.forEachIndexed { chunk, chunkEntries ->
        val chunkOwner = internalName(chunkClasses[chunk])
        val chunkWriter = supportClassWriter()
        chunkWriter.visit(CLASS_FILE_VERSION, Opcodes.ACC_FINAL or Opcodes.ACC_SUPER, chunkOwner, null, ModuleLowering.OBJECT, null)
        chunkWriter.visitMethod(Opcodes.ACC_STATIC, INSTALL_CHUNK, chunkDescriptor, null, null).apply {
            visitCode()
            chunkEntries.forEach { installed ->
                visitVarInsn(Opcodes.ALOAD, 0)
                pushInt(installed.function)
                visitTypeInsn(Opcodes.NEW, internalName(installed.entry))
                visitInsn(Opcodes.DUP)
                visitVarInsn(Opcodes.ALOAD, 1)
                if (installed.id >= 0) pushInt(installed.id)
                visitMethodInsn(Opcodes.INVOKESPECIAL, internalName(installed.entry), "<init>", entryConstructor(binding, installed.id >= 0), false)
                visitMethodInsn(Opcodes.INVOKEVIRTUAL, internalName(INSTALL.owner), INSTALL.name, INSTALL.descriptor, false)
            }
            visitInsn(Opcodes.RETURN)
            visitMaxs(0, 0)
            visitEnd()
        }
        chunkWriter.visitEnd()
        output.classFile(chunkOwner, chunkWriter.toByteArray())
    }
}

private fun writeMetadata(
    writer: ClassWriter,
    options: JvmOptions,
) {
    val store = options.storeConfig
    val limits = store.limits
    writer.constant(RUNTIME_ABI, COMPILED_RUNTIME_ABI)
    writer.constant(META_METER_FUEL, store.meterFuel)
    writer.constant(META_INTERRUPTIBLE, store.interruptible)
    writer.constant(META_STACKTRACES, options.stacktraces)
    writer.constant(META_INITIAL_FUEL, options.initialFuel ?: UNSET_LONG)
    writer.constant(META_GC_STRATEGY, gcStrategyCode(store.gcStrategy))
    writer.constant(META_GC_THRESHOLD_KIND, gcThresholdKind(store.gcThreshold))
    writer.constant(META_GC_THRESHOLD_VALUE, gcThresholdValue(store.gcThreshold))
    writer.constant(META_GC_HEAP_BYTES, limits.gcHeapBytes ?: UNSET_LONG)
    writer.constant(META_MEMORY_PAGES, limits.memoryPages ?: UNSET)
    writer.constant(META_TABLE_ELEMENTS, limits.tableElements ?: UNSET)
    writer.constant(META_TRAP_ON_GROW_FAILURE, limits.trapOnGrowFailure)
    writer.constant(META_STACK_BYTES, limits.stackBytes ?: UNSET_LONG)
}

private fun ClassWriter.constant(
    name: String,
    value: Int,
) = constantMethod(name, "()I", Opcodes.IRETURN) { visitLdcInsn(value) }

private fun ClassWriter.constant(
    name: String,
    value: Long,
) = constantMethod(name, "()J", Opcodes.LRETURN) { visitLdcInsn(value) }

private fun ClassWriter.constant(
    name: String,
    value: Boolean,
) = constantMethod(name, "()Z", Opcodes.IRETURN) { visitInsn(if (value) Opcodes.ICONST_1 else Opcodes.ICONST_0) }

private inline fun ClassWriter.constantMethod(
    name: String,
    descriptor: String,
    returnOpcode: Int,
    push: MethodVisitor.() -> Unit,
) {
    visitMethod(Opcodes.ACC_PUBLIC, name, descriptor, null, null).apply {
        visitCode()
        push()
        visitInsn(returnOpcode)
        visitMaxs(0, 0)
        visitEnd()
    }
}

private fun installGroupClass(
    owner: String,
    members: List<String>,
    descriptor: String,
): ByteArray {
    val writer = supportClassWriter()
    writer.visit(CLASS_FILE_VERSION, Opcodes.ACC_FINAL or Opcodes.ACC_SUPER, owner, null, ModuleLowering.OBJECT, null)
    writer.visitMethod(Opcodes.ACC_STATIC, INSTALL_CHUNK, descriptor, null, null).apply {
        visitCode()
        members.forEach { member ->
            visitVarInsn(Opcodes.ALOAD, 0)
            visitVarInsn(Opcodes.ALOAD, 1)
            visitMethodInsn(Opcodes.INVOKESTATIC, member, INSTALL_CHUNK, descriptor, false)
        }
        visitInsn(Opcodes.RETURN)
        visitMaxs(0, 0)
        visitEnd()
    }
    writer.visitEnd()
    return writer.toByteArray()
}

private const val INSTALL_CHUNK = "install"

private const val INSTANCE_FIELD = "INSTANCE"
