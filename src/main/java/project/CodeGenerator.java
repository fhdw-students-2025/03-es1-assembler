package project;

import project.constants.OperandType;
import project.exceptions.GenerationException;
import project.records.Instruction;
import project.records.InstructionDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static project.constants.InstructionSet.INSTRUCTIONS;

public class CodeGenerator {

    private static final int HEX_RADIX = 16;
    private static final int MEMORY_SIZE = 256;
    private static final int VALUES_START = MEMORY_SIZE - 1;

    List<String> machineCode;

    private List<String> values;

    private final Map<OperandType, Consumer<Instruction>> generators = Map.of(
            OperandType.NONE, this::generateNoOp,
            OperandType.ADDRESS, this::generateAdr,
            OperandType.VALUE, this::generateValueOp,
            OperandType.OPTIONAL_ADDRESS, this::generateOptional
    );

    public List<String> generate(List<Instruction> instructions) throws GenerationException {
        values = new ArrayList<>();
        machineCode = new ArrayList<>();
        for (Instruction instruction : instructions) {
            encodeInstruction(instruction);
        }

        if (machineCode.size() + values.size() > MEMORY_SIZE) {
            throw new GenerationException("Generated File exceeds the Memory-size of:" + MEMORY_SIZE);
        }

        while (machineCode.size() < MEMORY_SIZE) {
            machineCode.add("0");
        }

        generateValues();

        return machineCode;
    }

    private void generateValues() {
        int valIndex = VALUES_START;
        for (String value : values) {
            machineCode.set(valIndex, value);
            valIndex--;
        }
    }

    private void encodeInstruction(Instruction instruction) throws GenerationException {
        InstructionDefinition definition = INSTRUCTIONS.get(instruction.mnemonic());
        if (definition == null) {
            throw new GenerationException("CodeGenerator received unchecked instruction: " + instruction);
        }
        generators.get(definition.operandType()).accept(instruction);
    }

    private void generateOptional(Instruction instruction) {
        if (instruction.hasOperand()) {
            generateAdr(instruction);
        } else {
            generateNoOp(instruction);
        }
    }

    private void generateValueOp(Instruction instruction) {
        values.add(instruction.operand());
    }

    private void generateAdr(Instruction instruction) {
        generateNoOp(instruction);
        final int HIGH_NIBBLE_INDEX = 0;
        final int LOW_NIBBLE_INDEX = 1;

        int value = Integer.parseInt(instruction.operand(), HEX_RADIX);
        String hex = String.format("%02X", value); // always two uppercase digits
        machineCode.add(String.valueOf(hex.charAt(HIGH_NIBBLE_INDEX)));
        machineCode.add(String.valueOf(hex.charAt(LOW_NIBBLE_INDEX)));
    }

    private void generateNoOp(Instruction instruction) {
        machineCode.add(INSTRUCTIONS.get(instruction.mnemonic()).opcode());
    }
}