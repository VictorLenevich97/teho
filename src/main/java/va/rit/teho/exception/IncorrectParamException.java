package va.rit.teho.exception;

public class IncorrectParamException extends TehoException {
    public IncorrectParamException(String message) {
        super(message);
    }

    public IncorrectParamException(String paramName, Object paramValue) {
        super("Некорректное значение параметра \"" + paramName + "\": \"" + paramValue.toString() + "\"");
    }
}
