package va.rit.teho.exception;

public class AlreadyExistsException extends TehoException {
    public AlreadyExistsException(String entity, String param, Object value) {
        super(entity + "(" + param + " = " + value + ") уже существует!");
    }
}
