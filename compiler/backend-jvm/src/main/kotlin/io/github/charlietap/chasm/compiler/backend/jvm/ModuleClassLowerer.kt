package io.github.charlietap.chasm.compiler.backend.jvm

import io.github.charlietap.chasm.compiler.backend.abi.Fingerprint
import io.github.charlietap.chasm.compiler.backend.abi.internalName
import io.github.charlietap.chasm.compiler.backend.jvm.abi.CLASS_FILE_VERSION
import io.github.charlietap.chasm.compiler.backend.jvm.abi.CODE_IDENTITY
import io.github.charlietap.chasm.compiler.backend.jvm.abi.CODE_INSTALLATION
import io.github.charlietap.chasm.compiler.backend.jvm.abi.COMPILED_MODULE
import io.github.charlietap.chasm.compiler.backend.jvm.abi.INSTALL
import io.github.charlietap.chasm.compiler.backend.jvm.abi.INSTALLS_PER_METHOD
import io.github.charlietap.chasm.compiler.backend.jvm.abi.MAX_FANOUT
import io.github.charlietap.chasm.compiler.backend.jvm.abi.installClassDescriptor
import org.objectweb.asm.Opcodes

internal typealias ModuleClassLowerer = (ModuleLowering, String, List<InstalledEntry>, JvmOutput) -> Unit

internal class InstalledEntry(
    val function: Int,
    val entry: String,
    val id: Int,
)

internal fun ModuleClassLowerer(
    module: ModuleLowering,
    moduleDescriptor: String,
    entries: List<InstalledEntry>,
    output: JvmOutput,
) {
    val writer = supportClassWriter()
    val owner = internalName(moduleDescriptor)
    val superclass = internalName(COMPILED_MODULE)
    val binding = module.bindingType
    writer.visit(CLASS_FILE_VERSION, Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL or Opcodes.ACC_SUPER, owner, null, superclass, null)
    writer.visitField(Opcodes.ACC_PUBLIC or Opcodes.ACC_STATIC or Opcodes.ACC_FINAL, INSTANCE_FIELD, moduleDescriptor, null, null).visitEnd()

    writer.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null).apply {
        visitCode()
        visitTypeInsn(Opcodes.NEW, owner)
        visitInsn(Opcodes.DUP)
        visitMethodInsn(Opcodes.INVOKESPECIAL, owner, "<init>", "()V", false)
        visitFieldInsn(Opcodes.PUTSTATIC, owner, INSTANCE_FIELD, moduleDescriptor)
        visitInsn(Opcodes.RETURN)
        visitMaxs(0, 0)
        visitEnd()
    }

    writer.visitMethod(Opcodes.ACC_PRIVATE, "<init>", "()V", null, null).apply {
        visitCode()
        visitVarInsn(Opcodes.ALOAD, 0)
        visitMethodInsn(Opcodes.INVOKESPECIAL, superclass, "<init>", "()V", false)
        visitInsn(Opcodes.RETURN)
        visitMaxs(0, 0)
        visitEnd()
    }

    writer.visitMethod(Opcodes.ACC_PUBLIC, "getIdentity", "()$CODE_IDENTITY", null, null).apply {
        val options = module.options
        visitCode()
        visitTypeInsn(Opcodes.NEW, internalName(CODE_IDENTITY))
        visitInsn(Opcodes.DUP)
        pushInt(Fingerprint.RUNTIME_ABI)
        visitInsn(if (options.meterFuel) Opcodes.ICONST_1 else Opcodes.ICONST_0)
        visitInsn(if (options.interruptible) Opcodes.ICONST_1 else Opcodes.ICONST_0)
        visitMethodInsn(Opcodes.INVOKESPECIAL, internalName(CODE_IDENTITY), "<init>", "(IZZ)V", false)
        visitInsn(Opcodes.ARETURN)
        visitMaxs(0, 0)
        visitEnd()
    }

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
