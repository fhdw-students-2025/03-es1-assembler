package project.exceptions;

import java.util.List;

public class SyntaxException extends Exception {
    private final List<String> errors;

    public SyntaxException(String message, List<String> errors) {
        super(message);
        this.errors = List.copyOf(errors);
    }

    public List<String> getErrors() {
        return errors;
    }
}
