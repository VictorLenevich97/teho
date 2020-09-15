package va.rit.teho.enums;

public enum RepairTypeEnum {
    CURRENT_REPAIR("Текущий", true),
    AVG_REPAIR("Средний", true),
    FULL_REPAIR("Капитальный", true),
    LOSS("Безвозвратные потери", false);

    private final String name;
    private final boolean repairable;

    RepairTypeEnum(String name, boolean repairable) {
        this.name = name;
        this.repairable = repairable;
    }

    public boolean isRepairable() {
        return repairable;
    }

    public String getName() {
        return name;
    }
}
