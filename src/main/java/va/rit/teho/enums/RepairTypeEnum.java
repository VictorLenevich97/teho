package va.rit.teho.enums;

public enum RepairTypeEnum {
    CURRENT_REPAIR("Текущий", "ТР", true, true, true),
    AVG_REPAIR("Средний", "СР", true, true, true),
    FULL_REPAIR("Капитальный", "КР", false, true, false),
    LOSS("Безвозвратные потери", "БП", false, false, false);

    private final String fullName;
    private final String shortName;
    private final boolean calculatable;
    private final boolean repairable;
    private final boolean splitToIntervals;

    RepairTypeEnum(String name, String shortName, boolean calculatable, boolean repairable, boolean splitToIntervals) {
        this.fullName = name;
        this.shortName = shortName;
        this.calculatable = calculatable;
        this.repairable = repairable;
        this.splitToIntervals = splitToIntervals;
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

    public boolean isSplitToIntervals() {
        return splitToIntervals;
    }

    public boolean isRepairable() {
        return repairable;
    }
}
