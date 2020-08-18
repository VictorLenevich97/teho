package va.rit.teho.exception;

public class EquipmentNotFoundException extends NotFoundException {
    public EquipmentNotFoundException(Long equipmentId) {
        super("ВВСТ с id = " + equipmentId + " не существует");
    }
}
