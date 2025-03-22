package io.github.charlietap.chasm.ir.module

import io.github.charlietap.chasm.type.DefinedType

data class Module(
    val version: Version,
    val types: List<Type>,
    val definedTypes: List<DefinedType>,
    val imports: List<Import>,
    val functions: List<Function>,
    val tables: List<Table>,
    val memories: List<Memory>,
    val tags: List<Tag>,
    val globals: List<Global>,
    val exports: List<Export>,
    val startFunction: StartFunction?,
    val elementSegments: List<ElementSegment>,
    val dataSegments: List<DataSegment>,
    val customs: List<Custom>,
)
