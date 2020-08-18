package va.rit.teho.exception;

public class EquipmentNotUniqueException extends TehoException {

    private static final String MESSAGE = "Такой образец ВВСТ уже существует в базе данных";

    public EquipmentNotUniqueException() {
        super(MESSAGE);
    }
}
