package project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Path sourcePath;
        Path outputPath;

        if (args.length == 0) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the source file path: ");
            sourcePath = Path.of(scanner.nextLine());
            if (!Files.exists(sourcePath)) {
                System.err.println("No file found at \"" + sourcePath.toAbsolutePath() + "\"");
                System.exit(1);
                return;
            }
            System.out.print("Enter the output file path: ");
            outputPath = Path.of(scanner.nextLine());

        } else if (args.length == 2) {
            sourcePath = Path.of(args[0]);
            outputPath = Path.of(args[1]);
        } else {
            System.err.println("Invalid number of arguments. Usage: <source file> <output file>");
            // Status 2 is commonly used for invalid argument format
            System.exit(2);
            return;
        }

        Assembler assembler = new Assembler();
        try {
            assembler.assemble(sourcePath, outputPath);
        } catch (AssemblerException e) {
            System.err.println(e.getMessage());
            for (String error : e.getErrors()) {
                System.err.println(error);
            }
            System.exit(1);
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
            System.exit(1);
        }
    }
}
