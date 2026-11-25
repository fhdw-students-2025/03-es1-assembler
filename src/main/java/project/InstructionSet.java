package project;

import java.util.Map;
import java.util.Set;

public final class InstructionSet {

    public static final Map<String, String> OPCODES = Map.ofEntries(
            Map.entry("AND", "0"), Map.entry("OR", "1"), Map.entry("NOT", "2"),
            Map.entry("ADD", "3"), Map.entry("SUB", "4"), Map.entry("MUL", "5"),
            Map.entry("RES1", "6"), Map.entry("RES2", "7"),
            Map.entry("JUMP", "8"), Map.entry("JUMPC", "9"),
            Map.entry("JUMPO", "A"), Map.entry("JUMPZ", "B"),
            Map.entry("LOAD", "C"), Map.entry("SWAP", "D"),
            Map.entry("SAVE", "E"), Map.entry("RES3", "F"));

    public static final Set<String> MNEMONICS_WITH_OPERAND =
            Set.of("JUMP", "JUMPC", "JUMPO", "JUMPZ", "LOAD", "SAVE");

    public static final int MAX_ADDRESS = 0xFF;

    private InstructionSet() {
    }
}
