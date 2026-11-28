package project;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import project.exceptions.GenerationException;
import project.records.Instruction;
import project.records.SourceLine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The CodeGenerator assumes that the SyntaxChecker already accepted the program.
 * That is why these tests only use valid input, except for the unknown mnemonic edge case.
 */
class CodeGeneratorTest {

    private static final int MEMORY_SIZE = 256;

    private final CodeGenerator generator = new CodeGenerator();

    // Builds the instructions the same way the Assembler does, line numbers start at 1
    private static List<Instruction> program(String... lines) {
        List<Instruction> instructions = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            instructions.add(Instruction.parse(new SourceLine(i + 1, lines[i])));
        }
        return instructions;
    }

    // ---------- Opcodes ----------

    // SWAP comes second: if it lands at address 01, the instruction took exactly one cell
    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
            "AND, 0", "OR, 1", "NOT, 2", "ADD, 3", "SUB, 4", "MUL, 5",
            "RES1, 6", "RES2, 7", "SWAP, D"
    })
    void instructionWithoutOperandTakesOneCell(String mnemonic, String opcode) throws GenerationException {
        List<String> code = generator.generate(program(mnemonic, "SWAP"));

        assertEquals(List.of(opcode, "D"), code.subList(0, 2));
    }

    // 3A has two different digits, so a swapped high and low digit would be noticed
    @ParameterizedTest(name = "{0} 3A -> {1} 3 A")
    @CsvSource({"JUMP, 8", "JUMPC, 9", "JUMPO, A", "JUMPZ, B", "LOAD, C", "SAVE, E"})
    void instructionWithAddressIsOpcodeFollowedByAddress(String mnemonic, String opcode) throws GenerationException {
        List<String> code = generator.generate(program(mnemonic + " 3A"));

        assertEquals(List.of(opcode, "3", "A"), code.subList(0, 3));
    }

    // Edge cases: leading zero, highest address, lower case input
    @ParameterizedTest(name = "LOAD {0} -> C {1} {2}")
    @CsvSource({"05, 0, 5", "FF, F, F", "ff, F, F"})
    void addressIsAlwaysTwoUpperCaseDigits(String address, String high, String low) throws GenerationException {
        List<String> code = generator.generate(program("LOAD " + address));

        assertEquals(List.of("C", high, low), code.subList(0, 3));
    }

    @Test
    void callWithAddressAndReturnWithout() throws GenerationException {
        List<String> code = generator.generate(program("CALL 20", "ADD", "CALL"));

        assertEquals(List.of("F", "2", "0", "3", "F"), code.subList(0, 5));
    }

    // ---------- SET values ----------

    @Test
    void setValuesAreStoredBackwardsFromTheLastCell() throws GenerationException {
        List<String> code = generator.generate(program("SET 5", "SET 7", "ADD"));

        assertEquals("3", code.getFirst());   // SET creates no instruction, so ADD is at address 00
        assertEquals("5", code.get(255)); // first SET -> address FF
        assertEquals("7", code.get(254)); // second SET -> address FE
    }

    // ---------- Output format ----------

    @Test
    void exampleProgramAddAsm() throws GenerationException {
        List<String> code = generator.generate(program(
                "LOAD F0", "SWAP", "LOAD F1", "ADD", "SAVE F2", "JUMP 0B"));

        assertEquals(List.of("C", "F", "0", "D", "C", "F", "1", "3", "E", "F", "2", "8", "0", "B"),
                code.subList(0, 14));
        assertEquals(MEMORY_SIZE, code.size());
        assertEquals("0", code.get(14)); // the rest is filled with zeros
    }

    @Test
    void emptyProgramIsOnlyZeros() throws GenerationException {
        List<String> code = generator.generate(List.of());

        assertEquals(Collections.nCopies(MEMORY_SIZE, "0"), code);
    }

    // ---------- Edge cases: memory size ----------

    @Test
    void programThatFillsTheMemoryExactlyIsAccepted() throws GenerationException {
        String[] lines = Collections.nCopies(MEMORY_SIZE, "ADD").toArray(String[]::new);

        List<String> code = generator.generate(program(lines));

        assertEquals(Collections.nCopies(MEMORY_SIZE, "3"), code);
    }

    @Test
    void programOneCellTooLargeThrows() {
        String[] lines = Collections.nCopies(MEMORY_SIZE + 1, "ADD").toArray(String[]::new);

        assertThrows(GenerationException.class, () -> generator.generate(program(lines)));
    }

    // 255 instructions + 2 values = 257 cells, so the values must count towards the limit
    @Test
    void setValuesCountTowardsTheMemorySize() {
        List<String> lines = new ArrayList<>(Collections.nCopies(MEMORY_SIZE - 1, "ADD"));
        lines.add("SET 1");
        lines.add("SET 2");

        assertThrows(GenerationException.class,
                () -> generator.generate(program(lines.toArray(String[]::new))));
    }

    // ---------- Edge case: no definition for the mnemonic ----------

    @Test
    void unknownMnemonicThrows() {
        assertThrows(GenerationException.class, () -> generator.generate(program("NOP")));
    }
}
