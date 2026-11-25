package project;

import java.util.Arrays;
import java.util.List;

public record Instruction(int lineNumber, String mnemonic, List<String> operands) {
    private static final String WHITESPACE = "\\s+";

    public static Instruction parse(SourceLine sourceLine) {
        String[] tokens = sourceLine.text().split(WHITESPACE);
        List<String> operands = List.of(Arrays.copyOfRange(tokens, 1, tokens.length));
        return new Instruction(sourceLine.lineNumber(), tokens[0].toUpperCase(), operands);
    }

    public boolean hasOperand() {
        return !operands.isEmpty();
    }

    public String operand() {
        return operands.getFirst();
    }

}
