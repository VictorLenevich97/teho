package by.varb.teho.exception;

public class TehoException extends Exception {

    public TehoException(String message) {
        super(message);
    }

    public TehoException(Exception e) {
        super(e);
    }
}
