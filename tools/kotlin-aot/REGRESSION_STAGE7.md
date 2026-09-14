# Stage 7 regression investigation

Stage 7 is slower because its native locals add separate representations and
selection flags that survive JIT optimization and increase register pressure.
The generated code spills more state to the native stack. Changing Kotlin
`Long` declarations to `Int` was not, by itself, a useful optimization: C2
already emits 32-bit ARM integer operations for the relevant stage 6 code.

The branch checkpoint is `cbf8c1f9`. The unfinished stage 8 changes were preserved
in a named local stash before this investigation. No stage 8 code participates
in these results. The runtime and generator are the committed stage 7 versions;
the diagnostic harness adds selection of the existing generation tier.

## Controlled comparison

Both tiers run in the same runtime/classpath, with the same CoreMark binary,
fixed heap and compressed-pointer settings. Each trial uses a fresh JVM, with
the order shuffled within each pair. Counters, source compilation, JFR and JIT
logging are absent from the timed comparisons. CPU placement checks pass.

| Pair | STRUCTURED | TYPED |
| --- | ---: | ---: |
| 1 | 4857.91 | 3382.95 |
| 2 | 4888.78 | 3361.72 |
| 3 | 4906.37 | 3375.53 |
| 4 | 4861.45 | 3411.42 |
| 5 | 4895.16 | 3350.65 |
| Median | **4888.78** | **3375.53** |

TYPED scores 31.0% lower. Deterministic verification gives identical scores,
clock-call counts, full memory hashes and generated instruction counts. Both
implementations had already passed all 386 supported corpus fixtures in the
stage checkpoints. The 14 focused backend tests also pass after restoring the
stage 7 checkout. This comparison reproduces the regression without relying
on timing differences between the original stage 6 and stage 7 runs.

## What changed

Stage 6 keeps one raw word per physical slot:

```kotlin
var r3 = vstack.getFrameSlot(3)
// Numeric uses decode the word; writes replace it.
```

Stage 7 adds a native value and a flag alongside that word:

```kotlin
var b3 = vstack.getFrameSlot(3)
var r3 = b3.toInt()
var n3 = false
// A numeric write changes r3 and sets n3, retaining the old b3.
// A raw copy or boundary save selects the complete current word:
val word = if (n3) r3.toLong() else b3
```

The raw word is necessary under this design: physical slots can be reused, and
a path may skip the numeric write or receive a reference from a frame helper.
Discarding the upper bits would break correctness. However, carrying the old
word, the native value and the selection flag across control-flow joins gives
the register allocator more live state than the single-word representation.
Raw copies additionally select a word and decode it into another native local.

## JIT and machine-code evidence

Separate diagnostic runs collected JFR execution samples and HotSpot compilation
logs. The hot methods compile successfully with C2 in both tiers. There are no
compilation failures; the two methods below have matching C2 inlining outcomes.
The regression is not a failure to reach optimized JVM code.

The two regions of generated function 1 account for about 24% of sampled
execution in STRUCTURED and 37% in TYPED. These are sampled shares, not exact
instruction counts or independent performance measurements.

Separate assembly captures show:

| Method | STRUCTURED frame | TYPED frame | STRUCTURED main code | TYPED main code |
| --- | ---: | ---: | ---: | ---: |
| `GeneratedFunction1Region0.invoke` | 160 B | 224 B | 7656 B | 9536 B |
| `GeneratedFunction1Region1.invoke` | 160 B | 208 B | 10064 B | 11920 B |

For one corresponding copy/load/dispatch span in region 0, the structured
capture has 48 instructions with no stack-relative loads or stores. The typed
capture has 66 instructions containing 10 stack-relative loads and seven
stores, including loads of the selection flag and raw word. These are static
counts in the displayed spans, not a claim that every instruction executes on
every invocation. Exception and cold paths are excluded from this span count.

Both versions use 32-bit register instructions such as `and w...` and `eor w...`
for the relevant integer operations. The typed source removes explicit Kotlin
conversions, but does not remove an equivalent amount of actual machine work;
it introduces additional selection and spill traffic instead.

## Consequence

Stage 6 remains the best validated performance baseline. Stage 7's current
representation is an unsuccessful optimization, despite passing correctness.
A repair needs to avoid carrying independent raw/native/flag state through
joins, or prove which values can safely use a single native representation.
Simply removing raw-word preservation would fail the existing regression tests.
No optimizer repair or stage 8 continuation is included in this investigation.

## Reproduce

From the repository root, after the applicable correctness gates:

```sh
./gradlew :compiler:kotlin:jvmTest :tools:kotlin-aot:writeRuntimeClasspath \
  --no-configuration-cache --max-workers=4
python3 tools/kotlin-aot/compare_tiers.py --pairs 5 --profile
```

The script first verifies output in interpreter and both generated tiers, then
runs the uninstrumented comparisons. Optional JFR and compilation logs are
collected afterward. The CoreMark command also accepts `--tier STRUCTURED` or
`--tier TYPED`. Results and raw profiles go under
`tools/kotlin-aot/build/tier-comparison/`.

The retained [investigation record](results/stages/stage7-regression.json)
contains the original paired reports, verified artifact keys, profile summaries
and annotated machine-code spans. Full local diagnostics are under
`tools/kotlin-aot/build/regression-stage7/`. Native assembly was captured with
HotSpot `CompileCommand=print` and decoded with Capstone 5.0.7 on ARM64. The
earlier stage checkpoints and their measurements remain available.
