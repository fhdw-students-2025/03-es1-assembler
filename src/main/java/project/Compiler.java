package project;

import java.util.ArrayList;
import java.util.List;

import static project.Constants.OPCODES;

public class Compiler {
    private final List<String> compiledLines = new ArrayList<>();

    //TODO fallback für unerwartet falsche Syntax mit lines
    public List<String> compile(List<Line> lines) {
        for (Line line : lines) {
            compileLine(line.text());
        }
        return compiledLines;
    }

    private void compileLine(String line) {
        final int HIGH_NIBLE = 0;
        final int LOW_NIBLE = 1;

        String[] parts = line.split("\\s+");

        String cmd = parts[0];
        compiledLines.add(OPCODES.get(cmd));

        if (parts.length == 2) {
            String adr = parts[1];
            compiledLines.add(String.valueOf(adr.charAt(HIGH_NIBLE)));
            compiledLines.add(String.valueOf(adr.charAt(LOW_NIBLE)));
        }
    }
}
