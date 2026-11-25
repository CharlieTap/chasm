package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.compiler.backend.abi.COMPILED_STRUCT
import io.github.charlietap.chasm.compiler.backend.abi.GeneratedClassNames
import io.github.charlietap.chasm.compiler.backend.abi.RTT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.UNREACHABLE
import io.github.charlietap.chasm.compiler.backend.analysis.gc.StructLanes
import io.github.charlietap.chasm.compiler.backend.dex.abi.method
import io.github.charlietap.chasm.compiler.backend.dex.gc.StructRegisters
import io.github.charlietap.chasm.compiler.backend.dex.gc.allocateStruct
import io.github.charlietap.dexter.ClassAccess
import io.github.charlietap.dexter.ClassType
import io.github.charlietap.dexter.DexFile
import io.github.charlietap.dexter.DexMethod
import io.github.charlietap.dexter.DexType
import io.github.charlietap.dexter.MemberAccess
import io.github.charlietap.dexter.Reg

internal class StructsWriter(
    private val names: GeneratedClassNames,
    private val lanes: StructLanes,
) {

    fun write(files: MetadataFiles): List<String> {

        val groups = lanes.structs.keys.groupBy { type -> type / STRUCTS_PER_GROUP }

        return groups.map { (group, types) ->
            val owner = names.structs(group)
            files.write { file -> writeClass(file, owner, types) }
            owner
        }
    }

    private fun writeClass(
        file: DexFile,
        owner: String,
        types: List<Int>,
    ) {

        val builder = file.addClass(ClassType(owner), DexType.Object, access = ClassAccess.PackageFinal)
        val code = builder.beginStaticMethod(newStructMethod(owner), registerCount = REGISTERS, access = MemberAccess.Package)
        val type = code.parameter(0)
        val rtt = code.parameter(1)

        val first = types.first()
        val missing = code.label()
        val table = code.labelTable(first, types.last() - first + 1, missing)
        val cases = types.map { case -> case to code.label() }
        cases.forEach { (case, label) -> table[case - first] = label }
        code.packedSwitch(type, table)

        code.bind(missing)
        code.invokeStatic(UNREACHABLE.method)
        code.const(STRUCT, 0)
        code.returnValue(STRUCT)

        cases.forEach { (case, label) ->
            code.bind(label)
            code.allocateStruct(lanes.structs.getValue(case), rtt, REGISTERS_USED)
            code.returnValue(STRUCT)
        }

        code.finish()
    }

    private companion object {
        const val REGISTERS = 5
        val STRUCT = Reg(0)
        val REGISTERS_USED = StructRegisters(struct = STRUCT, array = Reg(1), index = Reg(2))
    }
}

internal fun newStructMethod(owner: String): DexMethod {
    return DexMethod(ClassType(owner), NEW_STRUCT, ClassType(COMPILED_STRUCT), DexType.Int, ClassType(RTT_TYPE))
}

internal const val NEW_STRUCT = "newStruct"

internal const val STRUCTS_PER_GROUP = 128
