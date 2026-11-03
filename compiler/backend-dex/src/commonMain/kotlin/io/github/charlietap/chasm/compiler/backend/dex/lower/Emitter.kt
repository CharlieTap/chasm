package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.descriptorWords
import io.github.charlietap.chasm.compiler.backend.dex.abi.DexMethods
import io.github.charlietap.chasm.compiler.backend.dex.abi.referenceType
import io.github.charlietap.chasm.compiler.backend.dex.abi.valueType
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.ArrayType
import io.github.charlietap.dexter.BinaryOp
import io.github.charlietap.dexter.ClassType
import io.github.charlietap.dexter.CodeEmitter
import io.github.charlietap.dexter.CompareOp
import io.github.charlietap.dexter.Cond
import io.github.charlietap.dexter.Conversion
import io.github.charlietap.dexter.DexField
import io.github.charlietap.dexter.HandlerLabel
import io.github.charlietap.dexter.Label
import io.github.charlietap.dexter.LiteralOp
import io.github.charlietap.dexter.Reg
import io.github.charlietap.dexter.UnaryOp

internal class Emitter(
    private val code: CodeEmitter,
    private val methods: DexMethods,
) {

    private val labels = HashMap<String, Label>()
    private val handlers = HashMap<String, HandlerLabel>()
    private var fresh = 0

    fun place(
        name: String,
    ) = code.bind(label(name))

    fun freshLabel(): String = "l${fresh++}"

    fun moveWide(
        destination: Int,
        source: Int,
    ) {
        if (destination != source) code.moveWide(Reg(destination), Reg(source))
    }

    fun moveObject(
        destination: Int,
        source: Int,
    ) {
        if (destination != source) code.moveObject(Reg(destination), Reg(source))
    }

    fun move(
        destination: Int,
        source: Int,
    ) {
        if (destination != source) code.move(Reg(destination), Reg(source))
    }

    fun getField(
        destination: Int,
        instance: Int,
        owner: String,
        name: String,
        type: String,
    ) = code.getField(Reg(destination), Reg(instance), DexField(ClassType(owner), name, valueType(type)))

    fun putField(
        source: Int,
        instance: Int,
        owner: String,
        name: String,
        type: String,
    ) = code.putField(Reg(source), Reg(instance), DexField(ClassType(owner), name, valueType(type)))

    fun checkCast(
        register: Int,
        type: String,
    ) {
        if (type == OBJECT_TYPE) return
        code.checkCast(Reg(register), referenceType(type))
    }

    fun newArray(
        destination: Int,
        size: Int,
        type: String,
    ) = code.newArray(Reg(destination), Reg(size), referenceType(type) as ArrayType)

    fun constWide(
        destination: Int,
        value: Long,
    ) = code.constWide(Reg(destination), value)

    fun const(
        destination: Int,
        value: Int,
    ) = code.const(Reg(destination), value)

    fun unary(
        op: UnaryOp,
        destination: Int,
        source: Int,
    ) = code.unary(op, Reg(destination), Reg(source))

    fun convert(
        op: Conversion,
        destination: Int,
        source: Int,
    ) = code.convert(op, Reg(destination), Reg(source))

    fun binary(
        op: BinaryOp,
        destination: Int,
        left: Int,
        right: Int,
    ) = code.binary(op, Reg(destination), Reg(left), Reg(right))

    fun literal(
        op: LiteralOp,
        destination: Int,
        source: Int,
        literal: Int,
    ) = code.binary(op, Reg(destination), Reg(source), literal)

    fun compare(
        op: CompareOp,
        destination: Int,
        left: Int,
        right: Int,
    ) = code.compare(op, Reg(destination), Reg(left), Reg(right))

    fun arrayGet(
        kind: ArrayKind,
        destination: Int,
        array: Int,
        index: Int,
    ) = code.arrayGet(kind, Reg(destination), Reg(array), Reg(index))

    fun arrayPut(
        kind: ArrayKind,
        source: Int,
        array: Int,
        index: Int,
    ) = code.arrayPut(kind, Reg(source), Reg(array), Reg(index))

    fun returnVoid() = code.returnVoid()

    fun returnValue(
        register: Int,
    ) = code.returnValue(Reg(register))

    fun beginHandler(
        name: String,
        register: Int,
    ) = code.bindHandler(handler(name), Reg(register))

    fun gotoLabel(
        name: String,
    ) = code.goto(label(name))

    fun branchZero(
        cond: Cond,
        register: Int,
        target: String,
    ) = code.branchZero(cond, Reg(register), label(target))

    fun branchCompare(
        cond: Cond,
        left: Int,
        right: Int,
        target: String,
    ) = code.branch(cond, Reg(left), Reg(right), label(target))

    fun packedSwitch(
        value: Int,
        firstKey: Int,
        targets: List<String>,
    ) {
        val table = code.labelTable(firstKey, targets.size, label(targets.first()))
        targets.forEachIndexed { index, target -> table[index] = label(target) }
        code.packedSwitch(Reg(value), table)
    }

    fun catchRange(
        type: String,
        start: String,
        end: String,
        handler: String,
    ) = code.catchRange(label(start), label(end), ClassType(type), handler(handler))

    fun invoke(
        method: MethodRef,
        vararg registers: Int,
    ) {
        val arguments = arguments(method.parameters, registers, 0)
        val m = methods.of(method)
        when (arguments.size) {
            0 -> code.invokeStatic(m)
            1 -> code.invokeStatic(m, arguments[0])
            2 -> code.invokeStatic(m, arguments[0], arguments[1])
            3 -> code.invokeStatic(m, arguments[0], arguments[1], arguments[2])
            4 -> code.invokeStatic(m, arguments[0], arguments[1], arguments[2], arguments[3])
            5 -> code.invokeStatic(m, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4])
            else -> error("$m takes more than five arguments: use invokeRange")
        }
    }

    fun invokeVirtual(
        method: MethodRef,
        receiver: Int,
        vararg registers: Int,
    ) {
        val arguments = arguments(method.parameters, registers, 0, Reg(receiver))
        val m = methods.of(method)
        when (arguments.size) {
            1 -> code.invokeVirtual(m, arguments[0])
            2 -> code.invokeVirtual(m, arguments[0], arguments[1])
            3 -> code.invokeVirtual(m, arguments[0], arguments[1], arguments[2])
            4 -> code.invokeVirtual(m, arguments[0], arguments[1], arguments[2], arguments[3])
            5 -> code.invokeVirtual(m, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4])
            else -> error("$m takes more than four arguments: use invokeVirtualRange")
        }
    }

    fun instanceOf(
        destination: Int,
        register: Int,
        type: String,
    ) = code.instanceOf(Reg(destination), Reg(register), referenceType(type))

    fun invokeInterfaceRange(
        method: MethodRef,
        rangeStart: Int,
    ) = code.invokeInterfaceRange(methods.of(method), Reg(rangeStart))

    fun invokeVirtualRange(
        method: MethodRef,
        rangeStart: Int,
    ) = code.invokeVirtualRange(methods.of(method), Reg(rangeStart))

    fun invokeRange(
        method: MethodRef,
        rangeStart: Int,
        words: Int,
    ) {
        check(words == method.words) { "${methods.of(method)} takes ${method.words} words, not $words" }
        code.invokeStaticRange(methods.of(method), Reg(rangeStart))
    }

    fun moveResult(
        register: Int,
    ) = code.moveResult(Reg(register))

    fun moveResultWide(
        register: Int,
    ) = code.moveResult(Reg(register))

    fun moveResultObject(
        register: Int,
    ) = code.moveResult(Reg(register))

    fun line(
        line: Int,
    ) = code.line(line)

    private fun label(
        name: String,
    ): Label = labels.getOrPut(name) { code.label() }

    private fun handler(
        name: String,
    ): HandlerLabel = handlers.getOrPut(name) { code.handlerLabel() }

    private fun arguments(
        parameters: List<String>,
        words: IntArray,
        first: Int,
        receiver: Reg? = null,
    ): List<Reg> {
        val arguments = ArrayList<Reg>(parameters.size + 1)
        if (receiver != null) arguments += receiver
        var word = first
        for (parameter in parameters) {
            arguments += Reg(words[word])
            word += descriptorWords(parameter)
        }
        check(word == words.size) { "argument words do not match $parameters" }
        return arguments
    }
}
