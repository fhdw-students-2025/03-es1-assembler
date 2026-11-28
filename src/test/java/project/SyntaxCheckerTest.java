package project;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import project.records.Instruction;
import project.records.SourceLine;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SyntaxCheckerTest {

    private final SyntaxChecker checker = new SyntaxChecker();

    // Builds the instructions the same way the Assembler does, line numbers start at 1
    private static List<Instruction> program(String... lines) {
        List<Instruction> instructions = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            instructions.add(Instruction.parse(new SourceLine(i + 1, lines[i])));
        }
        return instructions;
    }

    private List<String> errorsFor(String... lines) {
        checker.checkSyntax(program(lines));
        return checker.getErrors();
    }

    private static void assertSingleErrorInLine(int lineNumber, List<String> errors) {
        assertEquals(1, errors.size(), "expected exactly one error, got " + errors);
        assertTrue(errors.getFirst().startsWith("Line " + lineNumber + ":"), errors.getFirst());
    }

    // ---------- Valid programs ----------

    @Test
    void programUsingEveryInstructionHasNoErrors() {
        List<String> errors = errorsFor(
                "AND", "OR", "NOT", "ADD", "SUB", "MUL", "RES1", "RES2",
                "JUMP 00", "JUMPC 00", "JUMPO 00", "JUMPZ 00",
                "LOAD 00", "SWAP", "SAVE 00",
                "CALL 00", "CALL",
                "SET 0");

        assertEquals(List.of(), errors);
    }

    @Test
    void emptyProgramHasNoErrors() {
        assertEquals(List.of(), errorsFor());
    }

    // Edge cases: lowest and highest address and value, lower case hex digits
    @ParameterizedTest
    @ValueSource(strings = {"LOAD 00", "LOAD FF", "LOAD ff", "SET 0", "SET F", "SET f"})
    void operandsAtTheBoundariesAreValid(String line) {
        assertEquals(List.of(), errorsFor(line));
    }

    // ---------- Wrong mnemonic or operands ----------

    @Test
    void unknownMnemonicIsReportedWithLineNumber() {
        assertEquals(List.of("Line 2: unknown mnemonic \"NOP\""), errorsFor("ADD", "NOP"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"AND", "OR", "NOT", "ADD", "SUB", "MUL", "RES1", "RES2", "SWAP"})
    void instructionWithoutOperandRejectsOperand(String mnemonic) {
        assertSingleErrorInLine(1, errorsFor(mnemonic + " 05"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"JUMP", "JUMPC", "JUMPO", "JUMPZ", "LOAD", "SAVE"})
    void instructionWithAddressRequiresOperand(String mnemonic) {
        assertSingleErrorInLine(1, errorsFor(mnemonic));
    }

    // too short, too long, not hex, two operands
    @ParameterizedTest
    @ValueSource(strings = {"LOAD F", "LOAD 100", "LOAD G0", "LOAD F0 F1"})
    void invalidAddressIsRejected(String line) {
        assertSingleErrorInLine(1, errorsFor(line));
    }

    // two digits, not hex, missing, two operands
    @ParameterizedTest
    @ValueSource(strings = {"SET 10", "SET G", "SET", "SET 1 2"})
    void invalidSetValueIsRejected(String line) {
        assertSingleErrorInLine(1, errorsFor(line));
    }

    // ---------- CALL: "CALL adr" opens, "CALL" closes ----------

    @Test
    void twoCallsOneAfterAnotherAreValid() {
        assertEquals(List.of(), errorsFor("CALL 20", "ADD", "CALL", "CALL 30", "CALL"));
    }

    @Test
    void returnWithoutCallIsRejected() {
        assertSingleErrorInLine(1, errorsFor("CALL"));
    }

    @Test
    void callThatIsNeverClosedIsRejected() {
        assertEquals(List.of("EOF reached with no closing \"call\""), errorsFor("CALL 20"));
    }

    @Test
    void secondCallBeforeReturnIsRejected() {
        assertSingleErrorInLine(2, errorsFor("CALL 20", "CALL 30", "CALL"));
    }

    // ---------- Behaviour of the checker itself ----------

    @Test
    void allErrorsAreCollectedNotOnlyTheFirst() {
        List<String> errors = errorsFor("NOP", "ADD 1", "LOAD");

        assertEquals(3, errors.size(), errors.toString());
        assertTrue(errors.get(0).startsWith("Line 1:"));
        assertTrue(errors.get(1).startsWith("Line 2:"));
        assertTrue(errors.get(2).startsWith("Line 3:"));
    }

    // The first run leaves an error and an open CALL behind, the second run must not see either
    @Test
    void checkerCanBeReused() {
        errorsFor("NOP", "CALL 20");

        assertEquals(List.of(), errorsFor("ADD"));
    }
}
