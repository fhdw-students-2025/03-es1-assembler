package project;

import java.util.ArrayList;
import java.util.List;

import static project.InstructionSet.OPCODES;

public class CodeGenerator {

    private static final String WHITESPACE = "\\s+";
    private static final int HIGH_NIBBLE_INDEX = 0;
    private static final int LOW_NIBBLE_INDEX = 1;

    private final List<String> machineCode = new ArrayList<>();

    // TODO: handle unexpected invalid syntax gracefully
    public List<String> generate(List<SourceLine> sourceLines) {
        for (SourceLine sourceLine : sourceLines) {
            encodeInstruction(sourceLine.text());
        }
        return machineCode;
    }

    private void encodeInstruction(String instruction) {
        String[] tokens = instruction.split(WHITESPACE);

        String mnemonic = tokens[0];
        machineCode.add(OPCODES.get(mnemonic));

        if (tokens.length == 2) {
            String address = tokens[1];
            machineCode.add(String.valueOf(address.charAt(HIGH_NIBBLE_INDEX)));
            machineCode.add(String.valueOf(address.charAt(LOW_NIBBLE_INDEX)));
        }
    }
}
