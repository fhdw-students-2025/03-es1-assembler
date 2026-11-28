package project.constants;

import project.records.InstructionDefinition;

import java.util.Map;

public final class InstructionSet {

    public static final Map<String, InstructionDefinition> INSTRUCTIONS = Map.ofEntries(
            Map.entry("AND", new InstructionDefinition("0", OperandType.NONE)),
            Map.entry("OR", new InstructionDefinition("1", OperandType.NONE)),
            Map.entry("NOT", new InstructionDefinition("2", OperandType.NONE)),
            Map.entry("ADD", new InstructionDefinition("3", OperandType.NONE)),
            Map.entry("SUB", new InstructionDefinition("4", OperandType.NONE)),
            Map.entry("MUL", new InstructionDefinition("5", OperandType.NONE)),

            Map.entry("RES1", new InstructionDefinition("6", OperandType.NONE)),
            Map.entry("RES2", new InstructionDefinition("7", OperandType.NONE)),

            Map.entry("JUMP", new InstructionDefinition("8", OperandType.ADDRESS)),
            Map.entry("JUMPC", new InstructionDefinition("9", OperandType.ADDRESS)),
            Map.entry("JUMPO", new InstructionDefinition("A", OperandType.ADDRESS)),
            Map.entry("JUMPZ", new InstructionDefinition("B", OperandType.ADDRESS)),

            Map.entry("LOAD", new InstructionDefinition("C", OperandType.ADDRESS)),
            Map.entry("SWAP", new InstructionDefinition("D", OperandType.NONE)),
            Map.entry("SAVE", new InstructionDefinition("E", OperandType.ADDRESS)),

            Map.entry("CALL", new InstructionDefinition("F", OperandType.OPTIONAL_ADDRESS)),
            Map.entry("SET", new InstructionDefinition(null, OperandType.VALUE))
    );

    private InstructionSet() {
    }
}
