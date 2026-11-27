package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.compiled.format.ArtifactTarget
import io.github.charlietap.chasm.compiler.backend.abi.CODE_INSTALLATION
import io.github.charlietap.chasm.compiler.backend.abi.COMPILED_MODULE
import io.github.charlietap.chasm.compiler.backend.abi.INSTALLATION_MEMORY
import io.github.charlietap.chasm.compiler.backend.abi.ModuleSetting
import io.github.charlietap.chasm.compiler.backend.abi.ModuleSettings
import io.github.charlietap.chasm.compiler.backend.abi.REQUIRE_BYTE_ARRAY
import io.github.charlietap.chasm.compiler.backend.abi.RTT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.UNREACHABLE
import io.github.charlietap.chasm.compiler.backend.dex.abi.installClass
import io.github.charlietap.chasm.compiler.backend.dex.abi.method
import io.github.charlietap.chasm.compiler.backend.dex.binding.BindingLayout
import io.github.charlietap.chasm.compiler.backend.dex.binding.BoundResources
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.ClassBuilder
import io.github.charlietap.dexter.ClassType
import io.github.charlietap.dexter.CodeEmitter
import io.github.charlietap.dexter.ConstructorAccess
import io.github.charlietap.dexter.DexConstructor
import io.github.charlietap.dexter.DexField
import io.github.charlietap.dexter.DexFile
import io.github.charlietap.dexter.DexMethod
import io.github.charlietap.dexter.DexType
import io.github.charlietap.dexter.LiteralOp
import io.github.charlietap.dexter.MemberAccess
import io.github.charlietap.dexter.Reg

internal class ModuleClassWriter(
    private val plan: ModulePlan,
    private val byteArrayBaseOffset: Long,
) {

    private val owner = ClassType(plan.names.module())

    fun write(
        files: MetadataFiles,
        declareClasses: List<String>,
        structClasses: List<String>,
        classes: List<String>,
        bound: BoundResources,
        installEntries: List<String>,
        constants: String?,
    ) {
        files.write { file -> writeClass(file, declareClasses, structClasses, classes, bound, installEntries, constants) }
    }

    private fun writeClass(
        file: DexFile,
        declareClasses: List<String>,
        structClasses: List<String>,
        classes: List<String>,
        bound: BoundResources,
        installEntries: List<String>,
        constants: String?,
    ) {

        val builder = file.addClass(owner, DexType.Object, ClassType(COMPILED_MODULE))

        instance(builder)
        settings(builder)
        resourceRoot(builder)
        classes(builder, classes)
        initializesEagerly(builder)
        buildDeclarations(builder, declareClasses)
        structLanes(builder)
        newStruct(builder, structClasses)
        install(builder, bound, installEntries, constants)
    }

    private fun instance(builder: ClassBuilder) {

        val compiled = DexField(owner, COMPILED_FIELD, owner)
        val constructor = DexConstructor(owner)

        builder.addStaticField(compiled, MemberAccess.Public.final())

        builder.beginStaticInitializer(registerCount = 1).apply {
            newInstance(FIRST, owner)
            invokeConstructor(constructor, FIRST)
            putStatic(FIRST, compiled)
            returnVoid()
            finish()
        }

        builder.beginConstructor(constructor, registerCount = 1, access = ConstructorAccess.Private).apply {
            invokeConstructor(DexConstructor(DexType.Object), self)
            returnVoid()
            finish()
        }
    }

    private fun settings(builder: ClassBuilder) {

        val settings = ModuleSettings(plan.options.runtime, ArtifactTarget.ART, byteArrayBaseOffset)

        settings.constants().forEach { setting ->
            when (setting) {
                is ModuleSetting.IntSetting -> constant(builder, setting.name, DexType.Int) {
                    const(FIRST, setting.value)
                }
                is ModuleSetting.LongSetting -> constant(builder, setting.name, DexType.Long) {
                    constWide(FIRST, setting.value)
                }
                is ModuleSetting.BooleanSetting -> constant(builder, setting.name, DexType.Boolean) {
                    const(FIRST, if (setting.value) 1 else 0)
                }
            }
        }
    }

    private fun resourceRoot(builder: ClassBuilder) {
        constant(builder, RESOURCE_ROOT, DexType.String) {
            constString(FIRST, plan.entryPath)
        }
    }

    private fun classes(
        builder: ClassBuilder,
        classes: List<String>,
    ) {

        val parts = classes.chunked(CLASSES_PER_METHOD)
        val partMethods = parts.indices.map { part -> DexMethod(owner, "$CLASSES$part", DexType.Void, STRINGS) }

        parts.forEachIndexed { part, names ->
            builder.beginStaticMethod(partMethods[part], registerCount = 3, access = MemberAccess.Private).apply {
                val array = parameter(0)
                names.forEachIndexed { index, name ->
                    const(FIRST, part * CLASSES_PER_METHOD + index)
                    constString(SECOND, name)
                    arrayPut(ArrayKind.Object, SECOND, array, FIRST)
                }
                returnVoid()
                finish()
            }
        }

        builder.beginInstanceMethod(DexMethod(owner, CLASSES, STRINGS), registerCount = 3).apply {
            const(FIRST, classes.size)
            newArray(SECOND, FIRST, STRINGS)
            partMethods.forEach { part -> invokeStatic(part, SECOND) }
            returnValue(SECOND)
            finish()
        }
    }

    private fun initializesEagerly(builder: ClassBuilder) {
        constant(builder, INITIALIZES_EAGERLY, DexType.Boolean) {
            const(FIRST, 1)
        }
    }

    private fun buildDeclarations(
        builder: ClassBuilder,
        declareClasses: List<String>,
    ) {

        val parts = declareClasses.chunked(DECLARES_PER_METHOD)
        val calls = if (parts.size <= 1) {
            declareClasses.map(::declareMethod)
        } else {
            parts.mapIndexed { part, members ->
                val method = DexMethod(owner, "$BUILD_DECLARATIONS$part", DexType.Void, DECLARATIONS_BUILDER)
                builder.beginStaticMethod(method, registerCount = 1, access = MemberAccess.Private).apply {
                    members.forEach { member -> invokeStatic(declareMethod(member), parameter(0)) }
                    returnVoid()
                    finish()
                }
                method
            }
        }

        builder.beginInstanceMethod(DexMethod(owner, BUILD_DECLARATIONS, MODULE_DECLARATIONS, DATA_SOURCE), registerCount = 3).apply {
            newInstance(FIRST, DECLARATIONS_BUILDER)
            invokeConstructor(DexConstructor(DECLARATIONS_BUILDER), FIRST)
            calls.forEach { call -> invokeStatic(call, FIRST) }
            invokeVirtual(DexMethod(DECLARATIONS_BUILDER, BUILD, MODULE_DECLARATIONS, DATA_SOURCE), FIRST, parameter(0))
            moveResult(FIRST)
            returnValue(FIRST)
            finish()
        }
    }

    private fun structLanes(builder: ClassBuilder) {

        val lanes = plan.structLanes.encode()

        builder.beginInstanceMethod(DexMethod(owner, STRUCT_LANES, LONGS), registerCount = 3).apply {
            const(FIRST, lanes.size)
            newArray(SECOND, FIRST, LONGS)
            if (lanes.isNotEmpty()) {
                fillArrayData(SECOND, lanes)
            }
            returnValue(SECOND)
            finish()
        }
    }

    private fun newStruct(
        builder: ClassBuilder,
        structClasses: List<String>,
    ) {

        val groups = plan.structLanes.structs.keys.map { type -> type / STRUCTS_PER_GROUP }.distinct()

        builder.beginInstanceMethod(DexMethod(owner, NEW_STRUCT, DexType.Object, DexType.Int, DexType.Object), registerCount = 5).apply {

            val type = parameter(0)
            val rtt = parameter(1)
            val cases = groups.map { group -> group to label() }
            val missing = label()

            if (cases.isNotEmpty()) {
                val first = groups.first()
                val table = labelTable(first, groups.last() - first + 1, missing)
                cases.forEach { (group, label) -> table[group - first] = label }
                binary(LiteralOp.DivInt, FIRST, type, STRUCTS_PER_GROUP)
                packedSwitch(FIRST, table)
            }

            bind(missing)
            invokeStatic(UNREACHABLE.method)
            const(FIRST, 0)
            returnValue(FIRST)

            cases.forEachIndexed { index, (_, label) ->
                bind(label)
                moveObject(SECOND, rtt)
                checkCast(SECOND, RTT)
                invokeStatic(newStructMethod(structClasses[index]), type, SECOND)
                moveResult(FIRST)
                returnValue(FIRST)
            }

            finish()
        }
    }

    private fun install(
        builder: ClassBuilder,
        bound: BoundResources,
        installEntries: List<String>,
        constants: String?,
    ) {

        val installer = installClass(plan.entryPath)
        val method = DexMethod(owner, INSTALL, DexType.Void, INSTALLATION)

        builder.beginInstanceMethod(method, registerCount = 5).apply {

            val installation = parameter(0)

            if (plan.inlineMemory != null) {
                const(FIRST, 0)
                invokeVirtual(INSTALLATION_MEMORY.method, installation, FIRST)
                moveResult(SECOND)
                invokeStatic(REQUIRE_BYTE_ARRAY.method, SECOND)
            }

            const(FIRST, bound.objectCount)
            newArray(SECOND, FIRST, BINDING)
            const(FIRST, bound.valueCount)
            newArray(THIRD, FIRST, LONGS)
            const(FIRST, BindingLayout.VALUES_SLOT)
            arrayPut(ArrayKind.Object, THIRD, SECOND, FIRST)

            bindObjectsMethods(installer, bound).forEach { chunk -> invokeStatic(chunk, installation, SECOND) }
            bindValuesMethods(installer, bound).forEach { chunk -> invokeStatic(chunk, installation, SECOND) }
            installEntries.forEach { group -> invokeStatic(installEntriesMethod(group), installation, SECOND) }
            if (constants != null) {
                invokeStatic(DexMethod(ClassType(constants), INITIALIZE, DexType.Void, INSTALLATION, BINDING), installation, SECOND)
            }

            returnVoid()
            finish()
        }
    }

    private inline fun constant(
        builder: ClassBuilder,
        name: String,
        type: DexType,
        load: CodeEmitter.() -> Unit,
    ) {
        builder.beginInstanceMethod(DexMethod(owner, name, type), registerCount = 3).apply {
            load()
            returnValue(FIRST)
            finish()
        }
    }

    private companion object {
        const val COMPILED_FIELD = "COMPILED"
        const val RESOURCE_ROOT = "resourceRoot"
        const val CLASSES = "classes"
        const val INITIALIZES_EAGERLY = "initializesEagerly"
        const val BUILD_DECLARATIONS = "buildDeclarations"
        const val BUILD = "build"
        const val STRUCT_LANES = "structLanes"
        const val INSTALL = "install"
        const val INITIALIZE = "initialize"
        const val CLASSES_PER_METHOD = 256
        const val DECLARES_PER_METHOD = 256

        val FIRST = Reg(0)
        val SECOND = Reg(1)
        val THIRD = Reg(2)

        val INSTALLATION = ClassType(CODE_INSTALLATION)
        val RTT = ClassType(RTT_TYPE)
        val BINDING = DexType.Object.array()
        val STRINGS = DexType.String.array()
        val LONGS = DexType.Long.array()
        val MODULE_DECLARATIONS = ClassType("Lio/github/charlietap/chasm/declarations/ModuleDeclarations;")
        val DATA_SOURCE = ClassType("Lio/github/charlietap/chasm/declarations/DataSource;")
    }
}
