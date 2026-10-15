# Compiler baselines

Each `*-baseline.json` file records exactly what the compiler produces for one fixture. When
compiler output changes, `./gradlew checkCompilerBaseline` fails with the changed lines.
Accept an intended change with `./gradlew updateCompilerBaseline`. The fixtures live in
`tools/compiler-baseline/src/main/resources/compiler-baseline`; rebuild `contrived.wasm` with
`wasm-tools parse contrived.wat -o contrived.wasm`.

## Layout

- `modules[].functions[]` has one entry per defined function, in module order.
- Each function describes its compiled frame, with offsets from FP:
  - `frame_slots`: slots the frame needs;
  - `activation_header_slot`: where the activation header (return address and caller frame)
    sits. Parameters arrive and results leave in the slots below it, so it is the larger of
    the parameter and result counts. Defined locals and temporaries follow it;
  - `local_initialization`: which locals are set on entry, and to what;
  - `exceptions` (only for functions with exception handlers): the regions and catches.
- `instructions[]` lists the program instructions in order:
  - `offset` is the instruction's position within the function;
  - `tag` names the instruction;
  - `immediates` holds the values compiled into it, and is left out when there are none.

## Values

- **Targets:** `"@n"` is the instruction at offset `n` in the same function.
- **Runtime objects:** referred to by their index in the module, e.g. `"memory[0]"`,
  `"table[1]"`, `"global[2]"`, `"func[7]"`, `"type[3]"`, `"tag[0]"`, `"data[0]"`,
  `"elem[0]"`. `"module"` is the module instance itself.
- **Floating point:** written as a value followed by its raw bits, e.g.
  `"1.0 (0x3f800000)"`, so NaN payloads are exact.
- **Variants of a sealed type:** objects carry a `kind`, e.g.
  `{"kind": "slot", "slot": 2}` or `{"kind": "i32_lt_s", ...}`.
- **Operand transfers:** `sources` lists where each value comes from. `moves` lists the copies
  performed, in order:
  - `"+n"` is the destination base plus `n`;
  - `"slot s"` reads frame slot `s`;
  - `scratch` is a temporary used to break cycles.
- **No destination:** a `result_destination_slot` of `-1` means the call leaves its results in
  place.
- **Local defaults:** `{"kind": "constant_stores", "values": [...]}` lists the encoded initial
  value of each local that needs one. Non-zero values are encoded reference values, e.g. a null
  `externref`.
