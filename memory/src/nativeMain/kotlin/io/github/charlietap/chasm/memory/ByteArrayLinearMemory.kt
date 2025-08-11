package io.github.charlietap.chasm.memory

import io.github.charlietap.chasm.runtime.memory.LinearMemory

class ByteArrayLinearMemory(
    var memory: ByteArray,
) : LinearMemory {
    constructor(
        pages: LinearMemory.Pages,
    ) : this(
        ByteArray(pages.amount.toInt() * LinearMemory.PAGE_SIZE),
    )
}
