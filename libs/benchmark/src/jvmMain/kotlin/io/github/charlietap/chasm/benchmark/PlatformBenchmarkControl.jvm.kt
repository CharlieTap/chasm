package io.github.charlietap.chasm.benchmark

import com.sun.jna.Library
import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.NativeLong
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.LongByReference
import com.sun.jna.ptr.NativeLongByReference

internal actual object PlatformBenchmarkControl {

    actual fun enter(mode: BenchmarkMode): PolicyApplication {
        if (!isMacOs()) return unsupported(mode)

        return runCatching {
            when (mode) {
                BenchmarkMode.PREFER_FASTEST -> {
                    val result = Darwin.pthread_set_qos_class_self_np(QOS_CLASS_USER_INTERACTIVE, 0)
                    application(mode, result)
                }

                BenchmarkMode.SHORT_REALTIME -> enterRealtime(mode)
            }
        }.getOrElse { error ->
            unsupported(mode, "Darwin policy APIs are unavailable: ${error.message}")
        }
    }

    actual fun topology(): BenchmarkTopology = topology(System.getProperty("os.name"))

    internal fun topology(osName: String): BenchmarkTopology {
        if (osName != MAC_OS_NAME) return unavailableTopology()

        return runCatching { darwinTopology() }.getOrElse { unavailableTopology() }
    }

    private fun darwinTopology(): BenchmarkTopology {
        val logicalCpuCount = Darwin.sysctlInt("hw.logicalcpu") ?: 0
        val fastestCpuCount = Darwin.sysctlInt("hw.perflevel0.logicalcpu")
        val fastestClassName = Darwin.sysctlString("hw.perflevel0.name")
        val deviceTreeIds = Darwin.fastestDeviceTreeCpuIds()
        val detectedFastestCpuIds = deviceTreeIds.orEmpty()
        val fastestCpuIds = detectedFastestCpuIds.takeIf { it.isNotEmpty() && currentCpu() != null }.orEmpty()

        return BenchmarkTopology(
            logicalCpuCount = logicalCpuCount,
            fastestCpuCount = fastestCpuCount,
            fastestClassName = fastestClassName,
            fastestCpuIds = fastestCpuIds,
            source = when {
                fastestCpuIds.isEmpty() -> CpuTopologySource.UNAVAILABLE
                else -> CpuTopologySource.DEVICE_TREE
            },
        )
    }

    actual fun currentCpu(): Int? {
        if (!isMacOs()) return null

        return runCatching {
            val cpu = LongByReference()
            if (Darwin.pthread_cpu_number_np(cpu) == 0) cpu.value.toInt() else null
        }.getOrNull()
    }

    actual fun sleepMillis(durationMillis: Long) {
        if (durationMillis == 0L) {
            Thread.yield()
        } else {
            Thread.sleep(durationMillis)
        }
    }

    private fun enterRealtime(mode: BenchmarkMode): PolicyApplication {
        val ticksPerSecond = Darwin.sysctlLong("hw.tbfrequency")
            ?: return PolicyApplication(mode, false, message = "hw.tbfrequency is unavailable")
        val computation = microsToTicks(REALTIME_COMPUTATION_MICROS, ticksPerSecond)
        val constraint = microsToTicks(REALTIME_CONSTRAINT_MICROS, ticksPerSecond)
        val thread = Darwin.mach_thread_self()
        val result = try {
            Darwin.thread_policy_set(
                thread,
                THREAD_TIME_CONSTRAINT_POLICY,
                intArrayOf(0, computation, constraint, 1),
                THREAD_TIME_CONSTRAINT_POLICY_COUNT,
            )
        } finally {
            Darwin.mach_port_deallocate(Darwin.mach_task_self(), thread)
        }

        return application(mode, result)
    }

    private fun microsToTicks(micros: Long, ticksPerSecond: Long): Int =
        (micros * ticksPerSecond / MICROS_PER_SECOND).toInt()

    private fun application(mode: BenchmarkMode, result: Int): PolicyApplication =
        PolicyApplication(
            mode = mode,
            isApplied = result == 0,
            nativeErrorCode = result.takeUnless { it == 0 },
            message = if (result == 0) null else "Darwin policy call failed with code $result",
        )

    private fun isMacOs(): Boolean = System.getProperty("os.name") == MAC_OS_NAME

    private fun unavailableTopology() =
        BenchmarkTopology(
            logicalCpuCount = Runtime.getRuntime().availableProcessors(),
            fastestCpuCount = null,
            fastestClassName = null,
            fastestCpuIds = emptySet(),
            source = CpuTopologySource.UNAVAILABLE,
        )

    private fun unsupported(
        mode: BenchmarkMode,
        message: String = "Benchmark placement control is only available on supported macOS systems",
    ) = PolicyApplication(
        mode = mode,
        isApplied = false,
        message = message,
        isSupported = false,
    )

    private object Darwin {
        private val system = Native.load("System", SystemLibrary::class.java)
        private val ioKit = Native.load("IOKit", IOKitLibrary::class.java)
        private val coreFoundation = Native.load("CoreFoundation", CoreFoundationLibrary::class.java)

        fun pthread_set_qos_class_self_np(qosClass: Int, relativePriority: Int): Int =
            system.pthread_set_qos_class_self_np(qosClass, relativePriority)

        fun pthread_cpu_number_np(cpu: LongByReference): Int = system.pthread_cpu_number_np(cpu)

        fun mach_thread_self(): Int = system.mach_thread_self()

        fun mach_task_self(): Int = system.mach_task_self()

        fun mach_port_deallocate(task: Int, name: Int): Int = system.mach_port_deallocate(task, name)

        fun thread_policy_set(
            thread: Int,
            flavor: Int,
            policy: IntArray,
            count: Int,
        ): Int = system.thread_policy_set(thread, flavor, policy, count)

        fun sysctlInt(name: String): Int? {
            val memory = Memory(Int.SIZE_BYTES.toLong())
            val size = NativeLongByReference(NativeLong(Int.SIZE_BYTES.toLong()))
            return if (system.sysctlbyname(name, memory, size, null, NativeLong(0)) == 0) {
                memory.getInt(0)
            } else {
                null
            }
        }

        fun sysctlLong(name: String): Long? {
            val memory = Memory(Long.SIZE_BYTES.toLong())
            val size = NativeLongByReference(NativeLong(Long.SIZE_BYTES.toLong()))
            return if (system.sysctlbyname(name, memory, size, null, NativeLong(0)) == 0) {
                memory.getLong(0)
            } else {
                null
            }
        }

        fun sysctlString(name: String): String? {
            val size = NativeLongByReference()
            if (system.sysctlbyname(name, null, size, null, NativeLong(0)) != 0) return null
            if (size.value.toLong() <= 1) return null

            val memory = Memory(size.value.toLong())
            return if (system.sysctlbyname(name, memory, size, null, NativeLong(0)) == 0) {
                memory.getString(0)
            } else {
                null
            }
        }

        fun fastestDeviceTreeCpuIds(): Set<Int>? {
            val root = ioKit.IORegistryEntryFromPath(0, DEVICE_TREE_CPU_PATH)
            if (root == 0) return null

            val iteratorReference = IntByReference()
            val iteratorResult = ioKit.IORegistryEntryGetChildIterator(
                root,
                DEVICE_TREE_PLANE,
                iteratorReference,
            )
            ioKit.IOObjectRelease(root)
            if (iteratorResult != 0) return null

            val clusterTypes = mutableMapOf<Int, Char>()
            val clusterTypeKey = coreFoundation.CFStringCreateWithCString(
                null,
                "cluster-type",
                CF_STRING_ENCODING_UTF8,
            )
            try {
                var entry = ioKit.IOIteratorNext(iteratorReference.value)
                while (entry != 0) {
                    try {
                        val name = ByteArray(IO_NAME_SIZE)
                        if (ioKit.IORegistryEntryGetName(entry, name) == 0) {
                            val cpuId = name.decodeToString().trimEnd('\u0000').removePrefix("cpu").toIntOrNull()
                            val property = ioKit.IORegistryEntryCreateCFProperty(entry, clusterTypeKey, null, 0)
                            if (property != null) {
                                try {
                                    if (
                                        cpuId != null &&
                                        coreFoundation.CFGetTypeID(property) == coreFoundation.CFDataGetTypeID()
                                    ) {
                                        val bytes = coreFoundation.CFDataGetBytePtr(property)
                                        if (bytes != null && coreFoundation.CFDataGetLength(property) > 0) {
                                            clusterTypes[cpuId] = bytes.getByte(0).toInt().toChar()
                                        }
                                    }
                                } finally {
                                    coreFoundation.CFRelease(property)
                                }
                            }
                        }
                    } finally {
                        ioKit.IOObjectRelease(entry)
                    }
                    entry = ioKit.IOIteratorNext(iteratorReference.value)
                }
            } finally {
                coreFoundation.CFRelease(clusterTypeKey)
                ioKit.IOObjectRelease(iteratorReference.value)
            }

            val fastestType = FASTEST_CLUSTER_TYPES.firstOrNull(clusterTypes.values::contains) ?: return null
            return clusterTypes.filterValues { it == fastestType }.keys
        }
    }

    private interface SystemLibrary : Library {
        fun pthread_set_qos_class_self_np(qosClass: Int, relativePriority: Int): Int

        fun pthread_cpu_number_np(cpuNumber: LongByReference): Int

        fun mach_thread_self(): Int

        fun mach_task_self(): Int

        fun mach_port_deallocate(task: Int, name: Int): Int

        fun thread_policy_set(thread: Int, flavor: Int, policy: IntArray, count: Int): Int

        fun sysctlbyname(
            name: String,
            oldValue: Pointer?,
            oldLength: NativeLongByReference,
            newValue: Pointer?,
            newLength: NativeLong,
        ): Int
    }

    private interface IOKitLibrary : Library {
        fun IORegistryEntryFromPath(mainPort: Int, path: String): Int

        fun IORegistryEntryGetChildIterator(entry: Int, plane: String, iterator: IntByReference): Int

        fun IOIteratorNext(iterator: Int): Int

        fun IORegistryEntryGetName(entry: Int, name: ByteArray): Int

        fun IORegistryEntryCreateCFProperty(
            entry: Int,
            key: Pointer,
            allocator: Pointer?,
            options: Int,
        ): Pointer?

        fun IOObjectRelease(obj: Int): Int
    }

    private interface CoreFoundationLibrary : Library {
        fun CFStringCreateWithCString(allocator: Pointer?, value: String, encoding: Int): Pointer

        fun CFDataGetLength(data: Pointer): Long

        fun CFDataGetBytePtr(data: Pointer): Pointer?

        fun CFGetTypeID(value: Pointer): Long

        fun CFDataGetTypeID(): Long

        fun CFRelease(value: Pointer)
    }

    private const val QOS_CLASS_USER_INTERACTIVE = 0x21
    private const val MAC_OS_NAME = "Mac OS X"
    private const val THREAD_TIME_CONSTRAINT_POLICY = 2
    private const val THREAD_TIME_CONSTRAINT_POLICY_COUNT = 4
    private const val REALTIME_COMPUTATION_MICROS = 5_000L
    private const val REALTIME_CONSTRAINT_MICROS = 10_000L
    private const val MICROS_PER_SECOND = 1_000_000L
    private const val DEVICE_TREE_CPU_PATH = "IODeviceTree:/cpus"
    private const val DEVICE_TREE_PLANE = "IODeviceTree"
    private const val CF_STRING_ENCODING_UTF8 = 0x08000100
    private const val IO_NAME_SIZE = 128
    private val FASTEST_CLUSTER_TYPES = charArrayOf('P', 'M', 'E')
}
