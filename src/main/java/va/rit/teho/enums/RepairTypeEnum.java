package va.rit.teho.enums;

public enum RepairTypeEnum {
    CURRENT_REPAIR("Текущий", true),
    AVG_REPAIR("Средний", true),
    FULL_REPAIR("Капитальный", false),
    LOSS("Безвозвратные потери", false);

    private final String name;
    private final boolean calculatable;

    RepairTypeEnum(String name, boolean calculatable) {
        this.name = name;
        this.calculatable = calculatable;
    }

    public boolean isCalculatable() {
        return calculatable;
    }

    public String getName() {
        return name;
    }
}
