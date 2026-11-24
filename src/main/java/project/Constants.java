package project;

import java.util.Map;
import java.util.Set;

public class Constants {

    public static final Map<String, Integer> OPCODES = Map.ofEntries(
            Map.entry("AND", 0x0), Map.entry("OR", 0x1), Map.entry("NOT", 0x2),
            Map.entry("ADD", 0x3), Map.entry("SUB", 0x4), Map.entry("MUL", 0x5),
            Map.entry("RES1", 0x6), Map.entry("RES2", 0x7),
            Map.entry("JUMP", 0x8), Map.entry("JUMPC", 0x9),
            Map.entry("JUMPO", 0xA), Map.entry("JUMPZ", 0xB),
            Map.entry("LOAD", 0xC), Map.entry("SWAP", 0xD),
            Map.entry("SAVE", 0xE), Map.entry("RES3", 0xF));

    public static final Set<String> CMDS_WITH_ADR =
            Set.of("JUMP", "JUMPC", "JUMPO", "JUMPZ", "LOAD", "SAVE");
}