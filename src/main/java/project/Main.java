package project;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String inputDateiPfad = args[0];

        Assembler assembler = new Assembler();
        try {
            assembler.assemble(inputDateiPfad);
        } catch (IOException e) {
            System.out.println("keine Textdatei unter: \"" + inputDateiPfad + "\" gefunden ");
        }
    }
}
