package by.varb.teho.exception;

public class TehoException extends RuntimeException {

    public TehoException(String message) {
        super(message);
    }

    public TehoException(Exception e) {
        super(e);
    }
}
