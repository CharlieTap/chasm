package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.dex.abi.DexReferences
import io.github.charlietap.chasm.compiler.backend.dex.abi.Invocation
import io.github.charlietap.chasm.compiler.backend.dex.abi.valueType
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.ArrayType
import io.github.charlietap.dexter.BinaryOp
import io.github.charlietap.dexter.ClassType
import io.github.charlietap.dexter.CodeEmitter
import io.github.charlietap.dexter.CompareOp
import io.github.charlietap.dexter.Cond
import io.github.charlietap.dexter.Conversion
import io.github.charlietap.dexter.DexConstructor
import io.github.charlietap.dexter.DexField
import io.github.charlietap.dexter.HandlerLabel
import io.github.charlietap.dexter.Label
import io.github.charlietap.dexter.Labels
import io.github.charlietap.dexter.LiteralOp
import io.github.charlietap.dexter.Reg
import io.github.charlietap.dexter.UnaryOp

internal class Emitter(
    private val code: CodeEmitter,
    private val references: DexReferences,
    private val recording: Boolean = false,
) {

    private val argumentWords = IntArray(MAX_INVOKE_WORDS)

    fun place(label: Label) {
        if (recording) return
        code.bind(label)
    }

    fun freshLabel(): Label {
        return code.label()
    }

    fun labels(count: Int): Labels {
        return code.labels(count)
    }

    fun handlerLabel(): HandlerLabel {
        return code.handlerLabel()
    }

    fun moveWide(
        destination: Int,
        source: Int,
    ) {
        if (recording) return
        if (destination != source) code.moveWide(Reg(destination), Reg(source))
    }

    fun moveObject(
        destination: Int,
        source: Int,
    ) {
        if (recording) return
        if (destination != source) code.moveObject(Reg(destination), Reg(source))
    }

    fun move(
        destination: Int,
        source: Int,
    ) {
        if (recording) return
        if (destination != source) code.move(Reg(destination), Reg(source))
    }

    fun getField(
        destination: Int,
        instance: Int,
        owner: String,
        name: String,
        type: String,
    ) {
        if (recording) return
        code.getField(Reg(destination), Reg(instance), references.field(owner, name, type))
    }

    fun putField(
        source: Int,
        instance: Int,
        owner: String,
        name: String,
        type: String,
    ) {
        if (recording) return
        code.putField(Reg(source), Reg(instance), references.field(owner, name, type))
    }

    fun getStatic(
        destination: Int,
        field: DexField,
    ) {
        if (recording) return
        code.getStatic(Reg(destination), field)
    }

    fun checkCast(
        register: Int,
        type: String,
    ) {
        if (recording || type == OBJECT_TYPE) return
        code.checkCast(Reg(register), references.reference(type))
    }

    fun newArray(
        destination: Int,
        size: Int,
        type: String,
    ) {
        if (recording) return
        code.newArray(Reg(destination), Reg(size), references.reference(type) as ArrayType)
    }

    fun newInstance(
        destination: Int,
        type: String,
    ) {
        if (recording) return
        code.newInstance(Reg(destination), references.reference(type) as ClassType)
    }

    fun invokeConstructor(
        owner: String,
        receiver: Int,
    ) {
        if (recording) return
        code.invokeConstructor(DexConstructor(references.reference(owner) as ClassType), Reg(receiver))
    }

    fun invokeConstructor(
        owner: String,
        parameter: String,
        receiver: Int,
        argument: Int,
    ) {
        if (recording) return
        code.invokeConstructor(DexConstructor(references.reference(owner) as ClassType, valueType(parameter)), Reg(receiver), Reg(argument))
    }

    fun invokeConstructor(
        owner: String,
        firstParameter: String,
        secondParameter: String,
        receiver: Int,
        firstArgument: Int,
        secondArgument: Int,
    ) {
        if (recording) return
        code.invokeConstructor(
            DexConstructor(references.reference(owner) as ClassType, valueType(firstParameter), valueType(secondParameter)),
            Reg(receiver),
            Reg(firstArgument),
            Reg(secondArgument),
        )
    }

    fun constWide(
        destination: Int,
        value: Long,
    ) {
        if (recording) return
        code.constWide(Reg(destination), value)
    }

    fun const(
        destination: Int,
        value: Int,
    ) {
        if (recording) return
        code.const(Reg(destination), value)
    }

    fun unary(
        op: UnaryOp,
        destination: Int,
        source: Int,
    ) {
        if (recording) return
        code.unary(op, Reg(destination), Reg(source))
    }

    fun convert(
        op: Conversion,
        destination: Int,
        source: Int,
    ) {
        if (recording) return
        code.convert(op, Reg(destination), Reg(source))
    }

    fun binary(
        op: BinaryOp,
        destination: Int,
        left: Int,
        right: Int,
    ) {
        if (recording) return
        code.binary(op, Reg(destination), Reg(left), Reg(right))
    }

    fun literal(
        op: LiteralOp,
        destination: Int,
        source: Int,
        literal: Int,
    ) {
        if (recording) return
        code.binary(op, Reg(destination), Reg(source), literal)
    }

    fun compare(
        op: CompareOp,
        destination: Int,
        left: Int,
        right: Int,
    ) {
        if (recording) return
        code.compare(op, Reg(destination), Reg(left), Reg(right))
    }

    fun arrayLength(
        destination: Int,
        array: Int,
    ) {
        if (recording) return
        code.arrayLength(Reg(destination), Reg(array))
    }

    fun arrayGet(
        kind: ArrayKind,
        destination: Int,
        array: Int,
        index: Int,
    ) {
        if (recording) return
        code.arrayGet(kind, Reg(destination), Reg(array), Reg(index))
    }

    fun arrayPut(
        kind: ArrayKind,
        source: Int,
        array: Int,
        index: Int,
    ) {
        if (recording) return
        code.arrayPut(kind, Reg(source), Reg(array), Reg(index))
    }

    fun returnVoid() {
        if (recording) return
        code.returnVoid()
    }

    fun returnValue(register: Int) {
        if (recording) return
        code.returnValue(Reg(register))
    }

    fun beginHandler(
        handler: HandlerLabel,
        register: Int,
    ) {
        if (recording) return
        code.bindHandler(handler, Reg(register))
    }

    fun gotoLabel(target: Label) {
        if (recording) return
        code.goto(target)
    }

    fun branchZero(
        cond: Cond,
        register: Int,
        target: Label,
    ) {
        if (recording) return
        code.branchZero(cond, Reg(register), target)
    }

    fun branchCompare(
        cond: Cond,
        left: Int,
        right: Int,
        target: Label,
    ) {
        if (recording) return
        code.branch(cond, Reg(left), Reg(right), target)
    }

    fun packedSwitch(
        value: Int,
        firstKey: Int,
        targets: List<Label>,
    ) {
        if (recording) return
        val table = code.labelTable(firstKey, targets.size, targets.first())
        targets.forEachIndexed { index, target -> table[index] = target }
        code.packedSwitch(Reg(value), table)
    }

    fun catchRange(
        type: String,
        start: Label,
        end: Label,
        handler: HandlerLabel,
    ) {
        if (recording) return
        code.catchRange(start, end, ClassType(type), handler)
    }

    fun invoke(method: MethodRef) {
        invokeStatic(method, 0)
    }

    fun invoke(
        method: MethodRef,
        a: Int,
    ) {
        argumentWords[0] = a
        invokeStatic(method, 1)
    }

    fun invoke(
        method: MethodRef,
        a: Int,
        b: Int,
    ) {
        argumentWords[0] = a
        argumentWords[1] = b
        invokeStatic(method, 2)
    }

    fun invoke(
        method: MethodRef,
        a: Int,
        b: Int,
        c: Int,
    ) {
        argumentWords[0] = a
        argumentWords[1] = b
        argumentWords[2] = c
        invokeStatic(method, 3)
    }

    fun invoke(
        method: MethodRef,
        a: Int,
        b: Int,
        c: Int,
        d: Int,
    ) {
        argumentWords[0] = a
        argumentWords[1] = b
        argumentWords[2] = c
        argumentWords[3] = d
        invokeStatic(method, 4)
    }

    fun invoke(
        method: MethodRef,
        a: Int,
        b: Int,
        c: Int,
        d: Int,
        e: Int,
    ) {
        argumentWords[0] = a
        argumentWords[1] = b
        argumentWords[2] = c
        argumentWords[3] = d
        argumentWords[4] = e
        invokeStatic(method, 5)
    }

    fun invokeVirtual(
        method: MethodRef,
        receiver: Int,
    ) {
        invokeVirtual(method, receiver, 0)
    }

    fun invokeVirtual(
        method: MethodRef,
        receiver: Int,
        a: Int,
        b: Int,
    ) {
        argumentWords[0] = a
        argumentWords[1] = b
        invokeVirtual(method, receiver, 2)
    }

    fun instanceOf(
        destination: Int,
        register: Int,
        type: String,
    ) {
        if (recording) return
        code.instanceOf(Reg(destination), Reg(register), references.reference(type))
    }

    fun invokeInterfaceRange(
        method: MethodRef,
        rangeStart: Int,
    ) {
        if (recording) return
        code.invokeInterfaceRange(references.of(method), Reg(rangeStart))
    }

    fun invokeVirtualRange(
        method: MethodRef,
        rangeStart: Int,
    ) {
        if (recording) return
        code.invokeVirtualRange(references.of(method), Reg(rangeStart))
    }

    fun invokeRange(
        method: MethodRef,
        rangeStart: Int,
        words: Int,
    ) {
        check(words == method.words) { "${references.of(method)} takes ${method.words} words, not $words" }
        if (recording) return
        code.invokeStaticRange(references.of(method), Reg(rangeStart))
    }

    fun invokeRange(
        invocation: Invocation,
        rangeStart: Int,
        words: Int,
    ) {
        check(words == invocation.words) { "${invocation.method} takes ${invocation.words} words, not $words" }
        if (recording) return
        code.invokeStaticRange(invocation.method, Reg(rangeStart))
    }

    fun moveResult(register: Int) {
        if (recording) return
        code.moveResult(Reg(register))
    }

    fun moveResultWide(register: Int) {
        if (recording) return
        code.moveResult(Reg(register))
    }

    fun moveResultObject(register: Int) {
        if (recording) return
        code.moveResult(Reg(register))
    }

    fun line(line: Int) {
        if (recording) return
        code.line(line)
    }

    private fun invokeStatic(
        method: MethodRef,
        count: Int,
    ) {

        val invocation = references.invocation(method)
        check(count == invocation.words) { "argument words do not match ${method.parameters}" }
        if (recording) return

        val target = invocation.method
        val offsets = invocation.offsets
        when (offsets.size) {
            0 -> code.invokeStatic(target)
            1 -> code.invokeStatic(target, argument(offsets, 0))
            2 -> code.invokeStatic(target, argument(offsets, 0), argument(offsets, 1))
            3 -> code.invokeStatic(target, argument(offsets, 0), argument(offsets, 1), argument(offsets, 2))
            4 -> code.invokeStatic(target, argument(offsets, 0), argument(offsets, 1), argument(offsets, 2), argument(offsets, 3))
            5 -> code.invokeStatic(target, argument(offsets, 0), argument(offsets, 1), argument(offsets, 2), argument(offsets, 3), argument(offsets, 4))
            else -> error("$target takes more than five arguments: use invokeRange")
        }
    }

    private fun invokeVirtual(
        method: MethodRef,
        receiver: Int,
        count: Int,
    ) {

        val invocation = references.invocation(method)
        check(count == invocation.words) { "argument words do not match ${method.parameters}" }
        if (recording) return

        val target = invocation.method
        val offsets = invocation.offsets
        when (offsets.size) {
            0 -> code.invokeVirtual(target, Reg(receiver))
            1 -> code.invokeVirtual(target, Reg(receiver), argument(offsets, 0))
            2 -> code.invokeVirtual(target, Reg(receiver), argument(offsets, 0), argument(offsets, 1))
            3 -> code.invokeVirtual(target, Reg(receiver), argument(offsets, 0), argument(offsets, 1), argument(offsets, 2))
            4 -> code.invokeVirtual(target, Reg(receiver), argument(offsets, 0), argument(offsets, 1), argument(offsets, 2), argument(offsets, 3))
            else -> error("$target takes more than four arguments: use invokeVirtualRange")
        }
    }

    private fun argument(
        offsets: IntArray,
        index: Int,
    ): Reg {
        return Reg(argumentWords[offsets[index]])
    }
}

private const val MAX_INVOKE_WORDS = 10
