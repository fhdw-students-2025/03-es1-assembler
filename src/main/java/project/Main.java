package project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "assembler", mixinStandardHelpOptions = true, version="1.0", description = "Assembles a source file into machine code. Built for ES1 lecture at FHDW Hannover.")
public class Main implements Callable<Integer> {

    private static final Set<String> YES_ANSWERS = Set.of("y", "yes");

    @Parameters(index = "0", description = "Source file")
    private Path sourcePath;

    @Parameters(index = "1", description = "Output file")
    private Path outputPath;

    @Override public Integer call() {
        try {
            // Checked before the overwrite prompt, so we don't ask about a run that can't succeed
            if (!Files.exists(sourcePath)) {
                System.err.println("No file found at \"" + sourcePath.toAbsolutePath() + "\"");
                return 1;
            }
            if (isSameFile(sourcePath, outputPath)) {
                System.err.println("Source and output file must be different: " + outputPath);
                return 2;
            }
            if (Files.exists(outputPath) && !confirmOverwrite(outputPath)) {
                System.err.println("Aborted. " + outputPath + " was not changed.");
                return 1;
            }
            new Assembler().assemble(sourcePath, outputPath);
            return 0;
        } catch (AssemblerException e) {
            System.err.println(e.getMessage());
            for (String error : e.getErrors()) {
                System.err.println(error);
            }
            return 1;
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
            return 1;
        }
    }

    // isSameFile also catches different spellings of one file, e.g. "a.asm" and "./a.asm"
    private static boolean isSameFile(Path a, Path b) throws IOException {
        return Files.exists(b) && Files.isSameFile(a, b);
    }

    // Prompt goes to stderr so stdout stays clean; no input (EOF) counts as "no"
    private static boolean confirmOverwrite(Path path) throws IOException {
        System.err.print(path + " already exists. Overwrite? [y/N] ");
        String answer = new BufferedReader(new InputStreamReader(System.in)).readLine();
        return answer != null && YES_ANSWERS.contains(answer.trim().toLowerCase());
    }

    public static void main(String[] args) {
        System.exit(new CommandLine(new Main()).execute(args));
    }
}
