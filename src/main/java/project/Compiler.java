package project;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Compiler {
    private final List<String> compiledLines = new ArrayList<>();

    public void compile(List<String> lines){
        for (String line : lines){
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty() || trimmedLine.startsWith("//")) {
                continue;
            }
            compileLine(trimmedLine);
        }
        writeToDocument();
    }

    private void compileLine(String line){
        //TODO compiling logik einbauen
    }

    private void writeToDocument() {
        //TODO Dateipfad anpassen
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("platzhalter"))) {
            for (String line : compiledLines){
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
