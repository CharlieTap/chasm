package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.compiler.backend.abi.CODE_INSTALLATION
import io.github.charlietap.chasm.compiler.backend.abi.INSTALL
import io.github.charlietap.chasm.compiler.backend.abi.INSTALLATION_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.INSTALLATION_TYPE_TEST
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.UNSAFE_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.heapTypeNumber
import io.github.charlietap.chasm.compiler.backend.abi.installationAccessor
import io.github.charlietap.chasm.compiler.backend.abi.installationIndex
import io.github.charlietap.chasm.compiler.backend.dex.abi.BINDING_TYPE
import io.github.charlietap.chasm.compiler.backend.dex.abi.method
import io.github.charlietap.chasm.compiler.backend.dex.binding.BindingLayout
import io.github.charlietap.chasm.compiler.backend.dex.binding.BoundResources
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.ClassAccess
import io.github.charlietap.dexter.ClassBuilder
import io.github.charlietap.dexter.ClassType
import io.github.charlietap.dexter.CodeEmitter
import io.github.charlietap.dexter.Conversion
import io.github.charlietap.dexter.DexConstructor
import io.github.charlietap.dexter.DexFile
import io.github.charlietap.dexter.DexType
import io.github.charlietap.dexter.Dexter
import io.github.charlietap.dexter.MemberAccess
import io.github.charlietap.dexter.Reg

internal typealias InstallLowerer = (Dexter, String, BoundResources, Map<Int, EntryRef>) -> List<ByteArray>

internal fun InstallLowerer(
    dexter: Dexter,
    installer: String,
    bound: BoundResources,
    entries: Map<Int, EntryRef>,
): List<ByteArray> {
    val groups = entries.entries.chunked(INSTALLS_PER_FILE)
    val groupClasses = groups.indices.map { group -> installer.removeSuffix(";") + "Entries$group;" }
    val files = groups.mapIndexed { group, installs ->
        dexFile(dexter) { file -> entriesClass(file, groupClasses[group], installs.chunked(INSTALLS_PER_METHOD)) }
    }
    return files + dexFile(dexter) { file -> installerClass(file, installer, bound, groupClasses) }
}

private inline fun dexFile(
    dexter: Dexter,
    write: (DexFile) -> Unit,
): ByteArray {
    val file = dexter.beginFile()
    return try {
        write(file)
        file.finish()
    } catch (failure: Throwable) {
        file.abort()
        throw failure
    }
}

private fun entriesClass(
    file: DexFile,
    owner: String,
    chunks: List<List<Map.Entry<Int, EntryRef>>>,
) {
    val builder = file.addClass(ClassType(owner), DexType.Object, access = ClassAccess.PackageFinal)
    val install = INSTALL.method
    builder.chunk(owner, INSTALL_ENTRIES, 0, MemberAccess.Package) { installation, binding ->
        chunks.indices.forEach { chunk -> invokeStatic(chunkMethod(owner, INSTALL_ENTRIES, chunk + 1), installation, binding) }
    }
    chunks.forEachIndexed { chunk, group ->
        builder.chunk(owner, INSTALL_ENTRIES, chunk + 1) { installation, binding ->
            group.forEach { (function, entry) ->
                val type = ClassType(entry.descriptor)
                newInstance(OBJECT, type)
                const(NUMBER, entry.id)
                invokeConstructor(DexConstructor(type, DexType.Object.array(), DexType.Int), OBJECT, binding, NUMBER)
                const(INDEX, function)
                invokeVirtual(install, installation, INDEX, OBJECT)
            }
        }
    }
}

private fun installerClass(
    file: DexFile,
    installer: String,
    bound: BoundResources,
    groupClasses: List<String>,
) {
    val owner = ClassType(installer)
    val builder = file.addClass(owner, DexType.Object, CONSUMER)
    val objects = bound.objects.chunked(RESOURCES_PER_METHOD)
    val values = bound.values.chunked(RESOURCES_PER_METHOD)

    builder.beginConstructor(DexConstructor(owner), registerCount = 1).apply {
        invokeConstructor(OBJECT_CONSTRUCTOR, self)
        returnVoid()
        finish()
    }

    val accept = MethodRef(installer, "accept", listOf(OBJECT_TYPE), "V").method
    builder.beginInstanceMethod(accept, registerCount = 5).apply {
        val installation = parameter(0)
        checkCast(installation, ClassType(CODE_INSTALLATION))
        const(SIZE, bound.objectCount)
        newArray(BINDING, SIZE, DexType.Object.array())
        const(SIZE, bound.valueCount)
        newArray(VALUES, SIZE, DexType.Long.array())
        const(SIZE, BindingLayout.VALUES_SLOT)
        arrayPut(ArrayKind.Object, VALUES, BINDING, SIZE)
        objects.indices.forEach { chunk -> invokeStatic(chunkMethod(installer, "bindObjects", chunk), installation, BINDING) }
        values.indices.forEach { chunk -> invokeStatic(chunkMethod(installer, "bindValues", chunk), installation, BINDING) }
        groupClasses.forEach { group -> invokeStatic(chunkMethod(group, INSTALL_ENTRIES, 0), installation, BINDING) }
        returnVoid()
        finish()
    }

    objects.forEachIndexed { chunk, bindings ->
        builder.chunk(installer, "bindObjects", chunk) { installation, binding ->
            bindings.forEach { bound ->
                loadObject(bound.resource, installation)
                const(INDEX, bound.slot)
                arrayPut(ArrayKind.Object, OBJECT, binding, INDEX)
            }
        }
    }

    values.forEachIndexed { chunk, bindings ->
        builder.chunk(installer, "bindValues", chunk) { installation, binding ->
            const(INDEX, BindingLayout.VALUES_SLOT)
            arrayGet(ArrayKind.Object, VALUES_ARRAY, binding, INDEX)
            checkCast(VALUES_ARRAY, DexType.Long.array())
            bindings.forEach { bound ->
                loadValue(bound.resource, installation)
                const(INDEX, bound.slot)
                arrayPut(ArrayKind.Wide, WIDE, VALUES_ARRAY, INDEX)
            }
        }
    }
}

private inline fun ClassBuilder.chunk(
    installer: String,
    name: String,
    chunk: Int,
    access: MemberAccess = MemberAccess.Private,
    body: CodeEmitter.(Reg, Reg) -> Unit,
) {
    beginStaticMethod(chunkMethod(installer, name, chunk), registerCount = SCRATCH + 2, access = access).apply {
        body(parameter(0), parameter(1))
        returnVoid()
        finish()
    }
}

private fun chunkMethod(
    installer: String,
    name: String,
    chunk: Int,
) = MethodRef(installer, "$name$chunk", listOf(CODE_INSTALLATION, BINDING_TYPE), "V").method

private fun CodeEmitter.loadObject(
    resource: Resource,
    installation: Reg,
) {
    when (resource) {
        Resource.Instance -> invokeVirtual(INSTALLATION_INSTANCE.method, installation)
        Resource.Unsafe -> invokeStatic(UNSAFE_INSTANCE.method)
        is Resource.TypeTest -> {
            const(INDEX, if (resource.type.nullable) 1 else 0)
            const(NUMBER, heapTypeNumber(resource.type.heapType))
            invokeVirtual(INSTALLATION_TYPE_TEST.method, installation, INDEX, NUMBER)
        }
        else -> {
            val accessor = checkNotNull(installationAccessor(resource)) { "$resource has no binding object" }
            const(INDEX, installationIndex(resource))
            invokeVirtual(accessor.method, installation, INDEX)
        }
    }
    moveResult(OBJECT)
}

private fun CodeEmitter.loadValue(
    resource: Resource,
    installation: Reg,
) {
    val accessor = checkNotNull(installationAccessor(resource)) { "$resource has no binding value" }
    const(INDEX, installationIndex(resource))
    invokeVirtual(accessor.method, installation, INDEX)
    if (accessor.returns == "J") {
        moveResult(WIDE)
    } else {
        moveResult(NUMBER)
        convert(Conversion.IntToLong, WIDE, NUMBER)
    }
}

private val CONSUMER = ClassType("Ljava/util/function/Consumer;")

private val OBJECT_CONSTRUCTOR = DexConstructor(DexType.Object)

private val SIZE = Reg(0)
private val BINDING = Reg(1)
private val VALUES = Reg(2)

private val WIDE = Reg(0)
private val INDEX = Reg(2)
private val NUMBER = Reg(3)
private val OBJECT = Reg(4)
private val VALUES_ARRAY = Reg(5)
private const val SCRATCH = 6

private const val RESOURCES_PER_METHOD = 200

private const val INSTALLS_PER_METHOD = 400

private const val INSTALLS_PER_FILE = 8_000

private const val INSTALL_ENTRIES = "installEntries"
