package by.varb.teho.exception;

public class EmptyFieldException extends TehoException {

    private static final String MESSAGE = "Недостаточно данных - одно или несколько полей не заполнены";

    public EmptyFieldException() {
        super(MESSAGE);
    }
}
