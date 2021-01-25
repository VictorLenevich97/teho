package va.rit.teho.enums;

public enum RestorationTypeEnum {
    TACTICAL("Тактический", 1), OPERATIONAL("Оперативный", 2), STRATEGIC("Стратегический", 3);

    private final String name;
    private final int weight;

    RestorationTypeEnum(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public String getName() {
        return name;
    }
}
