package project;

import java.util.List;

public class AssemblerException extends Exception {
    private final List<String> errors;

    public AssemblerException(String message, List<String> errors) {
        super(message);
        this.errors = List.copyOf(errors);
    }

    public List<String> getErrors() {
        return errors;
    }
}
