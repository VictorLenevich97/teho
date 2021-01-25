package va.rit.teho.enums;

public enum RepairTypeEnum {
    CURRENT_REPAIR("Текущий", "ТР", true),
    AVG_REPAIR("Средний", "СР", true),
    FULL_REPAIR("Капитальный", "КР", false),
    LOSS("Безвозвратные потери", "БП", false);

    private final String fullName;
    private final String shortName;
    private final boolean calculatable;

    RepairTypeEnum(String name, String shortName, boolean calculatable) {
        this.fullName = name;
        this.shortName = shortName;
        this.calculatable = calculatable;
    }

    public boolean isCalculatable() {
        return calculatable;
    }

    public String getFullName() {
        return fullName;
    }

    public String getShortName() {
        return shortName;
    }
}
