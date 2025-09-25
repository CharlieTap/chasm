package io.github.charlietap.chasm.vm

import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.shapes.Tag

internal actual typealias FunctionExternalAddress = Function
internal actual typealias GlobalExternalAddress = Global
internal actual typealias MemoryExternalAddress = Memory
internal actual typealias TableExternalAddress = Table
internal actual typealias TagExternalAddress = Tag
