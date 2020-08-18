package va.rit.teho.enums;

public enum RepairTypeEnum {
    CURRENT_REPAIR("Текущий"),
    AVG_REPAIR("Средний"),
    FULL_REPAIR("Капитальный"),
    LOSS("Безвозвратные потери");

    private final String name;

    RepairTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
