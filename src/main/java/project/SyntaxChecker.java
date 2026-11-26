package project;

import project.constants.OperandType;
import project.records.Instruction;
import project.records.InstructionDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static project.constants.InstructionSet.*;

public class SyntaxChecker {

    private final Map<OperandType, Consumer<Instruction>> validateStrategies = Map.of(
            OperandType.NONE, this::validateNoOperand,
            OperandType.ADDRESS, this::validateAddress,
            OperandType.VALUE, this::validateValue,
            OperandType.OPTIONAL_ADDRESS, this::validateOptionalAddress
    );

    private static final String ADDRESS_PATTERN = "[0-9A-Fa-f]{2}";
    private static final String VALUE_PATTERN = "[0-9A-Fa-f]";
    private static final String ERROR_PREFIX = "Line ";

    private boolean isCallOpen = false;

    private final List<String> errors = new ArrayList<>();

    public void checkSyntax(List<Instruction> instructions) {
        isCallOpen = false;
        errors.clear();
        for (Instruction instruction : instructions) {
            checkInstruction(instruction);
        }

        if (isCallOpen) {
            errors.add("EOF reached with no closing \"call\"");
        }
    }

    private void checkInstruction(Instruction instruction) {
        String mnemonic = instruction.mnemonic();
        int lineNumber = instruction.lineNumber();

        InstructionDefinition definition = INSTRUCTIONS.get(mnemonic);

        if (definition == null) {
            addError(lineNumber, "unknown mnemonic \"" + mnemonic + "\"");
            return;
        }

        Consumer<Instruction> validateFunction = validateStrategies.get(definition.operandType());
        validateFunction.accept(instruction);
    }

    //CALL can't be called without operand, before being called exactly once with adr operand
    private void validateOptionalAddress(Instruction instruction) {
        int lineNumber = instruction.lineNumber();
        String mnemonic = instruction.mnemonic();

        if (instruction.operands().size() != 1) {
            if (!isCallOpen) {
                addError(lineNumber, "\"" + mnemonic + "\" requires one address operand");
            } else {
                isCallOpen = false;
            }
            return;
        }

        if (isCallOpen) {
            addError(lineNumber, "\"" + mnemonic + "\" cannot be called with an address twice");
            return;
        }

        if (!isValidAddress(instruction.operand())) {
            addError(lineNumber,
                    "invalid address \"" + instruction.operand() + "\" (expected two hex digits 00-FF, e.g. 0F)");
        }

        isCallOpen = true;
    }

    private void validateValue(Instruction instruction) {
        if (instruction.operands().size() != 1) {
            addError(instruction.lineNumber(), "\"" + instruction.mnemonic() + "\" requires exactly one value operand");
            return;
        }

        String value = instruction.operand();
        if (!value.matches(VALUE_PATTERN)) {
            addError(instruction.lineNumber(), "invalid value \"" + value + "\" (expected one hex digit 0-F)");
        }
    }

    private void validateAddress(Instruction instruction) {
        if (instruction.operands().size() != 1) {
            addError(instruction.lineNumber(), "\"" + instruction.mnemonic() + "\" requires exactly one address operand");
            return;
        }

        String adr = instruction.operand();
        if (!isValidAddress(adr)) {
            addError(instruction.lineNumber(), "invalid address \"" + adr + "\" (expected two hex digits 00-FF, e.g. 0F)");
        }
    }

    private void validateNoOperand(Instruction instruction) {
        if (instruction.hasOperand()) {
            addError(instruction.lineNumber(), "\"" + instruction.mnemonic() + "\" takes no operand");
        }
    }

    private boolean isValidAddress(String adr) {
        return adr.matches(ADDRESS_PATTERN);
    }

    private void addError(int lineNumber, String message) {
        errors.add(ERROR_PREFIX + lineNumber + ": " + message);
    }

    public List<String> getErrors() {
        return List.copyOf(errors);
    }
}