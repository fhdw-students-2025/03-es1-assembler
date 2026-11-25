package project;

import java.util.ArrayList;
import java.util.List;

import static project.InstructionSet.OPCODES;

public class CodeGenerator {

    private static final int HEX_RADIX = 16;

    public List<String> generate(List<Instruction> instructions) {
        List<String> machineCode = new ArrayList<>();
        for (Instruction instruction : instructions) {
            encodeInstruction(instruction, machineCode);
        }
        return machineCode;
    }

    private void encodeInstruction(Instruction instruction, List<String> machineCode) {
        String opcode = OPCODES.get(instruction.mnemonic());
        if (opcode == null) {
            throw new IllegalStateException("CodeGenerator received unchecked instruction: " + instruction);
        }
        machineCode.add(opcode);

        if (instruction.hasOperand()) {
            int value = Integer.parseInt(instruction.operand(), HEX_RADIX);
            String hex = String.format("%02X", value); // always two uppercase digits
            machineCode.add(hex.substring(0, 1));
            machineCode.add(hex.substring(1, 2));
        }
    }
}
