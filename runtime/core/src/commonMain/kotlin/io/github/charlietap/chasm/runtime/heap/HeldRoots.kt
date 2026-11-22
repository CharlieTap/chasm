package io.github.charlietap.chasm.runtime.heap

import io.github.charlietap.chasm.gc.GcRootMarker

interface HeldRoots {

    fun visit(marker: GcRootMarker)
}
