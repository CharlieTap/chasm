package io.github.charlietap.chasm.compiler.context

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.instance.TagInstance
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.ext.arrayType
import io.github.charlietap.chasm.type.ext.structType

internal fun CompilerContext.function(index: Index.FunctionIndex): FunctionInstance =
    functions[index.toInt()]

internal fun CompilerContext.table(index: Index.TableIndex): TableInstance =
    tables[index.toInt()]

internal fun CompilerContext.memory(index: Index.MemoryIndex): MemoryInstance =
    memories[index.toInt()]

internal fun CompilerContext.tag(index: Index.TagIndex): TagInstance =
    tags[index.toInt()]

internal fun CompilerContext.global(index: Index.GlobalIndex): GlobalInstance =
    globals[index.toInt()]

internal fun CompilerContext.element(index: Index.ElementIndex): ElementInstance =
    elements[index.toInt()]

internal fun CompilerContext.data(index: Index.DataIndex): DataInstance =
    data[index.toInt()]

internal fun CompilerContext.rtt(index: Index.TypeIndex): RTT = runtimeTypes[index.toInt()]

internal fun CompilerContext.arrayType(index: Index.TypeIndex): ArrayType =
    module.definedTypes[index.toInt()].asSubType.compositeType.arrayType()
        ?: error("type ${index.idx} is not an array type")

internal fun CompilerContext.structType(index: Index.TypeIndex): StructType =
    module.definedTypes[index.toInt()].asSubType.compositeType.structType()
        ?: error("type ${index.idx} is not a struct type")
