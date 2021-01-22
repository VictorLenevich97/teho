package va.rit.teho.exception;

public class FormationNotFoundException extends NotFoundException {
    public FormationNotFoundException(Long formationId) {
        super("Формирование (id = " + formationId + ") не найдено!");
    }
}
