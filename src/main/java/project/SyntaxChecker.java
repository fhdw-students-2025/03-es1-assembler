package project;

import java.util.ArrayList;
import java.util.List;

import static project.InstructionSet.MNEMONICS_WITH_OPERAND;
import static project.InstructionSet.OPCODES;

public class SyntaxChecker {

    private static final String ADDRESS_PATTERN = "[0-9A-Fa-f]{2}";
    private static final String ERROR_PREFIX = "Line ";

    private final List<String> errors = new ArrayList<>();

    public void checkSyntax(List<Instruction> instructions) {
        for (Instruction instruction : instructions) {
            checkInstruction(instruction);
        }
    }

    private void checkInstruction(Instruction instruction) {
        String mnemonic = instruction.mnemonic();
        int lineNumber = instruction.lineNumber();

        if (!OPCODES.containsKey(mnemonic)) {
            addError(lineNumber, "unknown mnemonic \"" + mnemonic + "\"");
            return;
        }

        boolean expectsOperand = MNEMONICS_WITH_OPERAND.contains(mnemonic);
        int operandCount = instruction.operands().size();

        if (!expectsOperand && operandCount != 0) {
            addError(lineNumber, "\"" + mnemonic + "\" takes no operand");
        } else if (expectsOperand && operandCount != 1) {
            addError(lineNumber, "\"" + mnemonic + "\" requires exactly one address operand");
        } else if (expectsOperand) {
            checkAddress(lineNumber, instruction.operand());
        }
    }

    private void checkAddress(int lineNumber, String address) {
        if (!address.matches(ADDRESS_PATTERN)) {
            addError(lineNumber, "invalid address \"" + address + "\" (expected two hex digits 00-FF, e.g. 0F)");
        }
    }

    private void addError(int lineNumber, String message) {
        errors.add(ERROR_PREFIX + lineNumber + ": " + message);
    }

    public List<String> getErrors() {
        return errors;
    }
}
