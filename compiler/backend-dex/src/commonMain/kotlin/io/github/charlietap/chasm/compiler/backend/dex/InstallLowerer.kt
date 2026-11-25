package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.compiler.backend.abi.CODE_INSTALLATION
import io.github.charlietap.chasm.compiler.backend.abi.INSTALL
import io.github.charlietap.chasm.compiler.backend.abi.INSTALLATION_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.UNSAFE_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.installationAccessor
import io.github.charlietap.chasm.compiler.backend.abi.installationIndex
import io.github.charlietap.chasm.compiler.backend.dex.abi.BINDING_TYPE
import io.github.charlietap.chasm.compiler.backend.dex.abi.installClass
import io.github.charlietap.chasm.compiler.backend.dex.abi.installEntriesClass
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
import io.github.charlietap.dexter.DexMethod
import io.github.charlietap.dexter.DexType
import io.github.charlietap.dexter.MemberAccess
import io.github.charlietap.dexter.Reg

internal typealias InstallLowerer = (MetadataFiles, String, BoundResources, Map<Int, EntryRef>) -> List<String>

internal fun InstallLowerer(
    files: MetadataFiles,
    entryPath: String,
    bound: BoundResources,
    entries: Map<Int, EntryRef>,
): List<String> {

    val groups = entries.entries.chunked(INSTALLS_PER_CLASS)
    val groupClasses = groups.indices.map { group -> installEntriesClass(entryPath, group) }
    groups.forEachIndexed { group, installs ->
        files.write { file -> entriesClass(file, groupClasses[group], installs.chunked(INSTALLS_PER_METHOD)) }
    }
    files.write { file -> installerClass(file, installClass(entryPath), bound) }

    return groupClasses
}

internal fun bindObjectsMethods(
    installer: String,
    bound: BoundResources,
): List<DexMethod> {
    return bound.objects.chunked(RESOURCES_PER_METHOD).indices.map { chunk -> chunkMethod(installer, BIND_OBJECTS, chunk) }
}

internal fun bindValuesMethods(
    installer: String,
    bound: BoundResources,
): List<DexMethod> {
    return bound.values.chunked(RESOURCES_PER_METHOD).indices.map { chunk -> chunkMethod(installer, BIND_VALUES, chunk) }
}

internal fun installEntriesMethod(group: String): DexMethod {
    return chunkMethod(group, INSTALL_ENTRIES, 0)
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
) {

    val builder = file.addClass(ClassType(installer), DexType.Object, access = ClassAccess.PackageFinal)

    bound.objects.chunked(RESOURCES_PER_METHOD).forEachIndexed { chunk, bindings ->
        builder.chunk(installer, BIND_OBJECTS, chunk, MemberAccess.Package) { installation, binding ->
            bindings.forEach { bound ->
                loadObject(bound.resource, installation)
                const(INDEX, bound.slot)
                arrayPut(ArrayKind.Object, OBJECT, binding, INDEX)
            }
        }
    }

    bound.values.chunked(RESOURCES_PER_METHOD).forEachIndexed { chunk, bindings ->
        builder.chunk(installer, BIND_VALUES, chunk, MemberAccess.Package) { installation, binding ->
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
    owner: String,
    name: String,
    chunk: Int,
    access: MemberAccess = MemberAccess.Private,
    body: CodeEmitter.(Reg, Reg) -> Unit,
) {
    beginStaticMethod(chunkMethod(owner, name, chunk), registerCount = SCRATCH + 2, access = access).apply {
        body(parameter(0), parameter(1))
        returnVoid()
        finish()
    }
}

private fun chunkMethod(
    owner: String,
    name: String,
    chunk: Int,
): DexMethod {
    return MethodRef(owner, "$name$chunk", listOf(CODE_INSTALLATION, BINDING_TYPE), "V").method
}

private fun CodeEmitter.loadObject(
    resource: Resource,
    installation: Reg,
) {
    when (resource) {
        Resource.Instance -> invokeVirtual(INSTALLATION_INSTANCE.method, installation)
        Resource.Unsafe -> invokeStatic(UNSAFE_INSTANCE.method)
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

private val WIDE = Reg(0)
private val INDEX = Reg(2)
private val NUMBER = Reg(3)
private val OBJECT = Reg(4)
private val VALUES_ARRAY = Reg(5)
private const val SCRATCH = 6

private const val RESOURCES_PER_METHOD = 200

private const val INSTALLS_PER_METHOD = 400

private const val INSTALLS_PER_CLASS = 8_000

private const val INSTALL_ENTRIES = "installEntries"

private const val BIND_OBJECTS = "bindObjects"

private const val BIND_VALUES = "bindValues"
