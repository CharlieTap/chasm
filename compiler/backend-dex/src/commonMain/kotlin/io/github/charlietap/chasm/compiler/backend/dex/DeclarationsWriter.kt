package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.compiler.backend.abi.GeneratedClassNames
import io.github.charlietap.chasm.compiler.backend.analysis.declarations.BuilderCall
import io.github.charlietap.dexter.ClassAccess
import io.github.charlietap.dexter.ClassType
import io.github.charlietap.dexter.CodeEmitter
import io.github.charlietap.dexter.DexFile
import io.github.charlietap.dexter.DexMethod
import io.github.charlietap.dexter.DexType
import io.github.charlietap.dexter.MemberAccess
import io.github.charlietap.dexter.Reg
import io.github.charlietap.dexter.ValueType

internal class DeclarationsWriter(
    private val names: GeneratedClassNames,
) {

    fun write(
        files: MetadataFiles,
        calls: List<BuilderCall>,
    ): List<String> {
        return batches(calls).mapIndexed { index, batch ->
            val owner = names.declare(index)
            files.write { file -> writeClass(file, owner, batch) }
            owner
        }
    }

    private fun writeClass(
        file: DexFile,
        owner: String,
        batch: List<BuilderCall>,
    ) {

        val builder = file.addClass(ClassType(owner), DexType.Object, access = ClassAccess.PackageFinal)
        val code = builder.beginStaticMethod(declareMethod(owner), registerCount = REGISTERS, access = MemberAccess.Package)

        code.moveObject(BUILDER, code.parameter(0))
        batch.forEach { call -> code.call(call) }
        code.returnVoid()
        code.finish()
    }

    private fun batches(calls: List<BuilderCall>): List<List<BuilderCall>> {

        val batches = ArrayList<List<BuilderCall>>()
        var batch = ArrayList<BuilderCall>()
        var units = 0

        for (call in calls) {
            val cost = ARGUMENT_CODE_UNITS * call.arguments.size + CALL_CODE_UNITS
            if (batch.isNotEmpty() && units + cost > MAX_DECLARE_CODE_UNITS) {
                batches += batch
                batch = ArrayList()
                units = 0
            }
            batch += call
            units += cost
        }

        if (batch.isNotEmpty()) {
            batches += batch
        }

        return batches
    }

    private fun CodeEmitter.call(call: BuilderCall) {

        val parameters = ArrayList<ValueType>(call.arguments.size)
        var register = BUILDER.index + 1

        call.arguments.forEach { argument ->
            when (argument) {
                is Int -> {
                    const(Reg(register), argument)
                    parameters += DexType.Int
                    register += 1
                }
                is Long -> {
                    constWide(Reg(register), argument)
                    parameters += DexType.Long
                    register += 2
                }
                is Boolean -> {
                    const(Reg(register), if (argument) 1 else 0)
                    parameters += DexType.Boolean
                    register += 1
                }
                is String -> {
                    constString(Reg(register), argument)
                    parameters += DexType.String
                    register += 1
                }
                else -> throw IllegalArgumentException("a builder call cannot take $argument")
            }
        }

        invokeVirtualRange(DexMethod(DECLARATIONS_BUILDER, call.methodName, DexType.Void, parameters), BUILDER)
    }

    private companion object {
        const val MAX_DECLARE_CODE_UNITS = 6_000
        const val ARGUMENT_CODE_UNITS = 5
        const val CALL_CODE_UNITS = 3
        const val REGISTERS = 12
        val BUILDER = Reg(0)
    }
}

internal val DECLARATIONS_BUILDER = ClassType("Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;")

internal fun declareMethod(owner: String): DexMethod {
    return DexMethod(ClassType(owner), DECLARE, DexType.Void, DECLARATIONS_BUILDER)
}

private const val DECLARE = "declare"
