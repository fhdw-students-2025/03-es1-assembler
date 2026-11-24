package project;

import java.util.List;

import static project.Constants.*;
import static project.Constants.CMDS_WITH_ADR;

public class SyntaxChecker {

    public static final String SYNTAX_ERROR_MSG_START = "Syntax Fehler in Zeile ";

    List<String> syntaxErrors;

    public SyntaxChecker (List<String> syntaxErrorsReference){
        this.syntaxErrors = syntaxErrorsReference;
    }

    public void checkSyntax(List<String> lines) {
        int zeilenNummer = 0;
        for (String line : lines) {

            zeilenNummer++;

            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty() || trimmedLine.startsWith("//")) {
                continue;
            }

            //Zeile in Adresse und Befehl splitten (\s+ ist eine beliebige Einrückung)
            String[] parts = trimmedLine.split("\\s+");

            checkParts(zeilenNummer, parts);

            if (parts.length == 2) {
                checkAdr(zeilenNummer, parts[1]);
            }
        }
    }

    private void checkParts(int zeilenNummer, String[] parts) {
        if (!OPCODES.containsKey(parts[0])) {
            syntaxErrors.add(SYNTAX_ERROR_MSG_START + zeilenNummer + " unbekannter Befehl");
            return;
        }

        if (!CMDS_WITH_ADR.contains(parts[0]) && parts.length != 1) {
            syntaxErrors.add(SYNTAX_ERROR_MSG_START + zeilenNummer + " cmd: " + parts[0] + " hat keinen Operanden");
        }

        if (CMDS_WITH_ADR.contains(parts[0]) && parts.length != 2) {
            syntaxErrors.add(SYNTAX_ERROR_MSG_START + zeilenNummer + " cmd: " + parts[0] + " braucht einen Operanden");
        }
    }

    private void checkAdr(int zeilenNummer, String adr) {
        if (!adr.startsWith("0x")) {
            syntaxErrors.add(SYNTAX_ERROR_MSG_START + zeilenNummer + " ungültige Adresse");
            return;
        }
        String adrValue = adr.substring(2);
        int decimalValue;

        try {
            decimalValue = Integer.parseInt(adrValue, 16); //parseInt mit zweitem Parameter setzt die Basis
        } catch (NumberFormatException e) {
            syntaxErrors.add(SYNTAX_ERROR_MSG_START + zeilenNummer + " keine gültige Hexadresse");
            return;
        }

        if (decimalValue < 0 || decimalValue > 255) {
            syntaxErrors.add(SYNTAX_ERROR_MSG_START + zeilenNummer + " Adresse liegt außerhalb des gültigen Adressraums");
        }
    }
}
