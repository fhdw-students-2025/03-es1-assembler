package project;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "assembler", mixinStandardHelpOptions = true, version="1.0", description = "Assembles a source file into machine code. Built for ES1 lecture at FHDW Hannover.")
public class Main implements Callable<Integer> {

    @Parameters(index = "0", description = "Source file")
    private Path sourcePath;

    @Parameters(index = "1", description = "Output file")
    private Path outputPath;

    @Override public Integer call() {
        try {
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

    public static void main(String[] args) {
        System.exit(new CommandLine(new Main()).execute(args));
    }
}
