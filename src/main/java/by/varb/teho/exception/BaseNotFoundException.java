package by.varb.teho.exception;

public class BaseNotFoundException extends NotFoundException{
    public BaseNotFoundException(Long baseId) {
        super("Не существует id = " + baseId);
    }
}
