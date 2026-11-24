package project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Assembler {

    private final List<String> syntaxErrors = new ArrayList<>();

    public void assemble(String inputDateiPfad) throws IOException {
        List<String> lines = readFile(inputDateiPfad);

        SyntaxChecker syntaxChecker = new SyntaxChecker(syntaxErrors);
        Compiler compiler = new Compiler();

        syntaxChecker.checkSyntax(lines);

        if (!syntaxErrors.isEmpty()){
            System.out.println("Es wurden Syntaxfehler gefunden und der Code wurde nicht compiliert");
            System.out.println("Fehler:");

            for (String error : syntaxErrors){
                System.out.println(error);
            }
            return;
        }

        compiler.compile(lines);
    }

    private List<String> readFile(String inputDateiPfad) throws IOException {

        //TODO ErrorHandling bei nicht gefundenem File
        try (BufferedReader reader = new BufferedReader(new FileReader(inputDateiPfad))) {
            return reader.readAllLines();
        }
    }

}
