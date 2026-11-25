package project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Assembler {

    public void assemble(Path inputDateiPfad, Path outputDateiPfad) throws IOException {
        List<Line> lines = readFile(inputDateiPfad);

        SyntaxChecker syntaxChecker = new SyntaxChecker();

        Compiler compiler = new Compiler();

        List<Line> trimmedLines = trimmLines(lines);

        syntaxChecker.checkSyntax(trimmedLines);

        final List<String> syntaxErrors = syntaxChecker.getSyntaxErrors();

        if (!syntaxErrors.isEmpty()) {
            System.out.println("Es wurden Syntaxfehler gefunden und der Code wurde nicht compiliert");
            System.out.println("Fehler:");

            for (String error : syntaxErrors) {
                System.out.println(error);
            }
            return;
        }

        List<String> compiledLines = compiler.compile(trimmedLines);
        writeToDocument(compiledLines, outputDateiPfad);
    }

    private List<Line> trimmLines(List<Line> lines) {
        List<Line> trimmedLines = new ArrayList<>();

        for (Line line : lines) {
            Line trimmedLine = new Line(line.zeilenNummer(), line.text().trim());
            if (trimmedLine.text().isEmpty() || trimmedLine.text().startsWith(";")) {
                continue;
            }
            trimmedLines.add(trimmedLine);
        }
        return trimmedLines;
    }

    private List<Line> readFile(Path inputDateiPfad) throws IOException {
        List<Line> lines = new ArrayList<>();
        int zeilenNummer = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(inputDateiPfad.toFile()))) {
            for (String line : reader.readAllLines()) {
                lines.add(new Line(++zeilenNummer, line));
            }
        }
        return lines;
    }

    private void writeToDocument(List<String> compiledLines, Path outputDateiPfad) {
        if (Files.exists(outputDateiPfad)) {
            System.out.println(outputDateiPfad + " ist bereits vorhanden und wird überschrieben");
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputDateiPfad.toFile()))) {
            for (String line : compiledLines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("ein Fehler beim Schreiben ist aufgetreten: " + e.getMessage() + "ausgelöst durch" + e.getCause());
        }
    }
}
