package project;

import java.util.ArrayList;
import java.util.List;

import static project.InstructionSet.MAX_ADDRESS;
import static project.InstructionSet.MNEMONICS_WITH_OPERAND;
import static project.InstructionSet.OPCODES;

public class SyntaxChecker {

    private static final String ERROR_PREFIX = "Line ";
    private static final String WHITESPACE = "\\s+";
    private static final int MNEMONIC_INDEX = 0;
    private static final int OPERAND_INDEX = 1;
    private static final int HEX_RADIX = 16;

    private final List<String> errors = new ArrayList<>();

    public void checkSyntax(List<SourceLine> sourceLines) {
        for (SourceLine sourceLine : sourceLines) {
            // Split into mnemonic and operand (\s+ matches any whitespace)
            String[] tokens = sourceLine.text().split(WHITESPACE);

            checkInstruction(sourceLine.lineNumber(), tokens);

            if (tokens.length == 2 && MNEMONICS_WITH_OPERAND.contains(tokens[MNEMONIC_INDEX])) {
                checkAddress(sourceLine.lineNumber(), tokens[OPERAND_INDEX]);
            }
        }
    }

    private void checkInstruction(int lineNumber, String[] tokens) {
        String mnemonic = tokens[MNEMONIC_INDEX];

        if (!OPCODES.containsKey(mnemonic)) {
            addError(lineNumber, "unknown mnemonic \"" + mnemonic + "\"");
            return;
        }

        boolean expectsOperand = MNEMONICS_WITH_OPERAND.contains(mnemonic);

        if (!expectsOperand && tokens.length != 1) {
            addError(lineNumber, "\"" + mnemonic + "\" takes no operand");
        }

        if (expectsOperand && tokens.length != 2) {
            addError(lineNumber, "\"" + mnemonic + "\" requires an address operand");
        }
    }

    private void checkAddress(int lineNumber, String address) {
        int addressValue;

        try {
            addressValue = Integer.parseInt(address, HEX_RADIX);
        } catch (NumberFormatException e) {
            addError(lineNumber, "invalid hex address \"" + address + "\" (use hex digits 0-F, e.g. FF)");
            return;
        }

        if (addressValue < 0 || addressValue > MAX_ADDRESS) {
            addError(lineNumber, "address \"" + address + "\" is outside the valid range 00-FF");
        }
    }

    private void addError(int lineNumber, String message) {
        errors.add(ERROR_PREFIX + lineNumber + ": " + message);
    }

    public List<String> getErrors() {
        return errors;
    }
}
