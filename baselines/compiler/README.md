# Compiler baselines

Each fixture has two baselines:

- `*-frontend.json` records the Chasm IR the compiler frontend emits for it;
- `*-backend.json` records the dispatchers the Chasm backend installs for it and the values each
  captured, read from the program after instantiation.

When compiler output changes, `./gradlew checkCompilerBaseline` fails with the changed lines.
Accept an intended change with `./gradlew updateCompilerBaseline`. The fixtures live in
`tools/compiler-baseline/src/main/resources/compiler-baseline`; rebuild `contrived.wasm` with
`wasm-tools parse contrived.wat -o contrived.wasm`.

CoreMark's backend baseline is linked into Chasm's default store, where checkpoints link to
nothing. The contrived fixture exists to demonstrate everything the compiler does, so its
backend baseline is linked into a store that meters fuel and can be interrupted, where every
checkpoint links to a check.

## Backend baselines

### Layout

- `modules[].functions[]` has one entry per defined function, in module order.
- Each function describes its compiled frame, with offsets from FP:
  - `frame_slots`: slots the frame needs;
  - `activation_header_slot`: where the activation header (return address and caller frame)
    sits. Parameters arrive and results leave in the slots below it, so it is the larger of
    the parameter and result counts. Defined locals and temporaries follow it;
  - `local_initialization`: which locals are set on entry, and to what;
  - `unlinked_checkpoints` (only when there are any): the CIR positions of checkpoints that link
    to nothing in this store;
  - `exceptions` (only for functions with exception handlers): the regions and catches.
- `instructions[]` lists the program instructions in order:
  - `offset` is the instruction's position within the function;
  - `position` is the position of the CIR instruction it was linked from, as in the CIR
    baseline;
  - `dispatcher` names the factory that built it, e.g. `I32AddSsDispatcher`;
  - `variant` (only for factories that build several dispatchers) says which one it is, e.g.
    a call specialised for how many of its callee's locals it zeroes. The numbers are the
    compiler's own, so they change when that factory's source does;
  - `fields` holds the values the dispatcher captured, named after that factory's parameters,
    and is left out when there are none.

### Values

- **Targets:** `"@n"` is the instruction at offset `n` in the same function. A callee's
  `entry_ip` names the function instead, e.g. `"func[2]"`.
- **References:** a captured function reference is shown as its function, e.g. `"func[2]"`;
  other references by their kind, e.g. `{"kind": "null", ...}`.
- **Activation headers:** a call's packed header is shown as the offset it `return`s to, the
  `caller_frame_delta`, and `result_call` when the callee's single result goes straight to a
  caller slot.
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

## Frontend baselines

Each `*-frontend.json` file records the Chasm IR the compiler frontend emits for the same
fixture, before the Chasm backend links it, in CIR's own JSON format. The same tasks check and
update both kinds of baseline. CIR positions are indices into the function's instructions, so
`"target": 5` is the function's sixth instruction, and module objects are plain indices, e.g.
`"memory": 0`.
