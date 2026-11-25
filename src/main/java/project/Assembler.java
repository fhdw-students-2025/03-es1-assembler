package project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Assembler {

    private static final String COMMENT_PREFIX = ";";

    public void assemble(Path sourcePath, Path outputPath) throws IOException, AssemblerException {
        List<SourceLine> sourceLines = readSourceLines(sourcePath);
        List<SourceLine> codeLines = removeBlankAndCommentLines(sourceLines);
        // Split each code line once into mnemonic and operands
        List<Instruction> instructions = codeLines.stream().map(Instruction::parse).toList();

        SyntaxChecker syntaxChecker = new SyntaxChecker();
        syntaxChecker.checkSyntax(instructions);

        final List<String> syntaxErrors = syntaxChecker.getErrors();

        if (!syntaxErrors.isEmpty()) {
            throw new AssemblerException("Syntax errors found. The program was not assembled.", syntaxErrors);
        }

        CodeGenerator codeGenerator = new CodeGenerator();
        List<String> machineCode = codeGenerator.generate(instructions);
        writeMachineCode(machineCode, outputPath);
    }

    private List<SourceLine> removeBlankAndCommentLines(List<SourceLine> sourceLines) {
        List<SourceLine> codeLines = new ArrayList<>();

        for (SourceLine sourceLine : sourceLines) {
            String text = sourceLine.text().trim();
            if (text.isEmpty() || text.startsWith(COMMENT_PREFIX)) {
                continue;
            }
            codeLines.add(new SourceLine(sourceLine.lineNumber(), text));
        }
        return codeLines;
    }

    private List<SourceLine> readSourceLines(Path sourcePath) throws IOException {
        List<SourceLine> sourceLines = new ArrayList<>();
        int lineNumber = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(sourcePath.toFile()))) {
            for (String text : reader.readAllLines()) {
                sourceLines.add(new SourceLine(++lineNumber, text));
            }
        }
        return sourceLines;
    }

    private void writeMachineCode(List<String> machineCode, Path outputPath) throws IOException {
        if (Files.exists(outputPath)) {
            System.err.println("Warning: " + outputPath + " already exists and will be overwritten!");
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath.toFile()))) {
            for (String word : machineCode) {
                writer.write(word);
                writer.newLine();
            }
        }
    }
}
