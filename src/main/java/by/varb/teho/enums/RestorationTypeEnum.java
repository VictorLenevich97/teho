package by.varb.teho.enums;

public enum RestorationTypeEnum {
    TACTICAL("Тактический"), OPERATIONAL("Оперативный"), STRATEGIC("Стратегический");

    private final String name;

    RestorationTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
