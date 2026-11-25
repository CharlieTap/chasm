package io.github.charlietap.chasm.corpus

fun cpuIndices(cpuList: String): List<Int> {
    return cpuList.split(',').flatMap { range ->
        val first = range.substringBefore('-').trim().toInt()
        val last = range.substringAfter('-').trim().toInt()
        (first..last).toList()
    }
}

fun fastestCpu(
    cpus: List<Int>,
    maxFrequencyKhz: (Int) -> Long,
): Int {
    return cpus.sorted().maxBy { cpu -> maxFrequencyKhz(cpu) }
}
