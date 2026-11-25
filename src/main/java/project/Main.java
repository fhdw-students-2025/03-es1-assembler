package project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Path sourcePath;
        Path outputPath;

        if (args.length == 0) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the source file path: ");
            sourcePath = Path.of(scanner.nextLine());
            if (!Files.exists(sourcePath)) {
                System.out.println("No file found at \"" + sourcePath.toAbsolutePath() + "\"");
                return;
            }
            System.out.print("Enter the output file path: ");
            outputPath = Path.of(scanner.nextLine());

        } else if (args.length == 2) {
            sourcePath = Path.of(args[0]);
            outputPath = Path.of(args[1]);
        } else {
            System.out.println("Invalid number of arguments. Usage: <source file> <output file>");
            return;
        }

        Assembler assembler = new Assembler();
        assembler.assemble(sourcePath, outputPath);
    }
}
