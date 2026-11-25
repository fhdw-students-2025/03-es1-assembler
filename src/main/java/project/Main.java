package project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Path input;
        Path output;

        if (args.length == 0) {
            Scanner keyboard = new Scanner(System.in);
            System.out.print("bitte gebe den Pfad der input Pfad ein: ");
            input = Path.of(keyboard.nextLine());
            if (!Files.exists(input)) {
                System.out.println("keine Datei unter \"" + input.toAbsolutePath() + "\" gefunden");
                return;
            }
            System.out.print("bitte gebe den output Pfad ein: ");
            output = Path.of(keyboard.nextLine());

        } else if (args.length == 2) {
            input = Path.of(args[0]);
            output = Path.of(args[1]);
        } else {
            System.out.println("keine gültige Anzahl an Parametern");
            return;
        }

        Assembler assembler = new Assembler();
        assembler.assemble(input, output);
    }
}
