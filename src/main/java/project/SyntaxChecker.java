package project;

import java.util.ArrayList;
import java.util.List;

import static project.Constants.*;
import static project.Constants.CMDS_WITH_ADR;

public class SyntaxChecker {

    public static final String SYNTAX_ERROR_MSG_START = "Zeile ";
    private static final int PARTS_CMD_INDEX = 0;
    private static final int PARTS_ADR_INDEX = 1;


    List<String> syntaxErrors = new ArrayList<>();

    public void checkSyntax(List<Line> lines) {
        for (Line line : lines) {
            //Zeile in Adresse und Befehl splitten (\s+ ist eine beliebige Einrückung)
            String[] parts = line.text().split("\\s+");

            checkParts(line.zeilenNummer(), parts);

            if (parts.length == 2 && CMDS_WITH_ADR.contains(parts[PARTS_CMD_INDEX])) {
                checkAdr(line.zeilenNummer(), parts[PARTS_ADR_INDEX]);
            }
        }
    }

    private void checkParts(int zeilenNummer, String[] parts) {
        if (!OPCODES.containsKey(parts[PARTS_CMD_INDEX])) {
            syntaxErrors.add(SYNTAX_ERROR_MSG_START + zeilenNummer + " unbekannter Befehl: " + parts[PARTS_CMD_INDEX]);
            return;
        }

        if (!CMDS_WITH_ADR.contains(parts[PARTS_CMD_INDEX]) && parts.length != 1) {
            syntaxErrors.add(SYNTAX_ERROR_MSG_START + zeilenNummer + " cmd: " + parts[PARTS_CMD_INDEX] + " erwartet keinen Operanden");
        }

        if (CMDS_WITH_ADR.contains(parts[PARTS_CMD_INDEX]) && parts.length != 2) {
            syntaxErrors.add(SYNTAX_ERROR_MSG_START + zeilenNummer + " cmd: " + parts[PARTS_CMD_INDEX] + " braucht einen Operanden");
        }
    }

    private void checkAdr(int zeilenNummer, String adr) {
        int decimalValue;

        try {
            decimalValue = Integer.parseInt(adr, 16); //parseInt mit zweitem Parameter setzt die Basis
        } catch (NumberFormatException e) {
            syntaxErrors.add(SYNTAX_ERROR_MSG_START + zeilenNummer + " keine gültige Hexadresse: " + adr + " Hexziffern von 0-F z.B FF");
            return;
        }

        if (decimalValue < 0 || decimalValue > 255) {
            syntaxErrors.add(SYNTAX_ERROR_MSG_START + zeilenNummer + " Adresse " + adr + " liegt außerhalb des gültigen Adressraums");
        }
    }

    public List<String> getSyntaxErrors() {
        return syntaxErrors;
    }
}
